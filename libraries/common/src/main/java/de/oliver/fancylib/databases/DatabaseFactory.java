package de.oliver.fancylib.databases;

/**
 * Factory class for creating database instances
 */
public class DatabaseFactory {

    /**
     * Creates a database instance based on the configuration
     *
     * @param config the database configuration
     * @return the database instance
     * @throws IllegalArgumentException if the database type is not supported
     */
    public static Database createDatabase(DatabaseConfig config) {
        return switch (config.getType()) {
            case MYSQL -> new MySqlDatabase(config);
            case POSTGRESQL -> new PostgreSqlDatabase(config);
            case SQLITE -> new SqliteDatabase(config);
        };
    }

    /**
     * Creates a database instance with default configuration for the specified type
     *
     * @param type the database type
     * @return the database instance with default configuration
     */
    public static Database createDatabase(DatabaseConfig.DatabaseType type) {
        DatabaseConfig config = switch (type) {
            case MYSQL -> DatabaseConfig.builder()
                    .type(DatabaseConfig.DatabaseType.MYSQL)
                    .host("localhost")
                    .port(3306)
                    .database("fancyplugins")
                    .username("root")
                    .password("")
                    .build();
            case POSTGRESQL -> DatabaseConfig.builder()
                    .type(DatabaseConfig.DatabaseType.POSTGRESQL)
                    .host("localhost")
                    .port(5432)
                    .database("fancyplugins")
                    .username("postgres")
                    .password("")
                    .build();
            case SQLITE -> DatabaseConfig.builder()
                    .type(DatabaseConfig.DatabaseType.SQLITE)
                    .filePath("plugins/FancyPlugins/database.db")
                    .build();
        };
        
        return createDatabase(config);
    }
}
