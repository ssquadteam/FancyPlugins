package de.oliver.fancyholograms.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.storage.DataStorage;
import de.oliver.fancylib.storage.StorageType;
import de.oliver.fancylib.storage.sync.SyncableData;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.data.property.Visibility;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Database storage implementation for holograms
 */
public class DatabaseHologramStorage implements DataStorage<HologramData> {

    private final Database database;
    private final Gson gson;
    private final String serverId;

    public DatabaseHologramStorage(Database database, String serverId) {
        this.database = database;
        this.serverId = serverId;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public boolean save(HologramData data) {
        if (data == null) return false;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try {
                // Update sync metadata
                updateSyncMetadata(data);

                // Save main hologram data
                if (!saveMainHologramData(conn, data)) {
                    conn.rollback();
                    return false;
                }

                // Save type-specific data
                if (!saveHologramTypeData(conn, data)) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to save hologram " + data.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveMainHologramData(Connection conn, HologramData data) throws SQLException {
        String sql = """
            INSERT INTO holograms (
                name, type, world_name, x, y, z, yaw, pitch,
                visibility_distance, visibility_type, persistent, linked_npc_name, file_path,
                last_modified, version, last_modified_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                type = VALUES(type), world_name = VALUES(world_name), x = VALUES(x), y = VALUES(y), z = VALUES(z),
                yaw = VALUES(yaw), pitch = VALUES(pitch), visibility_distance = VALUES(visibility_distance),
                visibility_type = VALUES(visibility_type), persistent = VALUES(persistent),
                linked_npc_name = VALUES(linked_npc_name), file_path = VALUES(file_path),
                last_modified = VALUES(last_modified), version = VALUES(version), last_modified_by = VALUES(last_modified_by)
            """;

        // Adjust SQL for different database types
        if (database.getDatabaseType().equals("postgresql")) {
            sql = sql.replace("ON DUPLICATE KEY UPDATE", "ON CONFLICT (name) DO UPDATE SET");
            sql = sql.replaceAll("VALUES\\(([^)]+)\\)", "$1");
        } else if (database.getDatabaseType().equals("sqlite")) {
            sql = sql.replace("ON DUPLICATE KEY UPDATE", "ON CONFLICT (name) DO UPDATE SET");
            sql = sql.replaceAll("VALUES\\(([^)]+)\\)", "$1");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Location loc = data.getLocation();

            stmt.setString(1, data.getName());
            stmt.setString(2, data.getType().name());
            stmt.setString(3, loc.getWorld().getName());
            stmt.setDouble(4, loc.getX());
            stmt.setDouble(5, loc.getY());
            stmt.setDouble(6, loc.getZ());
            stmt.setFloat(7, loc.getYaw());
            stmt.setFloat(8, loc.getPitch());
            stmt.setInt(9, data.getVisibilityDistance());
            stmt.setString(10, data.getVisibility().name());
            stmt.setBoolean(11, data.isPersistent());
            stmt.setString(12, data.getLinkedNpcName());
            stmt.setString(13, data.getFilePath());
            stmt.setObject(14, Instant.now());
            stmt.setLong(15, getNextVersion(data.getName()));
            stmt.setString(16, serverId);

            return stmt.executeUpdate() > 0;
        }
    }

    private boolean saveHologramTypeData(Connection conn, HologramData data) throws SQLException {
        // Delete existing type-specific data
        deleteHologramTypeData(conn, data.getName());

        // Save new type-specific data
        return switch (data.getType()) {
            case TEXT -> saveTextHologramData(conn, (TextHologramData) data);
            case ITEM -> saveItemHologramData(conn, (ItemHologramData) data);
            case BLOCK -> saveBlockHologramData(conn, (BlockHologramData) data);
        };
    }

    private void deleteHologramTypeData(Connection conn, String hologramName) throws SQLException {
        String[] deleteSqls = {
            "DELETE FROM hologram_text_data WHERE hologram_name = ?",
            "DELETE FROM hologram_item_data WHERE hologram_name = ?",
            "DELETE FROM hologram_block_data WHERE hologram_name = ?"
        };

        for (String sql : deleteSqls) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, hologramName);
                stmt.executeUpdate();
            }
        }
    }

    private boolean saveTextHologramData(Connection conn, TextHologramData data) throws SQLException {
        String sql = """
            INSERT INTO hologram_text_data (
                hologram_name, text_lines, background_color, text_alignment, line_width, text_shadow, see_through
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.getName());
            stmt.setString(2, gson.toJson(data.getTextLines()));
            stmt.setString(3, data.getBackgroundColor() != null ? data.getBackgroundColor().toString() : null);
            stmt.setString(4, data.getTextAlignment().name());
            stmt.setInt(5, data.getLineWidth());
            stmt.setBoolean(6, data.isTextShadow());
            stmt.setBoolean(7, data.isSeeThrough());

            return stmt.executeUpdate() > 0;
        }
    }

    private boolean saveItemHologramData(Connection conn, ItemHologramData data) throws SQLException {
        String sql = "INSERT INTO hologram_item_data (hologram_name, item_data) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.getName());
            stmt.setString(2, gson.toJson(data.getItemStack()));

            return stmt.executeUpdate() > 0;
        }
    }

    private boolean saveBlockHologramData(Connection conn, BlockHologramData data) throws SQLException {
        String sql = "INSERT INTO hologram_block_data (hologram_name, block_data) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.getName());
            stmt.setString(2, gson.toJson(data.getBlockData()));

            return stmt.executeUpdate() > 0;
        }
    }

    private void updateSyncMetadata(HologramData data) {
        if (data instanceof SyncableData syncable) {
            syncable.setLastModified(Instant.now());
            syncable.setLastModifiedBy(serverId);
            syncable.setVersion(getNextVersion(data.getName()));
            syncable.setDirty(false);
        }
    }

    private long getNextVersion(String hologramName) {
        String sql = "SELECT COALESCE(MAX(version), 0) + 1 FROM holograms WHERE name = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hologramName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to get next version for hologram " + hologramName + ": " + e.getMessage());
        }
        return 1;
    }

    @Override
    public CompletableFuture<Boolean> saveAsync(HologramData data) {
        return CompletableFuture.supplyAsync(() -> save(data));
    }

    @Override
    public boolean saveBatch(Collection<HologramData> data) {
        if (data == null || data.isEmpty()) return true;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try {
                for (HologramData hologramData : data) {
                    updateSyncMetadata(hologramData);
                    
                    if (!saveMainHologramData(conn, hologramData) ||
                        !saveHologramTypeData(conn, hologramData)) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to save hologram batch: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> saveBatchAsync(Collection<HologramData> data) {
        return CompletableFuture.supplyAsync(() -> saveBatch(data));
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }

    @Override
    public boolean initialize() {
        return database.isConnected();
    }

    @Override
    public boolean close() {
        return database.close();
    }

    @Override
    public boolean isHealthy() {
        return database.testConnection();
    }

    @Override
    public HologramData load(String name) {
        String sql = """
            SELECT name, type, world_name, x, y, z, yaw, pitch,
                   visibility_distance, visibility_type, persistent, linked_npc_name, file_path,
                   last_modified, version, last_modified_by
            FROM holograms WHERE name = ?
            """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createHologramFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to load hologram " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompletableFuture<HologramData> loadAsync(String name) {
        return CompletableFuture.supplyAsync(() -> load(name));
    }

    @Override
    public Collection<HologramData> loadAll() {
        return loadAll(null);
    }

    @Override
    public CompletableFuture<Collection<HologramData>> loadAllAsync() {
        return CompletableFuture.supplyAsync(this::loadAll);
    }

    @Override
    public Collection<HologramData> loadAll(String world) {
        List<HologramData> holograms = new ArrayList<>();

        String sql = world != null ?
            "SELECT * FROM holograms WHERE world_name = ? ORDER BY name" :
            "SELECT * FROM holograms ORDER BY name";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (world != null) {
                stmt.setString(1, world);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HologramData hologramData = createHologramFromResultSet(rs);
                    if (hologramData != null) {
                        holograms.add(hologramData);
                    }
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to load holograms" +
                (world != null ? " for world " + world : "") + ": " + e.getMessage());
            e.printStackTrace();
        }

        return holograms;
    }

    @Override
    public CompletableFuture<Collection<HologramData>> loadAllAsync(String world) {
        return CompletableFuture.supplyAsync(() -> loadAll(world));
    }

    private HologramData createHologramFromResultSet(ResultSet rs) throws SQLException {
        String worldName = rs.getString("world_name");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            FancyHologramsPlugin.get().getFancyLogger().warn("World " + worldName + " not found for hologram " + rs.getString("name"));
            return null;
        }

        Location location = new Location(
            world,
            rs.getDouble("x"),
            rs.getDouble("y"),
            rs.getDouble("z"),
            rs.getFloat("yaw"),
            rs.getFloat("pitch")
        );

        String hologramName = rs.getString("name");
        HologramType type = HologramType.valueOf(rs.getString("type"));

        // Create the appropriate hologram data type
        DisplayHologramData hologramData = switch (type) {
            case TEXT -> {
                TextHologramData textData = new TextHologramData(hologramName, location);
                loadTextHologramData(textData);
                yield textData;
            }
            case ITEM -> {
                ItemHologramData itemData = new ItemHologramData(hologramName, location);
                loadItemHologramData(itemData);
                yield itemData;
            }
            case BLOCK -> {
                BlockHologramData blockData = new BlockHologramData(hologramName, location);
                loadBlockHologramData(blockData);
                yield blockData;
            }
        };

        // Set common properties
        hologramData.setVisibilityDistance(rs.getInt("visibility_distance"));
        hologramData.setVisibility(Visibility.valueOf(rs.getString("visibility_type")));
        hologramData.setPersistent(rs.getBoolean("persistent"));
        hologramData.setLinkedNpcName(rs.getString("linked_npc_name"));
        hologramData.setFilePath(rs.getString("file_path"));

        return hologramData;
    }

    private void loadTextHologramData(TextHologramData textData) {
        String sql = """
            SELECT text_lines, background_color, text_alignment, line_width, text_shadow, see_through
            FROM hologram_text_data WHERE hologram_name = ?
            """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, textData.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Parse text lines from JSON
                    String textLinesJson = rs.getString("text_lines");
                    List<String> textLines = gson.fromJson(textLinesJson, List.class);
                    textData.setTextLines(textLines);

                    // Set other properties
                    String backgroundColorStr = rs.getString("background_color");
                    if (backgroundColorStr != null) {
                        // Parse background color if needed
                        // textData.setBackgroundColor(Color.valueOf(backgroundColorStr));
                    }

                    textData.setTextAlignment(TextHologramData.TextAlignment.valueOf(rs.getString("text_alignment")));
                    textData.setLineWidth(rs.getInt("line_width"));
                    textData.setTextShadow(rs.getBoolean("text_shadow"));
                    textData.setSeeThrough(rs.getBoolean("see_through"));
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to load text data for hologram " + textData.getName() + ": " + e.getMessage());
        }
    }

    private void loadItemHologramData(ItemHologramData itemData) {
        String sql = "SELECT item_data FROM hologram_item_data WHERE hologram_name = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemData.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String itemDataJson = rs.getString("item_data");
                    ItemStack itemStack = gson.fromJson(itemDataJson, ItemStack.class);
                    itemData.setItemStack(itemStack);
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to load item data for hologram " + itemData.getName() + ": " + e.getMessage());
        }
    }

    private void loadBlockHologramData(BlockHologramData blockData) {
        String sql = "SELECT block_data FROM hologram_block_data WHERE hologram_name = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, blockData.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String blockDataJson = rs.getString("block_data");
                    // Parse block data from JSON - this depends on how BlockData is serialized
                    // blockData.setBlockData(gson.fromJson(blockDataJson, BlockData.class));
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to load block data for hologram " + blockData.getName() + ": " + e.getMessage());
        }
    }

    @Override
    public boolean delete(HologramData data) {
        return data != null && delete(data.getName());
    }

    @Override
    public CompletableFuture<Boolean> deleteAsync(HologramData data) {
        return CompletableFuture.supplyAsync(() -> delete(data));
    }

    @Override
    public boolean delete(String name) {
        if (name == null || name.isEmpty()) return false;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try {
                // Delete from all related tables (foreign keys should handle this, but being explicit)
                String[] deleteSqls = {
                    "DELETE FROM hologram_text_data WHERE hologram_name = ?",
                    "DELETE FROM hologram_item_data WHERE hologram_name = ?",
                    "DELETE FROM hologram_block_data WHERE hologram_name = ?",
                    "DELETE FROM holograms WHERE name = ?"
                };

                for (String sql : deleteSqls) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.executeUpdate();
                    }
                }

                // Update sync metadata to mark as deleted
                String syncSql = "INSERT INTO sync_metadata (data_type, data_id, server_id, last_sync, sync_version) VALUES (?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE last_sync = VALUES(last_sync), sync_version = VALUES(sync_version)";

                if (database.getDatabaseType().equals("postgresql")) {
                    syncSql = syncSql.replace("ON DUPLICATE KEY UPDATE", "ON CONFLICT (data_type, data_id, server_id) DO UPDATE SET");
                    syncSql = syncSql.replaceAll("VALUES\\(([^)]+)\\)", "$1");
                } else if (database.getDatabaseType().equals("sqlite")) {
                    syncSql = syncSql.replace("ON DUPLICATE KEY UPDATE", "ON CONFLICT (data_type, data_id, server_id) DO UPDATE SET");
                    syncSql = syncSql.replaceAll("VALUES\\(([^)]+)\\)", "$1");
                }

                try (PreparedStatement stmt = conn.prepareStatement(syncSql)) {
                    stmt.setString(1, "HOLOGRAM");
                    stmt.setString(2, name);
                    stmt.setString(3, serverId);
                    stmt.setObject(4, Instant.now());
                    stmt.setLong(5, -1); // -1 indicates deletion
                    stmt.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to delete hologram " + name + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> deleteAsync(String name) {
        return CompletableFuture.supplyAsync(() -> delete(name));
    }

    @Override
    public boolean exists(String name) {
        if (name == null || name.isEmpty()) return false;

        String sql = "SELECT 1 FROM holograms WHERE name = ? LIMIT 1";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to check existence of hologram " + name + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> existsAsync(String name) {
        return CompletableFuture.supplyAsync(() -> exists(name));
    }

    /**
     * Gets holograms that have been modified since the given timestamp
     *
     * @param since the timestamp to check from
     * @return collection of modified holograms
     */
    public Collection<HologramData> getModifiedSince(Instant since) {
        List<HologramData> holograms = new ArrayList<>();

        String sql = "SELECT * FROM holograms WHERE last_modified > ? ORDER BY last_modified";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, since);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HologramData hologramData = createHologramFromResultSet(rs);
                    if (hologramData != null) {
                        holograms.add(hologramData);
                    }
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to get modified holograms: " + e.getMessage());
            e.printStackTrace();
        }

        return holograms;
    }

    /**
     * Gets holograms that have been deleted since the given timestamp
     *
     * @param since the timestamp to check from
     * @return collection of deleted hologram names
     */
    public Collection<String> getDeletedSince(Instant since) {
        List<String> deletedNames = new ArrayList<>();

        String sql = "SELECT data_id FROM sync_metadata WHERE data_type = 'HOLOGRAM' AND last_sync > ? AND sync_version = -1";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, since);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deletedNames.add(rs.getString("data_id"));
                }
            }
        } catch (SQLException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to get deleted holograms: " + e.getMessage());
            e.printStackTrace();
        }

        return deletedNames;
    }
}
