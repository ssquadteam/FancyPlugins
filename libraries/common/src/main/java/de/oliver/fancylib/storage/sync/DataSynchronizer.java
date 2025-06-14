package de.oliver.fancylib.storage.sync;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for cross-server data synchronization
 */
public interface DataSynchronizer<T extends SyncableData> {

    /**
     * Starts the synchronization service
     *
     * @return true if successful, false otherwise
     */
    boolean start();

    /**
     * Stops the synchronization service
     *
     * @return true if successful, false otherwise
     */
    boolean stop();

    /**
     * Checks if the synchronization service is running
     *
     * @return true if running, false otherwise
     */
    boolean isRunning();

    /**
     * Synchronizes a single data object to other servers
     *
     * @param data the data to synchronize
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> syncToOthers(T data);

    /**
     * Synchronizes multiple data objects to other servers
     *
     * @param data the collection of data to synchronize
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> syncBatchToOthers(Collection<T> data);

    /**
     * Pulls changes from other servers
     *
     * @return CompletableFuture with the collection of updated data
     */
    CompletableFuture<Collection<T>> pullChanges();

    /**
     * Pulls changes for a specific data object from other servers
     *
     * @param id the identifier of the data to pull
     * @return CompletableFuture with the updated data
     */
    CompletableFuture<T> pullChanges(String id);

    /**
     * Forces a full synchronization of all data
     *
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> fullSync();

    /**
     * Registers a listener for synchronization events
     *
     * @param listener the listener to register
     */
    void addSyncListener(SyncListener<T> listener);

    /**
     * Unregisters a synchronization event listener
     *
     * @param listener the listener to unregister
     */
    void removeSyncListener(SyncListener<T> listener);

    /**
     * Gets the current server ID
     *
     * @return the server ID
     */
    String getServerId();

    /**
     * Sets the current server ID
     *
     * @param serverId the server ID
     */
    void setServerId(String serverId);

    /**
     * Gets synchronization statistics
     *
     * @return the synchronization statistics
     */
    SyncStats getStats();

    /**
     * Interface for listening to synchronization events
     */
    interface SyncListener<T extends SyncableData> {

        /**
         * Called when data is received from another server
         *
         * @param data the received data
         * @param sourceServerId the ID of the server that sent the data
         */
        void onDataReceived(T data, String sourceServerId);

        /**
         * Called when data is successfully sent to other servers
         *
         * @param data the sent data
         */
        void onDataSent(T data);

        /**
         * Called when a synchronization conflict occurs
         *
         * @param localData the local version of the data
         * @param remoteData the remote version of the data
         * @param resolution the conflict resolution result
         */
        void onConflict(T localData, T remoteData, SyncableData.MergeResult resolution);

        /**
         * Called when a synchronization error occurs
         *
         * @param error the error that occurred
         * @param data the data that failed to sync (may be null)
         */
        void onError(Exception error, T data);
    }

    /**
     * Synchronization statistics
     */
    class SyncStats {
        private long totalSent;
        private long totalReceived;
        private long conflictsResolved;
        private long errors;
        private long lastSyncTime;

        public long getTotalSent() { return totalSent; }
        public void setTotalSent(long totalSent) { this.totalSent = totalSent; }
        public void incrementSent() { this.totalSent++; }

        public long getTotalReceived() { return totalReceived; }
        public void setTotalReceived(long totalReceived) { this.totalReceived = totalReceived; }
        public void incrementReceived() { this.totalReceived++; }

        public long getConflictsResolved() { return conflictsResolved; }
        public void setConflictsResolved(long conflictsResolved) { this.conflictsResolved = conflictsResolved; }
        public void incrementConflicts() { this.conflictsResolved++; }

        public long getErrors() { return errors; }
        public void setErrors(long errors) { this.errors = errors; }
        public void incrementErrors() { this.errors++; }

        public long getLastSyncTime() { return lastSyncTime; }
        public void setLastSyncTime(long lastSyncTime) { this.lastSyncTime = lastSyncTime; }

        @Override
        public String toString() {
            return String.format("SyncStats{sent=%d, received=%d, conflicts=%d, errors=%d, lastSync=%d}",
                    totalSent, totalReceived, conflictsResolved, errors, lastSyncTime);
        }
    }
}
