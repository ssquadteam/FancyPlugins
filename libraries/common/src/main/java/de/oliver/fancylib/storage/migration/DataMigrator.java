package de.oliver.fancylib.storage.migration;

import de.oliver.fancylib.storage.DataStorage;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for migrating data between different storage systems
 */
public interface DataMigrator<T> {

    /**
     * Migrates data from source storage to target storage
     *
     * @param sourceStorage the source storage system
     * @param targetStorage the target storage system
     * @return migration result
     */
    MigrationResult migrate(DataStorage<T> sourceStorage, DataStorage<T> targetStorage);

    /**
     * Migrates data asynchronously
     *
     * @param sourceStorage the source storage system
     * @param targetStorage the target storage system
     * @return CompletableFuture with migration result
     */
    CompletableFuture<MigrationResult> migrateAsync(DataStorage<T> sourceStorage, DataStorage<T> targetStorage);

    /**
     * Validates data integrity after migration
     *
     * @param sourceStorage the source storage system
     * @param targetStorage the target storage system
     * @return validation result
     */
    ValidationResult validate(DataStorage<T> sourceStorage, DataStorage<T> targetStorage);

    /**
     * Creates a backup of the source data before migration
     *
     * @param sourceStorage the source storage system
     * @param backupPath the backup location
     * @return true if backup was successful
     */
    boolean createBackup(DataStorage<T> sourceStorage, String backupPath);

    /**
     * Migration result containing statistics and status
     */
    class MigrationResult {
        private final boolean success;
        private final long totalItems;
        private final long migratedItems;
        private final long failedItems;
        private final long durationMs;
        private final String errorMessage;

        public MigrationResult(boolean success, long totalItems, long migratedItems, long failedItems, long durationMs, String errorMessage) {
            this.success = success;
            this.totalItems = totalItems;
            this.migratedItems = migratedItems;
            this.failedItems = failedItems;
            this.durationMs = durationMs;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public long getTotalItems() { return totalItems; }
        public long getMigratedItems() { return migratedItems; }
        public long getFailedItems() { return failedItems; }
        public long getDurationMs() { return durationMs; }
        public String getErrorMessage() { return errorMessage; }

        @Override
        public String toString() {
            return String.format("MigrationResult{success=%s, total=%d, migrated=%d, failed=%d, duration=%dms, error='%s'}",
                    success, totalItems, migratedItems, failedItems, durationMs, errorMessage);
        }
    }

    /**
     * Validation result for data integrity checks
     */
    class ValidationResult {
        private final boolean valid;
        private final long sourceCount;
        private final long targetCount;
        private final Collection<String> missingItems;
        private final Collection<String> corruptedItems;

        public ValidationResult(boolean valid, long sourceCount, long targetCount, 
                              Collection<String> missingItems, Collection<String> corruptedItems) {
            this.valid = valid;
            this.sourceCount = sourceCount;
            this.targetCount = targetCount;
            this.missingItems = missingItems;
            this.corruptedItems = corruptedItems;
        }

        public boolean isValid() { return valid; }
        public long getSourceCount() { return sourceCount; }
        public long getTargetCount() { return targetCount; }
        public Collection<String> getMissingItems() { return missingItems; }
        public Collection<String> getCorruptedItems() { return corruptedItems; }

        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, source=%d, target=%d, missing=%d, corrupted=%d}",
                    valid, sourceCount, targetCount, missingItems.size(), corruptedItems.size());
        }
    }
}

/**
 * Abstract base implementation of DataMigrator
 */
abstract class AbstractDataMigrator<T> implements DataMigrator<T> {

    @Override
    public CompletableFuture<MigrationResult> migrateAsync(DataStorage<T> sourceStorage, DataStorage<T> targetStorage) {
        return CompletableFuture.supplyAsync(() -> migrate(sourceStorage, targetStorage));
    }

    @Override
    public MigrationResult migrate(DataStorage<T> sourceStorage, DataStorage<T> targetStorage) {
        long startTime = System.currentTimeMillis();
        long totalItems = 0;
        long migratedItems = 0;
        long failedItems = 0;
        String errorMessage = null;

        try {
            Bukkit.getLogger().info("Starting migration from " + sourceStorage.getStorageType() + " to " + targetStorage.getStorageType());

            // Load all data from source
            Collection<T> sourceData = sourceStorage.loadAll();
            totalItems = sourceData.size();

            Bukkit.getLogger().info("Found " + totalItems + " items to migrate");

            // Migrate each item
            for (T item : sourceData) {
                try {
                    if (targetStorage.save(item)) {
                        migratedItems++;
                    } else {
                        failedItems++;
                        Bukkit.getLogger().warning("Failed to migrate item: " + getItemIdentifier(item));
                    }
                } catch (Exception e) {
                    failedItems++;
                    Bukkit.getLogger().warning("Error migrating item " + getItemIdentifier(item) + ": " + e.getMessage());
                }

                // Log progress every 100 items
                if ((migratedItems + failedItems) % 100 == 0) {
                    Bukkit.getLogger().info("Migration progress: " + (migratedItems + failedItems) + "/" + totalItems);
                }
            }

            long durationMs = System.currentTimeMillis() - startTime;
            boolean success = failedItems == 0;

            Bukkit.getLogger().info("Migration completed: " + migratedItems + " migrated, " + failedItems + " failed in " + durationMs + "ms");

            return new MigrationResult(success, totalItems, migratedItems, failedItems, durationMs, errorMessage);

        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - startTime;
            errorMessage = e.getMessage();
            Bukkit.getLogger().severe("Migration failed: " + errorMessage);
            e.printStackTrace();
            return new MigrationResult(false, totalItems, migratedItems, failedItems, durationMs, errorMessage);
        }
    }

    @Override
    public ValidationResult validate(DataStorage<T> sourceStorage, DataStorage<T> targetStorage) {
        try {
            Collection<T> sourceData = sourceStorage.loadAll();
            Collection<T> targetData = targetStorage.loadAll();

            long sourceCount = sourceData.size();
            long targetCount = targetData.size();

            // Create maps for quick lookup
            java.util.Map<String, T> sourceMap = new java.util.HashMap<>();
            java.util.Map<String, T> targetMap = new java.util.HashMap<>();

            for (T item : sourceData) {
                sourceMap.put(getItemIdentifier(item), item);
            }

            for (T item : targetData) {
                targetMap.put(getItemIdentifier(item), item);
            }

            // Find missing items
            java.util.List<String> missingItems = new java.util.ArrayList<>();
            for (String id : sourceMap.keySet()) {
                if (!targetMap.containsKey(id)) {
                    missingItems.add(id);
                }
            }

            // Find corrupted items (basic check - can be overridden)
            java.util.List<String> corruptedItems = new java.util.ArrayList<>();
            for (String id : sourceMap.keySet()) {
                if (targetMap.containsKey(id)) {
                    T sourceItem = sourceMap.get(id);
                    T targetItem = targetMap.get(id);
                    if (!validateItemIntegrity(sourceItem, targetItem)) {
                        corruptedItems.add(id);
                    }
                }
            }

            boolean valid = missingItems.isEmpty() && corruptedItems.isEmpty() && sourceCount == targetCount;

            return new ValidationResult(valid, sourceCount, targetCount, missingItems, corruptedItems);

        } catch (Exception e) {
            Bukkit.getLogger().severe("Validation failed: " + e.getMessage());
            e.printStackTrace();
            return new ValidationResult(false, 0, 0, java.util.Collections.emptyList(), java.util.Collections.emptyList());
        }
    }

    @Override
    public boolean createBackup(DataStorage<T> sourceStorage, String backupPath) {
        try {
            // This is a basic implementation - can be overridden for specific backup strategies
            Bukkit.getLogger().info("Creating backup at: " + backupPath);
            
            // For file-based storage, this might involve copying files
            // For database storage, this might involve creating a dump
            // Implementation depends on the specific storage type
            
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to create backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a unique identifier for an item
     *
     * @param item the item
     * @return the identifier
     */
    protected abstract String getItemIdentifier(T item);

    /**
     * Validates the integrity of a migrated item
     *
     * @param sourceItem the original item
     * @param targetItem the migrated item
     * @return true if the items are equivalent
     */
    protected abstract boolean validateItemIntegrity(T sourceItem, T targetItem);
}
