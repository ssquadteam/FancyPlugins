package de.oliver.fancylib.storage.database;

import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.DatabaseConfig;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Database schema management for FancyPlugins
 */
public class DatabaseSchema {

    private final Database database;
    private final DatabaseConfig.DatabaseType databaseType;

    public DatabaseSchema(Database database) {
        this.database = database;
        this.databaseType = DatabaseConfig.DatabaseType.fromString(database.getDatabaseType());
    }

    /**
     * Initializes the complete database schema
     *
     * @return true if successful, false otherwise
     */
    public boolean initializeSchema() {
        try {
            // Create tables in order (respecting foreign key dependencies)
            createNpcTables();
            createHologramTables();
            createSyncTables();
            
            Bukkit.getLogger().info("Database schema initialized successfully");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to initialize database schema: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates NPC-related tables
     */
    private void createNpcTables() {
        // Main NPCs table
        String createNpcsTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS npcs (
                    id VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    creator VARCHAR(36) NOT NULL,
                    display_name TEXT,
                    world_name VARCHAR(255) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT NOT NULL,
                    pitch FLOAT NOT NULL,
                    entity_type VARCHAR(50) NOT NULL DEFAULT 'PLAYER',
                    show_in_tab BOOLEAN NOT NULL DEFAULT FALSE,
                    spawn_entity BOOLEAN NOT NULL DEFAULT TRUE,
                    collidable BOOLEAN NOT NULL DEFAULT TRUE,
                    glowing BOOLEAN NOT NULL DEFAULT FALSE,
                    glowing_color VARCHAR(20) DEFAULT 'WHITE',
                    turn_to_player BOOLEAN NOT NULL DEFAULT FALSE,
                    turn_to_player_distance INT DEFAULT -1,
                    interaction_cooldown FLOAT DEFAULT 0,
                    scale_factor FLOAT DEFAULT 1,
                    visibility_distance INT DEFAULT -1,
                    mirror_skin BOOLEAN NOT NULL DEFAULT FALSE,
                    skin_identifier TEXT,
                    skin_variant VARCHAR(20) DEFAULT 'CLASSIC',
                    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    version BIGINT DEFAULT 1,
                    last_modified_by VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_name (name),
                    INDEX idx_creator (creator),
                    INDEX idx_world (world_name),
                    INDEX idx_last_modified (last_modified)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS npcs (
                    id VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    creator VARCHAR(36) NOT NULL,
                    display_name TEXT,
                    world_name VARCHAR(255) NOT NULL,
                    x DOUBLE PRECISION NOT NULL,
                    y DOUBLE PRECISION NOT NULL,
                    z DOUBLE PRECISION NOT NULL,
                    yaw REAL NOT NULL,
                    pitch REAL NOT NULL,
                    entity_type VARCHAR(50) NOT NULL DEFAULT 'PLAYER',
                    show_in_tab BOOLEAN NOT NULL DEFAULT FALSE,
                    spawn_entity BOOLEAN NOT NULL DEFAULT TRUE,
                    collidable BOOLEAN NOT NULL DEFAULT TRUE,
                    glowing BOOLEAN NOT NULL DEFAULT FALSE,
                    glowing_color VARCHAR(20) DEFAULT 'WHITE',
                    turn_to_player BOOLEAN NOT NULL DEFAULT FALSE,
                    turn_to_player_distance INTEGER DEFAULT -1,
                    interaction_cooldown REAL DEFAULT 0,
                    scale_factor REAL DEFAULT 1,
                    visibility_distance INTEGER DEFAULT -1,
                    mirror_skin BOOLEAN NOT NULL DEFAULT FALSE,
                    skin_identifier TEXT,
                    skin_variant VARCHAR(20) DEFAULT 'CLASSIC',
                    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    version BIGINT DEFAULT 1,
                    last_modified_by VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                CREATE INDEX IF NOT EXISTS idx_npcs_name ON npcs(name);
                CREATE INDEX IF NOT EXISTS idx_npcs_creator ON npcs(creator);
                CREATE INDEX IF NOT EXISTS idx_npcs_world ON npcs(world_name);
                CREATE INDEX IF NOT EXISTS idx_npcs_last_modified ON npcs(last_modified);
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS npcs (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    creator TEXT NOT NULL,
                    display_name TEXT,
                    world_name TEXT NOT NULL,
                    x REAL NOT NULL,
                    y REAL NOT NULL,
                    z REAL NOT NULL,
                    yaw REAL NOT NULL,
                    pitch REAL NOT NULL,
                    entity_type TEXT NOT NULL DEFAULT 'PLAYER',
                    show_in_tab INTEGER NOT NULL DEFAULT 0,
                    spawn_entity INTEGER NOT NULL DEFAULT 1,
                    collidable INTEGER NOT NULL DEFAULT 1,
                    glowing INTEGER NOT NULL DEFAULT 0,
                    glowing_color TEXT DEFAULT 'WHITE',
                    turn_to_player INTEGER NOT NULL DEFAULT 0,
                    turn_to_player_distance INTEGER DEFAULT -1,
                    interaction_cooldown REAL DEFAULT 0,
                    scale_factor REAL DEFAULT 1,
                    visibility_distance INTEGER DEFAULT -1,
                    mirror_skin INTEGER NOT NULL DEFAULT 0,
                    skin_identifier TEXT,
                    skin_variant TEXT DEFAULT 'CLASSIC',
                    last_modified DATETIME DEFAULT CURRENT_TIMESTAMP,
                    version INTEGER DEFAULT 1,
                    last_modified_by TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                CREATE INDEX IF NOT EXISTS idx_npcs_name ON npcs(name);
                CREATE INDEX IF NOT EXISTS idx_npcs_creator ON npcs(creator);
                CREATE INDEX IF NOT EXISTS idx_npcs_world ON npcs(world_name);
                CREATE INDEX IF NOT EXISTS idx_npcs_last_modified ON npcs(last_modified);
                """;
        };

        database.executeNonQuery(createNpcsTable);

        // NPC Equipment table
        createNpcEquipmentTable();
        
        // NPC Actions table
        createNpcActionsTable();
        
        // NPC Attributes table
        createNpcAttributesTable();
    }

    private void createNpcEquipmentTable() {
        String createEquipmentTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS npc_equipment (
                    npc_id VARCHAR(36) NOT NULL,
                    slot VARCHAR(20) NOT NULL,
                    item_data LONGTEXT NOT NULL,
                    PRIMARY KEY (npc_id, slot),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS npc_equipment (
                    npc_id VARCHAR(36) NOT NULL,
                    slot VARCHAR(20) NOT NULL,
                    item_data TEXT NOT NULL,
                    PRIMARY KEY (npc_id, slot),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                );
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS npc_equipment (
                    npc_id TEXT NOT NULL,
                    slot TEXT NOT NULL,
                    item_data TEXT NOT NULL,
                    PRIMARY KEY (npc_id, slot),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                );
                """;
        };

        database.executeNonQuery(createEquipmentTable);
    }

    private void createNpcActionsTable() {
        String createActionsTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS npc_actions (
                    npc_id VARCHAR(36) NOT NULL,
                    trigger_type VARCHAR(50) NOT NULL,
                    action_order INT NOT NULL,
                    action_type VARCHAR(100) NOT NULL,
                    action_value TEXT,
                    PRIMARY KEY (npc_id, trigger_type, action_order),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE,
                    INDEX idx_trigger (trigger_type)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS npc_actions (
                    npc_id VARCHAR(36) NOT NULL,
                    trigger_type VARCHAR(50) NOT NULL,
                    action_order INTEGER NOT NULL,
                    action_type VARCHAR(100) NOT NULL,
                    action_value TEXT,
                    PRIMARY KEY (npc_id, trigger_type, action_order),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                );
                CREATE INDEX IF NOT EXISTS idx_npc_actions_trigger ON npc_actions(trigger_type);
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS npc_actions (
                    npc_id TEXT NOT NULL,
                    trigger_type TEXT NOT NULL,
                    action_order INTEGER NOT NULL,
                    action_type TEXT NOT NULL,
                    action_value TEXT,
                    PRIMARY KEY (npc_id, trigger_type, action_order),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                );
                CREATE INDEX IF NOT EXISTS idx_npc_actions_trigger ON npc_actions(trigger_type);
                """;
        };

        database.executeNonQuery(createActionsTable);
    }

    private void createNpcAttributesTable() {
        String createAttributesTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS npc_attributes (
                    npc_id VARCHAR(36) NOT NULL,
                    attribute_name VARCHAR(100) NOT NULL,
                    attribute_value TEXT,
                    PRIMARY KEY (npc_id, attribute_name),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS npc_attributes (
                    npc_id VARCHAR(36) NOT NULL,
                    attribute_name VARCHAR(100) NOT NULL,
                    attribute_value TEXT,
                    PRIMARY KEY (npc_id, attribute_name),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                );
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS npc_attributes (
                    npc_id TEXT NOT NULL,
                    attribute_name TEXT NOT NULL,
                    attribute_value TEXT,
                    PRIMARY KEY (npc_id, attribute_name),
                    FOREIGN KEY (npc_id) REFERENCES npcs(id) ON DELETE CASCADE
                );
                """;
        };

        database.executeNonQuery(createAttributesTable);
    }

    /**
     * Creates hologram-related tables
     */
    private void createHologramTables() {
        // Main holograms table
        String createHologramsTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS holograms (
                    name VARCHAR(255) PRIMARY KEY,
                    type VARCHAR(20) NOT NULL,
                    world_name VARCHAR(255) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT DEFAULT 0,
                    pitch FLOAT DEFAULT 0,
                    visibility_distance INT DEFAULT -1,
                    visibility_type VARCHAR(20) DEFAULT 'ALL',
                    persistent BOOLEAN NOT NULL DEFAULT TRUE,
                    linked_npc_name VARCHAR(255),
                    file_path VARCHAR(500),
                    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    version BIGINT DEFAULT 1,
                    last_modified_by VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_world (world_name),
                    INDEX idx_type (type),
                    INDEX idx_last_modified (last_modified)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS holograms (
                    name VARCHAR(255) PRIMARY KEY,
                    type VARCHAR(20) NOT NULL,
                    world_name VARCHAR(255) NOT NULL,
                    x DOUBLE PRECISION NOT NULL,
                    y DOUBLE PRECISION NOT NULL,
                    z DOUBLE PRECISION NOT NULL,
                    yaw REAL DEFAULT 0,
                    pitch REAL DEFAULT 0,
                    visibility_distance INTEGER DEFAULT -1,
                    visibility_type VARCHAR(20) DEFAULT 'ALL',
                    persistent BOOLEAN NOT NULL DEFAULT TRUE,
                    linked_npc_name VARCHAR(255),
                    file_path VARCHAR(500),
                    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    version BIGINT DEFAULT 1,
                    last_modified_by VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                CREATE INDEX IF NOT EXISTS idx_holograms_world ON holograms(world_name);
                CREATE INDEX IF NOT EXISTS idx_holograms_type ON holograms(type);
                CREATE INDEX IF NOT EXISTS idx_holograms_last_modified ON holograms(last_modified);
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS holograms (
                    name TEXT PRIMARY KEY,
                    type TEXT NOT NULL,
                    world_name TEXT NOT NULL,
                    x REAL NOT NULL,
                    y REAL NOT NULL,
                    z REAL NOT NULL,
                    yaw REAL DEFAULT 0,
                    pitch REAL DEFAULT 0,
                    visibility_distance INTEGER DEFAULT -1,
                    visibility_type TEXT DEFAULT 'ALL',
                    persistent INTEGER NOT NULL DEFAULT 1,
                    linked_npc_name TEXT,
                    file_path TEXT,
                    last_modified DATETIME DEFAULT CURRENT_TIMESTAMP,
                    version INTEGER DEFAULT 1,
                    last_modified_by TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                CREATE INDEX IF NOT EXISTS idx_holograms_world ON holograms(world_name);
                CREATE INDEX IF NOT EXISTS idx_holograms_type ON holograms(type);
                CREATE INDEX IF NOT EXISTS idx_holograms_last_modified ON holograms(last_modified);
                """;
        };

        database.executeNonQuery(createHologramsTable);

        // Hologram type-specific data tables
        createHologramDataTables();
    }

    private void createHologramDataTables() {
        // Text hologram data
        String createTextDataTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS hologram_text_data (
                    hologram_name VARCHAR(255) PRIMARY KEY,
                    text_lines JSON NOT NULL,
                    background_color VARCHAR(20),
                    text_alignment VARCHAR(20) DEFAULT 'CENTER',
                    line_width INT DEFAULT 200,
                    text_shadow BOOLEAN DEFAULT TRUE,
                    see_through BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS hologram_text_data (
                    hologram_name VARCHAR(255) PRIMARY KEY,
                    text_lines JSONB NOT NULL,
                    background_color VARCHAR(20),
                    text_alignment VARCHAR(20) DEFAULT 'CENTER',
                    line_width INTEGER DEFAULT 200,
                    text_shadow BOOLEAN DEFAULT TRUE,
                    see_through BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                );
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS hologram_text_data (
                    hologram_name TEXT PRIMARY KEY,
                    text_lines TEXT NOT NULL,
                    background_color TEXT,
                    text_alignment TEXT DEFAULT 'CENTER',
                    line_width INTEGER DEFAULT 200,
                    text_shadow INTEGER DEFAULT 1,
                    see_through INTEGER DEFAULT 0,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                );
                """;
        };

        database.executeNonQuery(createTextDataTable);

        // Item hologram data
        String createItemDataTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS hologram_item_data (
                    hologram_name VARCHAR(255) PRIMARY KEY,
                    item_data LONGTEXT NOT NULL,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS hologram_item_data (
                    hologram_name VARCHAR(255) PRIMARY KEY,
                    item_data TEXT NOT NULL,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                );
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS hologram_item_data (
                    hologram_name TEXT PRIMARY KEY,
                    item_data TEXT NOT NULL,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                );
                """;
        };

        database.executeNonQuery(createItemDataTable);

        // Block hologram data
        String createBlockDataTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS hologram_block_data (
                    hologram_name VARCHAR(255) PRIMARY KEY,
                    block_data LONGTEXT NOT NULL,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS hologram_block_data (
                    hologram_name VARCHAR(255) PRIMARY KEY,
                    block_data TEXT NOT NULL,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                );
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS hologram_block_data (
                    hologram_name TEXT PRIMARY KEY,
                    block_data TEXT NOT NULL,
                    FOREIGN KEY (hologram_name) REFERENCES holograms(name) ON DELETE CASCADE
                );
                """;
        };

        database.executeNonQuery(createBlockDataTable);
    }

    /**
     * Creates synchronization-related tables
     */
    private void createSyncTables() {
        // Sync metadata table for tracking changes
        String createSyncMetaTable = switch (databaseType) {
            case MYSQL -> """
                CREATE TABLE IF NOT EXISTS sync_metadata (
                    data_type VARCHAR(50) NOT NULL,
                    data_id VARCHAR(255) NOT NULL,
                    server_id VARCHAR(255) NOT NULL,
                    last_sync TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    sync_version BIGINT DEFAULT 1,
                    PRIMARY KEY (data_type, data_id, server_id),
                    INDEX idx_last_sync (last_sync),
                    INDEX idx_data_type (data_type)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
            case POSTGRESQL -> """
                CREATE TABLE IF NOT EXISTS sync_metadata (
                    data_type VARCHAR(50) NOT NULL,
                    data_id VARCHAR(255) NOT NULL,
                    server_id VARCHAR(255) NOT NULL,
                    last_sync TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    sync_version BIGINT DEFAULT 1,
                    PRIMARY KEY (data_type, data_id, server_id)
                );
                CREATE INDEX IF NOT EXISTS idx_sync_metadata_last_sync ON sync_metadata(last_sync);
                CREATE INDEX IF NOT EXISTS idx_sync_metadata_data_type ON sync_metadata(data_type);
                """;
            case SQLITE -> """
                CREATE TABLE IF NOT EXISTS sync_metadata (
                    data_type TEXT NOT NULL,
                    data_id TEXT NOT NULL,
                    server_id TEXT NOT NULL,
                    last_sync DATETIME DEFAULT CURRENT_TIMESTAMP,
                    sync_version INTEGER DEFAULT 1,
                    PRIMARY KEY (data_type, data_id, server_id)
                );
                CREATE INDEX IF NOT EXISTS idx_sync_metadata_last_sync ON sync_metadata(last_sync);
                CREATE INDEX IF NOT EXISTS idx_sync_metadata_data_type ON sync_metadata(data_type);
                """;
        };

        database.executeNonQuery(createSyncMetaTable);
    }
}
