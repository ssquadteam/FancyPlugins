package de.oliver.fancylib.databases;

/**
 * Configuration class for database connections
 */
public class DatabaseConfig {
    
    private final DatabaseType type;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String filePath; // For SQLite
    private final int maxPoolSize;
    private final int minPoolSize;
    private final long connectionTimeout;
    private final long idleTimeout;
    private final long maxLifetime;
    private final boolean useSSL;
    private final String additionalProperties;

    private DatabaseConfig(Builder builder) {
        this.type = builder.type;
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.filePath = builder.filePath;
        this.maxPoolSize = builder.maxPoolSize;
        this.minPoolSize = builder.minPoolSize;
        this.connectionTimeout = builder.connectionTimeout;
        this.idleTimeout = builder.idleTimeout;
        this.maxLifetime = builder.maxLifetime;
        this.useSSL = builder.useSSL;
        this.additionalProperties = builder.additionalProperties;
    }

    public DatabaseType getType() { return type; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFilePath() { return filePath; }
    public int getMaxPoolSize() { return maxPoolSize; }
    public int getMinPoolSize() { return minPoolSize; }
    public long getConnectionTimeout() { return connectionTimeout; }
    public long getIdleTimeout() { return idleTimeout; }
    public long getMaxLifetime() { return maxLifetime; }
    public boolean isUseSSL() { return useSSL; }
    public String getAdditionalProperties() { return additionalProperties; }

    public String getJdbcUrl() {
        return switch (type) {
            case MYSQL -> String.format("jdbc:mysql://%s:%d/%s%s", 
                host, port, database, useSSL ? "?useSSL=true" : "?useSSL=false");
            case POSTGRESQL -> String.format("jdbc:postgresql://%s:%d/%s%s", 
                host, port, database, useSSL ? "?ssl=true" : "");
            case SQLITE -> String.format("jdbc:sqlite:%s", filePath);
        };
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DatabaseType type = DatabaseType.SQLITE;
        private String host = "localhost";
        private int port = 3306;
        private String database = "fancyplugins";
        private String username = "";
        private String password = "";
        private String filePath = "plugins/FancyPlugins/database.db";
        private int maxPoolSize = 10;
        private int minPoolSize = 2;
        private long connectionTimeout = 30000; // 30 seconds
        private long idleTimeout = 600000; // 10 minutes
        private long maxLifetime = 1800000; // 30 minutes
        private boolean useSSL = false;
        private String additionalProperties = "";

        public Builder type(DatabaseType type) { this.type = type; return this; }
        public Builder host(String host) { this.host = host; return this; }
        public Builder port(int port) { this.port = port; return this; }
        public Builder database(String database) { this.database = database; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder maxPoolSize(int maxPoolSize) { this.maxPoolSize = maxPoolSize; return this; }
        public Builder minPoolSize(int minPoolSize) { this.minPoolSize = minPoolSize; return this; }
        public Builder connectionTimeout(long connectionTimeout) { this.connectionTimeout = connectionTimeout; return this; }
        public Builder idleTimeout(long idleTimeout) { this.idleTimeout = idleTimeout; return this; }
        public Builder maxLifetime(long maxLifetime) { this.maxLifetime = maxLifetime; return this; }
        public Builder useSSL(boolean useSSL) { this.useSSL = useSSL; return this; }
        public Builder additionalProperties(String additionalProperties) { this.additionalProperties = additionalProperties; return this; }

        public DatabaseConfig build() {
            // Validate configuration
            if (type == DatabaseType.SQLITE && (filePath == null || filePath.isEmpty())) {
                throw new IllegalArgumentException("File path is required for SQLite database");
            }
            if ((type == DatabaseType.MYSQL || type == DatabaseType.POSTGRESQL) && 
                (host == null || host.isEmpty() || database == null || database.isEmpty())) {
                throw new IllegalArgumentException("Host and database are required for " + type + " database");
            }
            
            return new DatabaseConfig(this);
        }
    }

    public enum DatabaseType {
        MYSQL("mysql", 3306),
        POSTGRESQL("postgresql", 5432),
        SQLITE("sqlite", 0);

        private final String identifier;
        private final int defaultPort;

        DatabaseType(String identifier, int defaultPort) {
            this.identifier = identifier;
            this.defaultPort = defaultPort;
        }

        public String getIdentifier() { return identifier; }
        public int getDefaultPort() { return defaultPort; }

        public static DatabaseType fromString(String type) {
            for (DatabaseType dbType : values()) {
                if (dbType.identifier.equalsIgnoreCase(type)) {
                    return dbType;
                }
            }
            throw new IllegalArgumentException("Unknown database type: " + type);
        }
    }
}
