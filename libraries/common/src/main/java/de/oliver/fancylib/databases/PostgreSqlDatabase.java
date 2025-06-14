package de.oliver.fancylib.databases;

/**
 * PostgreSQL database implementation
 */
public class PostgreSqlDatabase extends MySqlDatabase {

    public PostgreSqlDatabase(DatabaseConfig config) {
        super(config);
        if (config.getType() != DatabaseConfig.DatabaseType.POSTGRESQL) {
            throw new IllegalArgumentException("PostgreSQL database requires PostgreSQL configuration");
        }
    }

    @Override
    public String getDatabaseType() {
        return "postgresql";
    }

    @Override
    public boolean initializeSchema() {
        // PostgreSQL-specific schema initialization if needed
        return super.initializeSchema();
    }
}
