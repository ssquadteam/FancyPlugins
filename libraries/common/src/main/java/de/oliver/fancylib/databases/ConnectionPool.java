package de.oliver.fancylib.databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Database connection pool implementation using HikariCP
 */
public class ConnectionPool {
    
    private HikariDataSource dataSource;
    private final DatabaseConfig config;
    private boolean initialized = false;

    public ConnectionPool(DatabaseConfig config) {
        this.config = config;
    }

    /**
     * Initializes the connection pool
     *
     * @return true if successful, false otherwise
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }

        try {
            HikariConfig hikariConfig = new HikariConfig();
            
            // Basic connection settings
            hikariConfig.setJdbcUrl(config.getJdbcUrl());
            
            if (config.getType() != DatabaseConfig.DatabaseType.SQLITE) {
                hikariConfig.setUsername(config.getUsername());
                hikariConfig.setPassword(config.getPassword());
            }

            // Pool settings
            hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
            hikariConfig.setMinimumIdle(config.getMinPoolSize());
            hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
            hikariConfig.setIdleTimeout(config.getIdleTimeout());
            hikariConfig.setMaxLifetime(config.getMaxLifetime());

            // Connection validation
            hikariConfig.setConnectionTestQuery("SELECT 1");
            hikariConfig.setValidationTimeout(TimeUnit.SECONDS.toMillis(5));

            // Pool name for monitoring
            hikariConfig.setPoolName("FancyPlugins-" + config.getType().getIdentifier());

            // Database-specific settings
            switch (config.getType()) {
                case MYSQL -> {
                    hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                    hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
                    hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
                    hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
                    hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
                    hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
                    hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
                    hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
                }
                case POSTGRESQL -> {
                    hikariConfig.setDriverClassName("org.postgresql.Driver");
                    hikariConfig.addDataSourceProperty("prepareThreshold", "1");
                    hikariConfig.addDataSourceProperty("preparedStatementCacheQueries", "256");
                    hikariConfig.addDataSourceProperty("preparedStatementCacheSizeMiB", "5");
                    hikariConfig.addDataSourceProperty("databaseMetadataCacheFields", "65536");
                    hikariConfig.addDataSourceProperty("databaseMetadataCacheFieldsMiB", "5");
                }
                case SQLITE -> {
                    hikariConfig.setDriverClassName("org.sqlite.JDBC");
                    // SQLite doesn't support connection pooling in the traditional sense
                    hikariConfig.setMaximumPoolSize(1);
                    hikariConfig.setMinimumIdle(1);
                    hikariConfig.addDataSourceProperty("journal_mode", "WAL");
                    hikariConfig.addDataSourceProperty("synchronous", "NORMAL");
                    hikariConfig.addDataSourceProperty("cache_size", "10000");
                    hikariConfig.addDataSourceProperty("foreign_keys", "true");
                }
            }

            // Additional properties
            if (config.getAdditionalProperties() != null && !config.getAdditionalProperties().isEmpty()) {
                String[] properties = config.getAdditionalProperties().split(";");
                for (String property : properties) {
                    String[] keyValue = property.split("=", 2);
                    if (keyValue.length == 2) {
                        hikariConfig.addDataSourceProperty(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }

            dataSource = new HikariDataSource(hikariConfig);
            initialized = true;

            Bukkit.getLogger().info("Database connection pool initialized successfully for " + config.getType());
            return true;

        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a connection from the pool
     *
     * @return database connection or null if failed
     */
    public Connection getConnection() {
        if (!initialized || dataSource == null) {
            return null;
        }

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get database connection: " + e.getMessage());
            return null;
        }
    }

    /**
     * Closes the connection pool
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            initialized = false;
            Bukkit.getLogger().info("Database connection pool closed");
        }
    }

    /**
     * Checks if the pool is initialized and healthy
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        if (!initialized || dataSource == null || dataSource.isClosed()) {
            return false;
        }

        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Gets pool statistics for monitoring
     *
     * @return formatted string with pool statistics
     */
    public String getPoolStats() {
        if (!initialized || dataSource == null) {
            return "Pool not initialized";
        }

        return String.format("Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
    }
}
