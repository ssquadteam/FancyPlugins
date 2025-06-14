package de.oliver.fancylib.storage.config;

import de.oliver.fancylib.databases.DatabaseConfig;
import de.oliver.fancylib.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Configuration class for storage settings
 */
public class StorageConfig {

    private StorageType storageType;
    private DatabaseConfig databaseConfig;
    private boolean enableSync;
    private long syncIntervalSeconds;
    private String serverId;
    private boolean autoMigrate;
    private boolean createBackups;
    private String backupPath;

    // Default values
    private static final StorageType DEFAULT_STORAGE_TYPE = StorageType.YAML;
    private static final boolean DEFAULT_ENABLE_SYNC = false;
    private static final long DEFAULT_SYNC_INTERVAL = 30;
    private static final boolean DEFAULT_AUTO_MIGRATE = false;
    private static final boolean DEFAULT_CREATE_BACKUPS = true;
    private static final String DEFAULT_BACKUP_PATH = "plugins/FancyPlugins/backups";

    public StorageConfig() {
        // Initialize with defaults
        this.storageType = DEFAULT_STORAGE_TYPE;
        this.enableSync = DEFAULT_ENABLE_SYNC;
        this.syncIntervalSeconds = DEFAULT_SYNC_INTERVAL;
        this.autoMigrate = DEFAULT_AUTO_MIGRATE;
        this.createBackups = DEFAULT_CREATE_BACKUPS;
        this.backupPath = DEFAULT_BACKUP_PATH;
        this.databaseConfig = DatabaseConfig.builder().build(); // SQLite default
    }

    /**
     * Loads configuration from a Bukkit FileConfiguration
     *
     * @param config the configuration to load from
     */
    public void loadFromConfig(FileConfiguration config) {
        // Storage type
        String storageTypeStr = config.getString("storage.type", DEFAULT_STORAGE_TYPE.getIdentifier());
        try {
            this.storageType = StorageType.fromString(storageTypeStr);
        } catch (IllegalArgumentException e) {
            this.storageType = DEFAULT_STORAGE_TYPE;
        }

        // Synchronization settings
        this.enableSync = config.getBoolean("storage.sync.enabled", DEFAULT_ENABLE_SYNC);
        this.syncIntervalSeconds = config.getLong("storage.sync.interval-seconds", DEFAULT_SYNC_INTERVAL);
        this.serverId = config.getString("storage.sync.server-id", generateDefaultServerId());

        // Migration settings
        this.autoMigrate = config.getBoolean("storage.migration.auto-migrate", DEFAULT_AUTO_MIGRATE);
        this.createBackups = config.getBoolean("storage.migration.create-backups", DEFAULT_CREATE_BACKUPS);
        this.backupPath = config.getString("storage.migration.backup-path", DEFAULT_BACKUP_PATH);

        // Database configuration
        loadDatabaseConfig(config);
    }

    private void loadDatabaseConfig(FileConfiguration config) {
        ConfigurationSection dbSection = config.getConfigurationSection("storage.database");
        if (dbSection == null) {
            // Create default database section
            dbSection = config.createSection("storage.database");
            setDefaultDatabaseConfig(dbSection);
        }

        DatabaseConfig.DatabaseType dbType;
        try {
            dbType = DatabaseConfig.DatabaseType.fromString(dbSection.getString("type", "sqlite"));
        } catch (IllegalArgumentException e) {
            dbType = DatabaseConfig.DatabaseType.SQLITE;
        }

        DatabaseConfig.Builder builder = DatabaseConfig.builder().type(dbType);

        switch (dbType) {
            case MYSQL -> {
                builder.host(dbSection.getString("host", "localhost"))
                       .port(dbSection.getInt("port", 3306))
                       .database(dbSection.getString("database", "fancyplugins"))
                       .username(dbSection.getString("username", "root"))
                       .password(dbSection.getString("password", ""))
                       .useSSL(dbSection.getBoolean("ssl", false));
            }
            case POSTGRESQL -> {
                builder.host(dbSection.getString("host", "localhost"))
                       .port(dbSection.getInt("port", 5432))
                       .database(dbSection.getString("database", "fancyplugins"))
                       .username(dbSection.getString("username", "postgres"))
                       .password(dbSection.getString("password", ""))
                       .useSSL(dbSection.getBoolean("ssl", false));
            }
            case SQLITE -> {
                builder.filePath(dbSection.getString("file-path", "plugins/FancyPlugins/database.db"));
            }
        }

        // Connection pool settings
        ConfigurationSection poolSection = dbSection.getConfigurationSection("connection-pool");
        if (poolSection != null) {
            builder.maxPoolSize(poolSection.getInt("max-pool-size", 10))
                   .minPoolSize(poolSection.getInt("min-pool-size", 2))
                   .connectionTimeout(poolSection.getLong("connection-timeout", 30000))
                   .idleTimeout(poolSection.getLong("idle-timeout", 600000))
                   .maxLifetime(poolSection.getLong("max-lifetime", 1800000));
        }

        this.databaseConfig = builder.build();
    }

    /**
     * Saves configuration to a Bukkit FileConfiguration
     *
     * @param config the configuration to save to
     */
    public void saveToConfig(FileConfiguration config) {
        // Storage type
        config.set("storage.type", storageType.getIdentifier());

        // Synchronization settings
        config.set("storage.sync.enabled", enableSync);
        config.set("storage.sync.interval-seconds", syncIntervalSeconds);
        config.set("storage.sync.server-id", serverId);

        // Migration settings
        config.set("storage.migration.auto-migrate", autoMigrate);
        config.set("storage.migration.create-backups", createBackups);
        config.set("storage.migration.backup-path", backupPath);

        // Database configuration
        saveDatabaseConfig(config);

        // Add comments
        addConfigComments(config);
    }

    private void saveDatabaseConfig(FileConfiguration config) {
        config.set("storage.database.type", databaseConfig.getType().getIdentifier());

        switch (databaseConfig.getType()) {
            case MYSQL, POSTGRESQL -> {
                config.set("storage.database.host", databaseConfig.getHost());
                config.set("storage.database.port", databaseConfig.getPort());
                config.set("storage.database.database", databaseConfig.getDatabase());
                config.set("storage.database.username", databaseConfig.getUsername());
                config.set("storage.database.password", databaseConfig.getPassword());
                config.set("storage.database.ssl", databaseConfig.isUseSSL());
            }
            case SQLITE -> {
                config.set("storage.database.file-path", databaseConfig.getFilePath());
            }
        }

        // Connection pool settings
        config.set("storage.database.connection-pool.max-pool-size", databaseConfig.getMaxPoolSize());
        config.set("storage.database.connection-pool.min-pool-size", databaseConfig.getMinPoolSize());
        config.set("storage.database.connection-pool.connection-timeout", databaseConfig.getConnectionTimeout());
        config.set("storage.database.connection-pool.idle-timeout", databaseConfig.getIdleTimeout());
        config.set("storage.database.connection-pool.max-lifetime", databaseConfig.getMaxLifetime());
    }

    private void setDefaultDatabaseConfig(ConfigurationSection dbSection) {
        dbSection.set("type", "sqlite");
        dbSection.set("file-path", "plugins/FancyPlugins/database.db");
        
        // MySQL/PostgreSQL defaults (commented out)
        dbSection.set("host", "localhost");
        dbSection.set("port", 3306);
        dbSection.set("database", "fancyplugins");
        dbSection.set("username", "root");
        dbSection.set("password", "");
        dbSection.set("ssl", false);

        // Connection pool defaults
        dbSection.set("connection-pool.max-pool-size", 10);
        dbSection.set("connection-pool.min-pool-size", 2);
        dbSection.set("connection-pool.connection-timeout", 30000);
        dbSection.set("connection-pool.idle-timeout", 600000);
        dbSection.set("connection-pool.max-lifetime", 1800000);
    }

    private void addConfigComments(FileConfiguration config) {
        // This would add comments if the configuration system supports it
        // For now, we'll rely on the plugin to add comments when saving
    }

    private String generateDefaultServerId() {
        return "server-" + System.currentTimeMillis();
    }

    // Getters and setters
    public StorageType getStorageType() { return storageType; }
    public void setStorageType(StorageType storageType) { this.storageType = storageType; }

    public DatabaseConfig getDatabaseConfig() { return databaseConfig; }
    public void setDatabaseConfig(DatabaseConfig databaseConfig) { this.databaseConfig = databaseConfig; }

    public boolean isEnableSync() { return enableSync; }
    public void setEnableSync(boolean enableSync) { this.enableSync = enableSync; }

    public long getSyncIntervalSeconds() { return syncIntervalSeconds; }
    public void setSyncIntervalSeconds(long syncIntervalSeconds) { this.syncIntervalSeconds = syncIntervalSeconds; }

    public String getServerId() { return serverId; }
    public void setServerId(String serverId) { this.serverId = serverId; }

    public boolean isAutoMigrate() { return autoMigrate; }
    public void setAutoMigrate(boolean autoMigrate) { this.autoMigrate = autoMigrate; }

    public boolean isCreateBackups() { return createBackups; }
    public void setCreateBackups(boolean createBackups) { this.createBackups = createBackups; }

    public String getBackupPath() { return backupPath; }
    public void setBackupPath(String backupPath) { this.backupPath = backupPath; }

    /**
     * Validates the configuration
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (storageType == null) return false;
        if (databaseConfig == null) return false;
        if (syncIntervalSeconds <= 0) return false;
        if (serverId == null || serverId.isEmpty()) return false;
        if (backupPath == null || backupPath.isEmpty()) return false;

        // Validate database config based on type
        switch (databaseConfig.getType()) {
            case MYSQL, POSTGRESQL -> {
                if (databaseConfig.getHost() == null || databaseConfig.getHost().isEmpty()) return false;
                if (databaseConfig.getDatabase() == null || databaseConfig.getDatabase().isEmpty()) return false;
                if (databaseConfig.getPort() <= 0 || databaseConfig.getPort() > 65535) return false;
            }
            case SQLITE -> {
                if (databaseConfig.getFilePath() == null || databaseConfig.getFilePath().isEmpty()) return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("StorageConfig{type=%s, sync=%s, interval=%ds, serverId='%s', autoMigrate=%s}",
                storageType, enableSync, syncIntervalSeconds, serverId, autoMigrate);
    }
}
