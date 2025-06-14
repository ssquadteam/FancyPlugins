package de.oliver.fancylib.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface Database {

    /**
     * Connects to the database
     *
     * @return true if success, false if failed
     */
    boolean connect();

    /**
     * Closes the database connection
     *
     * @return true if success, false if failed
     */
    boolean close();

    /**
     * @return true if connected, false if not
     */
    boolean isConnected();

    /**
     * @return the connection object, null if not connected
     */
    Connection getConnection();

    /**
     * Executes a statement on the database
     *
     * @return true if success, false if failed
     */
    boolean executeNonQuery(String sql);

    /**
     * Executes a query on the database
     *
     * @return the result or null if failed
     */
    ResultSet executeQuery(String query);

    /**
     * Executes a prepared statement on the database
     *
     * @param sql the SQL statement with placeholders
     * @param parameters the parameters to bind to the statement
     * @return true if success, false if failed
     */
    boolean executePreparedStatement(String sql, Object... parameters);

    /**
     * Executes a prepared query on the database
     *
     * @param sql the SQL query with placeholders
     * @param parameters the parameters to bind to the query
     * @return the result or null if failed
     */
    ResultSet executePreparedQuery(String sql, Object... parameters);

    /**
     * Executes a prepared statement asynchronously
     *
     * @param sql the SQL statement with placeholders
     * @param parameters the parameters to bind to the statement
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> executePreparedStatementAsync(String sql, Object... parameters);

    /**
     * Executes a prepared query asynchronously
     *
     * @param sql the SQL query with placeholders
     * @param parameters the parameters to bind to the query
     * @return CompletableFuture with the result
     */
    CompletableFuture<ResultSet> executePreparedQueryAsync(String sql, Object... parameters);

    /**
     * Creates a prepared statement
     *
     * @param sql the SQL statement
     * @return the prepared statement or null if failed
     * @throws SQLException if a database access error occurs
     */
    PreparedStatement prepareStatement(String sql) throws SQLException;

    /**
     * Tests the database connection
     *
     * @return true if connection is valid, false otherwise
     */
    boolean testConnection();

    /**
     * Gets the database type identifier
     *
     * @return the database type (mysql, postgresql, sqlite, etc.)
     */
    String getDatabaseType();

    /**
     * Initializes the database schema if needed
     *
     * @return true if successful, false otherwise
     */
    boolean initializeSchema();
}
