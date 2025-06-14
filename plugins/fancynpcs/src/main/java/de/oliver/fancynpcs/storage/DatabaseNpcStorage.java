package de.oliver.fancynpcs.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.storage.DataStorage;
import de.oliver.fancylib.storage.StorageType;
import de.oliver.fancylib.storage.sync.SyncableData;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.api.utils.SkinData;
import de.oliver.fancynpcs.api.utils.NpcAttribute;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database storage implementation for NPCs
 */
public class DatabaseNpcStorage implements DataStorage<NpcData> {

    private final Database database;
    private final Gson gson;
    private final String serverId;

    public DatabaseNpcStorage(Database database, String serverId) {
        this.database = database;
        this.serverId = serverId;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public boolean save(NpcData data) {
        if (data == null) return false;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try {
                // Update sync metadata
                updateSyncMetadata(data);

                // Save main NPC data
                if (!saveMainNpcData(conn, data)) {
                    conn.rollback();
                    return false;
                }

                // Save equipment
                if (!saveNpcEquipment(conn, data)) {
                    conn.rollback();
                    return false;
                }

                // Save actions
                if (!saveNpcActions(conn, data)) {
                    conn.rollback();
                    return false;
                }

                // Save attributes
                if (!saveNpcAttributes(conn, data)) {
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
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save NPC " + data.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveMainNpcData(Connection conn, NpcData data) throws SQLException {
        String sql = """
            INSERT INTO npcs (
                id, name, creator, display_name, world_name, x, y, z, yaw, pitch,
                entity_type, show_in_tab, spawn_entity, collidable, glowing, glowing_color,
                turn_to_player, turn_to_player_distance, interaction_cooldown, scale_factor,
                visibility_distance, mirror_skin, skin_identifier, skin_variant,
                last_modified, version, last_modified_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                name = VALUES(name), creator = VALUES(creator), display_name = VALUES(display_name),
                world_name = VALUES(world_name), x = VALUES(x), y = VALUES(y), z = VALUES(z),
                yaw = VALUES(yaw), pitch = VALUES(pitch), entity_type = VALUES(entity_type),
                show_in_tab = VALUES(show_in_tab), spawn_entity = VALUES(spawn_entity),
                collidable = VALUES(collidable), glowing = VALUES(glowing), glowing_color = VALUES(glowing_color),
                turn_to_player = VALUES(turn_to_player), turn_to_player_distance = VALUES(turn_to_player_distance),
                interaction_cooldown = VALUES(interaction_cooldown), scale_factor = VALUES(scale_factor),
                visibility_distance = VALUES(visibility_distance), mirror_skin = VALUES(mirror_skin),
                skin_identifier = VALUES(skin_identifier), skin_variant = VALUES(skin_variant),
                last_modified = VALUES(last_modified), version = VALUES(version), last_modified_by = VALUES(last_modified_by)
            """;

        // Adjust SQL for different database types
        if (database.getDatabaseType().equals("postgresql")) {
            sql = sql.replace("ON DUPLICATE KEY UPDATE", "ON CONFLICT (id) DO UPDATE SET");
            sql = sql.replaceAll("VALUES\\(([^)]+)\\)", "$1");
        } else if (database.getDatabaseType().equals("sqlite")) {
            sql = sql.replace("ON DUPLICATE KEY UPDATE", "ON CONFLICT (id) DO UPDATE SET");
            sql = sql.replaceAll("VALUES\\(([^)]+)\\)", "$1");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Location loc = data.getLocation();
            SkinData skin = data.getSkinData();

            stmt.setString(1, data.getId());
            stmt.setString(2, data.getName());
            stmt.setString(3, data.getCreator().toString());
            stmt.setString(4, data.getDisplayName());
            stmt.setString(5, loc.getWorld().getName());
            stmt.setDouble(6, loc.getX());
            stmt.setDouble(7, loc.getY());
            stmt.setDouble(8, loc.getZ());
            stmt.setFloat(9, loc.getYaw());
            stmt.setFloat(10, loc.getPitch());
            stmt.setString(11, data.getType().name());
            stmt.setBoolean(12, data.isShowInTab());
            stmt.setBoolean(13, data.isSpawnEntity());
            stmt.setBoolean(14, data.isCollidable());
            stmt.setBoolean(15, data.isGlowing());
            stmt.setString(16, data.getGlowingColor().toString());
            stmt.setBoolean(17, data.isTurnToPlayer());
            stmt.setInt(18, data.getTurnToPlayerDistance());
            stmt.setFloat(19, data.getInteractionCooldown());
            stmt.setFloat(20, data.getScale());
            stmt.setInt(21, data.getVisibilityDistance());
            stmt.setBoolean(22, data.isMirrorSkin());
            stmt.setString(23, skin != null ? skin.getIdentifier() : null);
            stmt.setString(24, skin != null ? skin.getVariant().name() : null);
            stmt.setObject(25, Instant.now());
            stmt.setLong(26, getNextVersion(data.getId()));
            stmt.setString(27, serverId);

            return stmt.executeUpdate() > 0;
        }
    }

    private boolean saveNpcEquipment(Connection conn, NpcData data) throws SQLException {
        // Delete existing equipment
        String deleteSql = "DELETE FROM npc_equipment WHERE npc_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, data.getId());
            stmt.executeUpdate();
        }

        // Insert new equipment
        if (data.getEquipment() != null && !data.getEquipment().isEmpty()) {
            String insertSql = "INSERT INTO npc_equipment (npc_id, slot, item_data) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
                    stmt.setString(1, data.getId());
                    stmt.setString(2, entry.getKey().name());
                    stmt.setString(3, gson.toJson(entry.getValue()));
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        return true;
    }

    private boolean saveNpcActions(Connection conn, NpcData data) throws SQLException {
        // Delete existing actions
        String deleteSql = "DELETE FROM npc_actions WHERE npc_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, data.getId());
            stmt.executeUpdate();
        }

        // Insert new actions
        if (data.getActions() != null && !data.getActions().isEmpty()) {
            String insertSql = "INSERT INTO npc_actions (npc_id, trigger_type, action_order, action_type, action_value) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<ActionTrigger, List<NpcAction.NpcActionData>> entry : data.getActions().entrySet()) {
                    for (NpcAction.NpcActionData actionData : entry.getValue()) {
                        stmt.setString(1, data.getId());
                        stmt.setString(2, entry.getKey().name());
                        stmt.setInt(3, actionData.order());
                        stmt.setString(4, actionData.action().getName());
                        stmt.setString(5, actionData.value());
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            }
        }

        return true;
    }

    private boolean saveNpcAttributes(Connection conn, NpcData data) throws SQLException {
        // Delete existing attributes
        String deleteSql = "DELETE FROM npc_attributes WHERE npc_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, data.getId());
            stmt.executeUpdate();
        }

        // Insert new attributes
        if (data.getAttributes() != null && !data.getAttributes().isEmpty()) {
            String insertSql = "INSERT INTO npc_attributes (npc_id, attribute_name, attribute_value) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<NpcAttribute, String> entry : data.getAttributes().entrySet()) {
                    stmt.setString(1, data.getId());
                    stmt.setString(2, entry.getKey().getName());
                    stmt.setString(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        return true;
    }

    private void updateSyncMetadata(NpcData data) {
        if (data instanceof SyncableData syncable) {
            syncable.setLastModified(Instant.now());
            syncable.setLastModifiedBy(serverId);
            syncable.setVersion(getNextVersion(data.getId()));
            syncable.setDirty(false);
        }
    }

    private long getNextVersion(String npcId) {
        String sql = "SELECT COALESCE(MAX(version), 0) + 1 FROM npcs WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, npcId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to get next version for NPC " + npcId + ": " + e.getMessage());
        }
        return 1;
    }

    @Override
    public CompletableFuture<Boolean> saveAsync(NpcData data) {
        return CompletableFuture.supplyAsync(() -> save(data));
    }

    @Override
    public boolean saveBatch(Collection<NpcData> data) {
        if (data == null || data.isEmpty()) return true;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try {
                for (NpcData npcData : data) {
                    updateSyncMetadata(npcData);
                    
                    if (!saveMainNpcData(conn, npcData) ||
                        !saveNpcEquipment(conn, npcData) ||
                        !saveNpcActions(conn, npcData) ||
                        !saveNpcAttributes(conn, npcData)) {
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
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save NPC batch: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> saveBatchAsync(Collection<NpcData> data) {
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
    public NpcData load(String id) {
        String sql = """
            SELECT id, name, creator, display_name, world_name, x, y, z, yaw, pitch,
                   entity_type, show_in_tab, spawn_entity, collidable, glowing, glowing_color,
                   turn_to_player, turn_to_player_distance, interaction_cooldown, scale_factor,
                   visibility_distance, mirror_skin, skin_identifier, skin_variant,
                   last_modified, version, last_modified_by
            FROM npcs WHERE id = ?
            """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NpcData npcData = createNpcFromResultSet(rs);
                    if (npcData != null) {
                        loadNpcEquipment(npcData);
                        loadNpcActions(npcData);
                        loadNpcAttributes(npcData);
                    }
                    return npcData;
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load NPC " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompletableFuture<NpcData> loadAsync(String id) {
        return CompletableFuture.supplyAsync(() -> load(id));
    }

    @Override
    public Collection<NpcData> loadAll() {
        return loadAll(null);
    }

    @Override
    public CompletableFuture<Collection<NpcData>> loadAllAsync() {
        return CompletableFuture.supplyAsync(this::loadAll);
    }

    @Override
    public Collection<NpcData> loadAll(String world) {
        List<NpcData> npcs = new ArrayList<>();

        String sql = world != null ?
            "SELECT * FROM npcs WHERE world_name = ? ORDER BY name" :
            "SELECT * FROM npcs ORDER BY name";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (world != null) {
                stmt.setString(1, world);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NpcData npcData = createNpcFromResultSet(rs);
                    if (npcData != null) {
                        loadNpcEquipment(npcData);
                        loadNpcActions(npcData);
                        loadNpcAttributes(npcData);
                        npcs.add(npcData);
                    }
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load NPCs" +
                (world != null ? " for world " + world : "") + ": " + e.getMessage());
            e.printStackTrace();
        }

        return npcs;
    }

    @Override
    public CompletableFuture<Collection<NpcData>> loadAllAsync(String world) {
        return CompletableFuture.supplyAsync(() -> loadAll(world));
    }

    private NpcData createNpcFromResultSet(ResultSet rs) throws SQLException {
        String worldName = rs.getString("world_name");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            FancyNpcs.getInstance().getFancyLogger().warn("World " + worldName + " not found for NPC " + rs.getString("id"));
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

        SkinData skinData = null;
        String skinIdentifier = rs.getString("skin_identifier");
        if (skinIdentifier != null && !skinIdentifier.isEmpty()) {
            String skinVariant = rs.getString("skin_variant");
            skinData = new SkinData(skinIdentifier, SkinData.SkinVariant.valueOf(skinVariant));
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(rs.getString("entity_type"));
        } catch (IllegalArgumentException e) {
            entityType = EntityType.PLAYER;
        }

        NamedTextColor glowingColor;
        try {
            glowingColor = NamedTextColor.NAMES.value(rs.getString("glowing_color"));
        } catch (Exception e) {
            glowingColor = NamedTextColor.WHITE;
        }

        return new NpcData(
            rs.getString("id"),
            rs.getString("name"),
            UUID.fromString(rs.getString("creator")),
            rs.getString("display_name"),
            skinData,
            location,
            rs.getBoolean("show_in_tab"),
            rs.getBoolean("spawn_entity"),
            rs.getBoolean("collidable"),
            rs.getBoolean("glowing"),
            glowingColor,
            entityType,
            new ConcurrentHashMap<>(), // equipment - loaded separately
            rs.getBoolean("turn_to_player"),
            rs.getInt("turn_to_player_distance"),
            null, // onClick - not stored in database
            new ConcurrentHashMap<>(), // actions - loaded separately
            rs.getFloat("interaction_cooldown"),
            rs.getFloat("scale_factor"),
            rs.getInt("visibility_distance"),
            new ConcurrentHashMap<>(), // attributes - loaded separately
            rs.getBoolean("mirror_skin")
        );
    }

    private void loadNpcEquipment(NpcData npcData) {
        String sql = "SELECT slot, item_data FROM npc_equipment WHERE npc_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, npcData.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        NpcEquipmentSlot slot = NpcEquipmentSlot.valueOf(rs.getString("slot"));
                        ItemStack item = gson.fromJson(rs.getString("item_data"), ItemStack.class);
                        npcData.addEquipment(slot, item);
                    } catch (Exception e) {
                        FancyNpcs.getInstance().getFancyLogger().warn("Failed to load equipment for NPC " + npcData.getId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load equipment for NPC " + npcData.getId() + ": " + e.getMessage());
        }
    }

    private void loadNpcActions(NpcData npcData) {
        String sql = "SELECT trigger_type, action_order, action_type, action_value FROM npc_actions WHERE npc_id = ? ORDER BY trigger_type, action_order";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, npcData.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        ActionTrigger trigger = ActionTrigger.valueOf(rs.getString("trigger_type"));
                        String actionType = rs.getString("action_type");
                        String actionValue = rs.getString("action_value");
                        int order = rs.getInt("action_order");

                        // Get the action from the action manager
                        NpcAction action = FancyNpcs.getInstance().getActionManager().getAction(actionType);
                        if (action != null) {
                            NpcAction.NpcActionData actionData = new NpcAction.NpcActionData(action, actionValue, order);
                            npcData.getActions().computeIfAbsent(trigger, k -> new ArrayList<>()).add(actionData);
                        }
                    } catch (Exception e) {
                        FancyNpcs.getInstance().getFancyLogger().warn("Failed to load action for NPC " + npcData.getId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load actions for NPC " + npcData.getId() + ": " + e.getMessage());
        }
    }

    private void loadNpcAttributes(NpcData npcData) {
        String sql = "SELECT attribute_name, attribute_value FROM npc_attributes WHERE npc_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, npcData.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        String attributeName = rs.getString("attribute_name");
                        String attributeValue = rs.getString("attribute_value");

                        // Get the attribute from the attribute manager
                        NpcAttribute attribute = FancyNpcs.getInstance().getAttributeManager().getAttribute(attributeName);
                        if (attribute != null) {
                            npcData.getAttributes().put(attribute, attributeValue);
                        }
                    } catch (Exception e) {
                        FancyNpcs.getInstance().getFancyLogger().warn("Failed to load attribute for NPC " + npcData.getId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load attributes for NPC " + npcData.getId() + ": " + e.getMessage());
        }
    }

    @Override
    public boolean delete(NpcData data) {
        return data != null && delete(data.getId());
    }

    @Override
    public CompletableFuture<Boolean> deleteAsync(NpcData data) {
        return CompletableFuture.supplyAsync(() -> delete(data));
    }

    @Override
    public boolean delete(String id) {
        if (id == null || id.isEmpty()) return false;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try {
                // Delete from all related tables (foreign keys should handle this, but being explicit)
                String[] deleteSqls = {
                    "DELETE FROM npc_equipment WHERE npc_id = ?",
                    "DELETE FROM npc_actions WHERE npc_id = ?",
                    "DELETE FROM npc_attributes WHERE npc_id = ?",
                    "DELETE FROM npcs WHERE id = ?"
                };

                for (String sql : deleteSqls) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, id);
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
                    stmt.setString(1, "NPC");
                    stmt.setString(2, id);
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
            FancyNpcs.getInstance().getFancyLogger().error("Failed to delete NPC " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return CompletableFuture.supplyAsync(() -> delete(id));
    }

    @Override
    public boolean exists(String id) {
        if (id == null || id.isEmpty()) return false;

        String sql = "SELECT 1 FROM npcs WHERE id = ? LIMIT 1";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to check existence of NPC " + id + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> existsAsync(String id) {
        return CompletableFuture.supplyAsync(() -> exists(id));
    }

    /**
     * Gets NPCs that have been modified since the given timestamp
     *
     * @param since the timestamp to check from
     * @return collection of modified NPCs
     */
    public Collection<NpcData> getModifiedSince(Instant since) {
        List<NpcData> npcs = new ArrayList<>();

        String sql = "SELECT * FROM npcs WHERE last_modified > ? ORDER BY last_modified";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, since);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NpcData npcData = createNpcFromResultSet(rs);
                    if (npcData != null) {
                        loadNpcEquipment(npcData);
                        loadNpcActions(npcData);
                        loadNpcAttributes(npcData);
                        npcs.add(npcData);
                    }
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to get modified NPCs: " + e.getMessage());
            e.printStackTrace();
        }

        return npcs;
    }

    /**
     * Gets NPCs that have been deleted since the given timestamp
     *
     * @param since the timestamp to check from
     * @return collection of deleted NPC IDs
     */
    public Collection<String> getDeletedSince(Instant since) {
        List<String> deletedIds = new ArrayList<>();

        String sql = "SELECT data_id FROM sync_metadata WHERE data_type = 'NPC' AND last_sync > ? AND sync_version = -1";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, since);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deletedIds.add(rs.getString("data_id"));
                }
            }
        } catch (SQLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to get deleted NPCs: " + e.getMessage());
            e.printStackTrace();
        }

        return deletedIds;
    }
}
