package de.oliver.fancylib.storage.sync;

import java.time.Instant;

/**
 * Interface for data objects that support cross-server synchronization
 */
public interface SyncableData {

    /**
     * Gets the unique identifier for this data object
     *
     * @return the unique identifier
     */
    String getId();

    /**
     * Gets the last modification timestamp
     *
     * @return the last modification timestamp
     */
    Instant getLastModified();

    /**
     * Sets the last modification timestamp
     *
     * @param timestamp the timestamp to set
     */
    void setLastModified(Instant timestamp);

    /**
     * Gets the version number for conflict resolution
     *
     * @return the version number
     */
    long getVersion();

    /**
     * Sets the version number
     *
     * @param version the version number to set
     */
    void setVersion(long version);

    /**
     * Gets the server ID that last modified this data
     *
     * @return the server ID
     */
    String getLastModifiedBy();

    /**
     * Sets the server ID that last modified this data
     *
     * @param serverId the server ID
     */
    void setLastModifiedBy(String serverId);

    /**
     * Checks if this data object has been modified since the last sync
     *
     * @return true if dirty, false otherwise
     */
    boolean isDirty();

    /**
     * Sets the dirty flag
     *
     * @param dirty the dirty flag
     */
    void setDirty(boolean dirty);

    /**
     * Gets the data type identifier for synchronization
     *
     * @return the data type identifier
     */
    String getDataType();

    /**
     * Creates a copy of this data object for synchronization
     *
     * @return a copy of this data object
     */
    SyncableData copy();

    /**
     * Merges changes from another version of this data object
     *
     * @param other the other version to merge from
     * @return the merge result
     */
    MergeResult merge(SyncableData other);

    /**
     * Result of a merge operation
     */
    enum MergeResult {
        /**
         * Merge was successful, no conflicts
         */
        SUCCESS,
        
        /**
         * Merge had conflicts that were automatically resolved
         */
        CONFLICT_RESOLVED,
        
        /**
         * Merge had conflicts that require manual resolution
         */
        CONFLICT_MANUAL,
        
        /**
         * Merge failed due to incompatible versions
         */
        FAILED
    }
}
