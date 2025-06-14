package de.oliver.fancylib.storage;

import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.DatabaseFactory;
import de.oliver.fancylib.storage.config.StorageConfig;
import de.oliver.fancylib.storage.database.DatabaseSchema;
import de.oliver.fancylib.storage.sync.DatabaseSynchronizer;
import de.oliver.fancylib.storage.sync.DataSynchronizer;
import de.oliver.fancylib.storage.sync.SyncableData;
import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Factory for creating storage instances based on configuration
 */
public class StorageFactory {

    private static final Map<String, Database> databases = new ConcurrentHashMap<>();
    private static final Map<String, DataSynchronizer<?>> synchronizers = new ConcurrentHashMap<>();

    /**
     * Creates a database instance based on configuration
     *
     * @param config the storage configuration
     * @return the database instance
     */
    public static Database createDatabase(StorageConfig config) {
        String key = generateDatabaseKey(config);
        
        return databases.computeIfAbsent(key, k -> {
            try {
                Database database = DatabaseFactory.createDatabase(config.getDatabaseConfig());
                
                if (database.connect()) {
                    // Initialize schema
                    DatabaseSchema schema = new DatabaseSchema(database);
                    if (schema.initializeSchema()) {
                        Bukkit.getLogger().info("Database initialized successfully: " + config.getDatabaseConfig().getType());
                        return database;
                    } else {
                        Bukkit.getLogger().severe("Failed to initialize database schema");
                        database.close();
                        return null;
                    }
                } else {
                    Bukkit.getLogger().severe("Failed to connect to database");
                    return null;
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("Error creating database: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Creates a data storage instance based on configuration
     *
     * @param config the storage configuration
     * @param storageClass the storage implementation class
     * @param dataType the data type identifier for synchronization
     * @param <T> the data type
     * @return the storage instance
     */
    @SuppressWarnings("unchecked")
    public static <T> DataStorage<T> createStorage(StorageConfig config, Class<? extends DataStorage<T>> storageClass, String dataType) {
        try {
            switch (config.getStorageType()) {
                case DATABASE -> {
                    Database database = createDatabase(config);
                    if (database == null) {
                        throw new RuntimeException("Failed to create database");
                    }
                    
                    // Create database storage instance using reflection
                    return storageClass.getConstructor(Database.class, String.class)
                            .newInstance(database, config.getServerId());
                }
                case YAML, JSON -> {
                    // Create file-based storage instance
                    return storageClass.getConstructor().newInstance();
                }
                case HYBRID -> {
                    // Create hybrid storage (database with YAML fallback)
                    Database database = createDatabase(config);
                    DataStorage<T> primaryStorage = storageClass.getConstructor(Database.class, String.class)
                            .newInstance(database, config.getServerId());
                    
                    // Create fallback storage
                    // This would need a hybrid storage implementation
                    return primaryStorage; // Simplified for now
                }
                default -> throw new IllegalArgumentException("Unsupported storage type: " + config.getStorageType());
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to create storage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create storage", e);
        }
    }

    /**
     * Creates a data synchronizer for cross-server sync
     *
     * @param config the storage configuration
     * @param storage the data storage instance
     * @param dataType the data type identifier
     * @param <T> the data type
     * @return the synchronizer instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends SyncableData> DataSynchronizer<T> createSynchronizer(
            StorageConfig config, DataStorage<T> storage, String dataType) {
        
        if (!config.isEnableSync()) {
            return null;
        }

        String key = generateSyncKey(config, dataType);
        
        return (DataSynchronizer<T>) synchronizers.computeIfAbsent(key, k -> {
            try {
                Database database = createDatabase(config);
                if (database == null) {
                    throw new RuntimeException("Failed to create database for synchronization");
                }
                
                DatabaseSynchronizer<T> synchronizer = new DatabaseSynchronizer<>(database, storage, dataType);
                synchronizer.setServerId(config.getServerId());
                synchronizer.setSyncInterval(config.getSyncIntervalSeconds());
                
                return synchronizer;
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to create synchronizer: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Creates a complete storage system with synchronization
     *
     * @param config the storage configuration
     * @param storageClass the storage implementation class
     * @param dataType the data type identifier
     * @param <T> the data type
     * @return the storage system
     */
    public static <T extends SyncableData> StorageSystem<T> createStorageSystem(
            StorageConfig config, Class<? extends DataStorage<T>> storageClass, String dataType) {
        
        DataStorage<T> storage = createStorage(config, storageClass, dataType);
        DataSynchronizer<T> synchronizer = createSynchronizer(config, storage, dataType);
        
        return new StorageSystem<>(storage, synchronizer, config);
    }

    /**
     * Closes all databases and synchronizers
     */
    public static void shutdown() {
        Bukkit.getLogger().info("Shutting down storage systems...");
        
        // Stop all synchronizers
        for (DataSynchronizer<?> synchronizer : synchronizers.values()) {
            try {
                synchronizer.stop();
            } catch (Exception e) {
                Bukkit.getLogger().warning("Error stopping synchronizer: " + e.getMessage());
            }
        }
        synchronizers.clear();
        
        // Close all databases
        for (Database database : databases.values()) {
            try {
                database.close();
            } catch (Exception e) {
                Bukkit.getLogger().warning("Error closing database: " + e.getMessage());
            }
        }
        databases.clear();
        
        Bukkit.getLogger().info("Storage systems shutdown complete");
    }

    private static String generateDatabaseKey(StorageConfig config) {
        return config.getDatabaseConfig().getType() + ":" + 
               config.getDatabaseConfig().getHost() + ":" + 
               config.getDatabaseConfig().getPort() + ":" + 
               config.getDatabaseConfig().getDatabase() + ":" +
               config.getDatabaseConfig().getFilePath();
    }

    private static String generateSyncKey(StorageConfig config, String dataType) {
        return generateDatabaseKey(config) + ":" + dataType + ":" + config.getServerId();
    }

    /**
     * Complete storage system with storage and synchronization
     */
    public static class StorageSystem<T extends SyncableData> {
        private final DataStorage<T> storage;
        private final DataSynchronizer<T> synchronizer;
        private final StorageConfig config;

        public StorageSystem(DataStorage<T> storage, DataSynchronizer<T> synchronizer, StorageConfig config) {
            this.storage = storage;
            this.synchronizer = synchronizer;
            this.config = config;
        }

        public DataStorage<T> getStorage() { return storage; }
        public DataSynchronizer<T> getSynchronizer() { return synchronizer; }
        public StorageConfig getConfig() { return config; }

        /**
         * Initializes the storage system
         *
         * @return true if successful
         */
        public boolean initialize() {
            try {
                if (!storage.initialize()) {
                    Bukkit.getLogger().severe("Failed to initialize storage");
                    return false;
                }

                if (synchronizer != null && config.isEnableSync()) {
                    if (!synchronizer.start()) {
                        Bukkit.getLogger().severe("Failed to start synchronizer");
                        return false;
                    }
                }

                return true;
            } catch (Exception e) {
                Bukkit.getLogger().severe("Error initializing storage system: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Shuts down the storage system
         *
         * @return true if successful
         */
        public boolean shutdown() {
            try {
                boolean success = true;

                if (synchronizer != null) {
                    if (!synchronizer.stop()) {
                        Bukkit.getLogger().warning("Failed to stop synchronizer");
                        success = false;
                    }
                }

                if (!storage.close()) {
                    Bukkit.getLogger().warning("Failed to close storage");
                    success = false;
                }

                return success;
            } catch (Exception e) {
                Bukkit.getLogger().severe("Error shutting down storage system: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Checks if the storage system is healthy
         *
         * @return true if healthy
         */
        public boolean isHealthy() {
            boolean storageHealthy = storage.isHealthy();
            boolean syncHealthy = synchronizer == null || synchronizer.isRunning();
            return storageHealthy && syncHealthy;
        }

        /**
         * Gets system status information
         *
         * @return status string
         */
        public String getStatus() {
            StringBuilder status = new StringBuilder();
            status.append("Storage: ").append(storage.getStorageType()).append(" (")
                  .append(storage.isHealthy() ? "healthy" : "unhealthy").append(")");
            
            if (synchronizer != null) {
                status.append(", Sync: ").append(synchronizer.isRunning() ? "running" : "stopped")
                      .append(" (").append(synchronizer.getStats()).append(")");
            } else {
                status.append(", Sync: disabled");
            }
            
            return status.toString();
        }
    }
}
