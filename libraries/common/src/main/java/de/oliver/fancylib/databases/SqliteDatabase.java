package de.oliver.fancylib.databases;

import org.bukkit.Bukkit;

import java.io.File;

/**
 * SQLite database implementation
 */
public class SqliteDatabase extends MySqlDatabase {

    public SqliteDatabase(DatabaseConfig config) {
        super(config);
        if (config.getType() != DatabaseConfig.DatabaseType.SQLITE) {
            throw new IllegalArgumentException("SQLite database requires SQLite configuration");
        }
    }

    @Override
    public boolean connect() {
        // Ensure the database file directory exists
        File file = new File(config.getFilePath());
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Bukkit.getLogger().warning("Could not create database directory: " + parentDir.getPath());
                return false;
            }
        }

        return super.connect();
    }

    @Override
    public String getDatabaseType() {
        return "sqlite";
    }

    @Override
    public boolean initializeSchema() {
        // SQLite-specific schema initialization if needed
        // Enable foreign keys for SQLite
        executeNonQuery("PRAGMA foreign_keys = ON;");
        return super.initializeSchema();
    }
}
