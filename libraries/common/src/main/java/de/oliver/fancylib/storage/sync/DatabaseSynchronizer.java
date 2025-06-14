package de.oliver.fancylib.storage.sync;

import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.storage.DataStorage;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Database-based synchronization implementation
 */
public class DatabaseSynchronizer<T extends SyncableData> implements DataSynchronizer<T> {

    private final Database database;
    private final DataStorage<T> storage;
    private final String dataType;
    private final ScheduledExecutorService scheduler;
    private final Set<SyncListener<T>> listeners;
    private final SyncStats stats;
    
    private String serverId;
    private boolean running = false;
    private ScheduledFuture<?> syncTask;
    private long syncIntervalSeconds = 30; // Default 30 seconds
    private Instant lastSyncTime = Instant.now();

    public DatabaseSynchronizer(Database database, DataStorage<T> storage, String dataType) {
        this.database = database;
        this.storage = storage;
        this.dataType = dataType;
        this.scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r, "FancyPlugins-Sync-" + dataType);
            thread.setDaemon(true);
            return thread;
        });
        this.listeners = ConcurrentHashMap.newKeySet();
        this.stats = new SyncStats();
        this.serverId = generateServerId();
    }

    @Override
    public boolean start() {
        if (running) return true;

        try {
            // Initialize sync metadata table if needed
            initializeSyncMetadata();

            // Start periodic sync task
            syncTask = scheduler.scheduleAtFixedRate(
                this::performPeriodicSync,
                syncIntervalSeconds,
                syncIntervalSeconds,
                TimeUnit.SECONDS
            );

            running = true;
            Bukkit.getLogger().info("Started synchronization for " + dataType + " (server: " + serverId + ")");
            return true;

        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to start synchronization for " + dataType + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean stop() {
        if (!running) return true;

        try {
            if (syncTask != null) {
                syncTask.cancel(false);
            }

            scheduler.shutdown();
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }

            running = false;
            Bukkit.getLogger().info("Stopped synchronization for " + dataType);
            return true;

        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to stop synchronization for " + dataType + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public CompletableFuture<Boolean> syncToOthers(T data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Save to storage (which updates sync metadata)
                boolean saved = storage.save(data);
                if (saved) {
                    stats.incrementSent();
                    notifyListeners(listener -> listener.onDataSent(data));
                }
                return saved;
            } catch (Exception e) {
                stats.incrementErrors();
                notifyListeners(listener -> listener.onError(e, data));
                return false;
            }
        }, scheduler);
    }

    @Override
    public CompletableFuture<Boolean> syncBatchToOthers(Collection<T> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean saved = storage.saveBatch(data);
                if (saved) {
                    stats.setTotalSent(stats.getTotalSent() + data.size());
                    data.forEach(item -> notifyListeners(listener -> listener.onDataSent(item)));
                }
                return saved;
            } catch (Exception e) {
                stats.incrementErrors();
                notifyListeners(listener -> listener.onError(e, null));
                return false;
            }
        }, scheduler);
    }

    @Override
    public CompletableFuture<Collection<T>> pullChanges() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Collection<T> changes = getChangesFromOtherServers();
                stats.setTotalReceived(stats.getTotalReceived() + changes.size());
                changes.forEach(item -> notifyListeners(listener -> listener.onDataReceived(item, item.getLastModifiedBy())));
                return changes;
            } catch (Exception e) {
                stats.incrementErrors();
                notifyListeners(listener -> listener.onError(e, null));
                return Collections.emptyList();
            }
        }, scheduler);
    }

    @Override
    public CompletableFuture<T> pullChanges(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                T data = storage.load(id);
                if (data != null && isFromOtherServer(data)) {
                    stats.incrementReceived();
                    notifyListeners(listener -> listener.onDataReceived(data, data.getLastModifiedBy()));
                }
                return data;
            } catch (Exception e) {
                stats.incrementErrors();
                notifyListeners(listener -> listener.onError(e, null));
                return null;
            }
        }, scheduler);
    }

    @Override
    public CompletableFuture<Boolean> fullSync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Pull all changes from other servers
                Collection<T> changes = pullChanges().get();
                
                // Process each change
                for (T remoteData : changes) {
                    T localData = storage.load(remoteData.getId());
                    
                    if (localData == null) {
                        // New data from another server
                        storage.save(remoteData);
                    } else {
                        // Merge changes
                        SyncableData.MergeResult result = localData.merge(remoteData);
                        if (result != SyncableData.MergeResult.FAILED) {
                            storage.save(localData);
                            if (result == SyncableData.MergeResult.CONFLICT_RESOLVED || 
                                result == SyncableData.MergeResult.CONFLICT_MANUAL) {
                                stats.incrementConflicts();
                                notifyListeners(listener -> listener.onConflict(localData, remoteData, result));
                            }
                        }
                    }
                }

                lastSyncTime = Instant.now();
                stats.setLastSyncTime(lastSyncTime.toEpochMilli());
                return true;

            } catch (Exception e) {
                stats.incrementErrors();
                notifyListeners(listener -> listener.onError(e, null));
                return false;
            }
        }, scheduler);
    }

    private void performPeriodicSync() {
        if (!running) return;

        try {
            fullSync().get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Periodic sync failed for " + dataType + ": " + e.getMessage());
            stats.incrementErrors();
        }
    }

    private Collection<T> getChangesFromOtherServers() {
        List<T> changes = new ArrayList<>();
        
        String sql = """
            SELECT DISTINCT sm.data_id 
            FROM sync_metadata sm
            WHERE sm.data_type = ? 
            AND sm.last_sync > ? 
            AND sm.server_id != ?
            ORDER BY sm.last_sync
            """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dataType);
            stmt.setObject(2, lastSyncTime);
            stmt.setString(3, serverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dataId = rs.getString("data_id");
                    T data = storage.load(dataId);
                    if (data != null && isFromOtherServer(data)) {
                        changes.add(data);
                    }
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get changes from other servers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return changes;
    }

    private boolean isFromOtherServer(T data) {
        return data.getLastModifiedBy() != null && !data.getLastModifiedBy().equals(serverId);
    }

    private void initializeSyncMetadata() throws SQLException {
        // This is handled by DatabaseSchema, but we can add any additional initialization here
    }

    private String generateServerId() {
        // Generate a unique server ID based on server properties
        String serverName = Bukkit.getServer().getName();
        String serverPort = String.valueOf(Bukkit.getServer().getPort());
        return serverName + "-" + serverPort + "-" + System.currentTimeMillis();
    }

    private void notifyListeners(java.util.function.Consumer<SyncListener<T>> action) {
        for (SyncListener<T> listener : listeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Error notifying sync listener: " + e.getMessage());
            }
        }
    }

    @Override
    public void addSyncListener(SyncListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeSyncListener(SyncListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    @Override
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public SyncStats getStats() {
        return stats;
    }

    /**
     * Sets the synchronization interval
     *
     * @param intervalSeconds the interval in seconds
     */
    public void setSyncInterval(long intervalSeconds) {
        this.syncIntervalSeconds = intervalSeconds;
        
        // Restart sync task with new interval if running
        if (running && syncTask != null) {
            syncTask.cancel(false);
            syncTask = scheduler.scheduleAtFixedRate(
                this::performPeriodicSync,
                intervalSeconds,
                intervalSeconds,
                TimeUnit.SECONDS
            );
        }
    }

    /**
     * Gets the synchronization interval
     *
     * @return the interval in seconds
     */
    public long getSyncInterval() {
        return syncIntervalSeconds;
    }
}
