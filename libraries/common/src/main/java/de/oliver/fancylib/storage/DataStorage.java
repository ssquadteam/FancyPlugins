package de.oliver.fancylib.storage;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Generic storage interface for plugin data
 *
 * @param <T> the type of data to store
 */
public interface DataStorage<T> {

    /**
     * Saves a single data object
     *
     * @param data the data to save
     * @return true if successful, false otherwise
     */
    boolean save(T data);

    /**
     * Saves a single data object asynchronously
     *
     * @param data the data to save
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> saveAsync(T data);

    /**
     * Saves multiple data objects in a batch
     *
     * @param data the collection of data to save
     * @return true if successful, false otherwise
     */
    boolean saveBatch(Collection<T> data);

    /**
     * Saves multiple data objects in a batch asynchronously
     *
     * @param data the collection of data to save
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> saveBatchAsync(Collection<T> data);

    /**
     * Loads a single data object by identifier
     *
     * @param id the identifier of the data to load
     * @return the loaded data or null if not found
     */
    T load(String id);

    /**
     * Loads a single data object by identifier asynchronously
     *
     * @param id the identifier of the data to load
     * @return CompletableFuture with the loaded data
     */
    CompletableFuture<T> loadAsync(String id);

    /**
     * Loads all data objects
     *
     * @return collection of all loaded data
     */
    Collection<T> loadAll();

    /**
     * Loads all data objects asynchronously
     *
     * @return CompletableFuture with collection of all loaded data
     */
    CompletableFuture<Collection<T>> loadAllAsync();

    /**
     * Loads all data objects for a specific world
     *
     * @param world the world name
     * @return collection of loaded data for the world
     */
    Collection<T> loadAll(String world);

    /**
     * Loads all data objects for a specific world asynchronously
     *
     * @param world the world name
     * @return CompletableFuture with collection of loaded data for the world
     */
    CompletableFuture<Collection<T>> loadAllAsync(String world);

    /**
     * Deletes a data object
     *
     * @param data the data to delete
     * @return true if successful, false otherwise
     */
    boolean delete(T data);

    /**
     * Deletes a data object asynchronously
     *
     * @param data the data to delete
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> deleteAsync(T data);

    /**
     * Deletes a data object by identifier
     *
     * @param id the identifier of the data to delete
     * @return true if successful, false otherwise
     */
    boolean delete(String id);

    /**
     * Deletes a data object by identifier asynchronously
     *
     * @param id the identifier of the data to delete
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> deleteAsync(String id);

    /**
     * Checks if a data object exists
     *
     * @param id the identifier to check
     * @return true if exists, false otherwise
     */
    boolean exists(String id);

    /**
     * Checks if a data object exists asynchronously
     *
     * @param id the identifier to check
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> existsAsync(String id);

    /**
     * Gets the storage type
     *
     * @return the storage type
     */
    StorageType getStorageType();

    /**
     * Initializes the storage system
     *
     * @return true if successful, false otherwise
     */
    boolean initialize();

    /**
     * Closes the storage system and releases resources
     *
     * @return true if successful, false otherwise
     */
    boolean close();

    /**
     * Checks if the storage system is healthy and operational
     *
     * @return true if healthy, false otherwise
     */
    boolean isHealthy();
}
