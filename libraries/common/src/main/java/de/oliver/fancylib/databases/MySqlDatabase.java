package de.oliver.fancylib.databases;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySqlDatabase implements Database {

    protected final DatabaseConfig config;
    protected final ConnectionPool connectionPool;
    protected final ExecutorService asyncExecutor;

    public MySqlDatabase(DatabaseConfig config) {
        this.config = config;
        this.connectionPool = new ConnectionPool(config);
        this.asyncExecutor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r, "FancyPlugins-DB-" + config.getType());
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public boolean connect() {
        return connectionPool.initialize();
    }

    @Override
    public boolean close() {
        try {
            if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
                asyncExecutor.shutdown();
            }
            connectionPool.close();
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error closing database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connectionPool.isHealthy();
    }

    @Override
    public Connection getConnection() {
        return connectionPool.getConnection();
    }

    @Override
    public boolean executeNonQuery(String sql) {
        try (Connection conn = getConnection()) {
            if (conn == null) return false;
            conn.createStatement().execute(sql);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error executing non-query: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet executeQuery(String query) {
        try {
            Connection conn = getConnection();
            if (conn == null) return null;
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean executePreparedStatement(String sql, Object... parameters) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) return false;

            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }

            stmt.execute();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error executing prepared statement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet executePreparedQuery(String sql, Object... parameters) {
        try {
            Connection conn = getConnection();
            if (conn == null) return null;

            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }

            return stmt.executeQuery();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error executing prepared query: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CompletableFuture<Boolean> executePreparedStatementAsync(String sql, Object... parameters) {
        return CompletableFuture.supplyAsync(() -> executePreparedStatement(sql, parameters), asyncExecutor);
    }

    @Override
    public CompletableFuture<ResultSet> executePreparedQueryAsync(String sql, Object... parameters) {
        return CompletableFuture.supplyAsync(() -> executePreparedQuery(sql, parameters), asyncExecutor);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        Connection conn = getConnection();
        if (conn == null) {
            throw new SQLException("No database connection available");
        }
        return conn.prepareStatement(sql);
    }

    @Override
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public String getDatabaseType() {
        return config.getType().getIdentifier();
    }

    @Override
    public boolean initializeSchema() {
        // This will be implemented by specific database implementations
        // or overridden by plugins that need custom schema initialization
        return true;
    }

    /**
     * Gets the database configuration
     *
     * @return the database configuration
     */
    public DatabaseConfig getConfig() {
        return config;
    }

    /**
     * Gets connection pool statistics
     *
     * @return formatted string with pool statistics
     */
    public String getPoolStats() {
        return connectionPool.getPoolStats();
    }
}
