package de.oliver.fancynpcs.storage.migration;

import de.oliver.fancylib.storage.migration.AbstractDataMigrator;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Migrator for NPC data between different storage systems
 */
public class NpcDataMigrator extends AbstractDataMigrator<NpcData> {

    @Override
    protected String getItemIdentifier(NpcData item) {
        return item.getId();
    }

    @Override
    protected boolean validateItemIntegrity(NpcData sourceItem, NpcData targetItem) {
        if (sourceItem == null || targetItem == null) {
            return false;
        }

        // Basic validation - check core properties
        return sourceItem.getId().equals(targetItem.getId()) &&
               sourceItem.getName().equals(targetItem.getName()) &&
               sourceItem.getCreator().equals(targetItem.getCreator()) &&
               sourceItem.getType() == targetItem.getType() &&
               locationsEqual(sourceItem.getLocation(), targetItem.getLocation()) &&
               sourceItem.isShowInTab() == targetItem.isShowInTab() &&
               sourceItem.isSpawnEntity() == targetItem.isSpawnEntity() &&
               sourceItem.isCollidable() == targetItem.isCollidable() &&
               sourceItem.isGlowing() == targetItem.isGlowing() &&
               sourceItem.isTurnToPlayer() == targetItem.isTurnToPlayer() &&
               Float.compare(sourceItem.getInteractionCooldown(), targetItem.getInteractionCooldown()) == 0 &&
               Float.compare(sourceItem.getScale(), targetItem.getScale()) == 0 &&
               sourceItem.getVisibilityDistance() == targetItem.getVisibilityDistance() &&
               sourceItem.isMirrorSkin() == targetItem.isMirrorSkin();
    }

    private boolean locationsEqual(org.bukkit.Location loc1, org.bukkit.Location loc2) {
        if (loc1 == null || loc2 == null) {
            return loc1 == loc2;
        }

        return loc1.getWorld().equals(loc2.getWorld()) &&
               Double.compare(loc1.getX(), loc2.getX()) == 0 &&
               Double.compare(loc1.getY(), loc2.getY()) == 0 &&
               Double.compare(loc1.getZ(), loc2.getZ()) == 0 &&
               Float.compare(loc1.getYaw(), loc2.getYaw()) == 0 &&
               Float.compare(loc1.getPitch(), loc2.getPitch()) == 0;
    }

    @Override
    public boolean createBackup(de.oliver.fancylib.storage.DataStorage<NpcData> sourceStorage, String backupPath) {
        try {
            // Create backup directory
            Path backupDir = Path.of(backupPath);
            Files.createDirectories(backupDir);

            // Create timestamped backup folder
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            Path timestampedBackup = backupDir.resolve("npc_backup_" + timestamp);
            Files.createDirectories(timestampedBackup);

            // If source is YAML-based, copy the npcs.yml file
            if (sourceStorage.getStorageType() == de.oliver.fancylib.storage.StorageType.YAML) {
                File npcsFile = new File("plugins/FancyNpcs/npcs.yml");
                if (npcsFile.exists()) {
                    Path backupFile = timestampedBackup.resolve("npcs.yml");
                    Files.copy(npcsFile.toPath(), backupFile, StandardCopyOption.REPLACE_EXISTING);
                    Bukkit.getLogger().info("Backed up npcs.yml to: " + backupFile);
                }
            }

            // Also create a JSON export of all NPCs for universal backup
            exportToJson(sourceStorage, timestampedBackup.resolve("npcs_export.json"));

            Bukkit.getLogger().info("NPC backup created successfully at: " + timestampedBackup);
            return true;

        } catch (Exception e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to create NPC backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void exportToJson(de.oliver.fancylib.storage.DataStorage<NpcData> storage, Path jsonFile) throws IOException {
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .setPrettyPrinting()
                .create();

        java.util.Collection<NpcData> npcs = storage.loadAll();
        String json = gson.toJson(npcs);
        Files.writeString(jsonFile, json);
        
        Bukkit.getLogger().info("Exported " + npcs.size() + " NPCs to JSON: " + jsonFile);
    }

    /**
     * Migrates NPCs from YAML to database storage
     *
     * @param yamlStorage the YAML storage
     * @param databaseStorage the database storage
     * @return migration result
     */
    public MigrationResult migrateYamlToDatabase(
            de.oliver.fancylib.storage.DataStorage<NpcData> yamlStorage,
            de.oliver.fancylib.storage.DataStorage<NpcData> databaseStorage) {
        
        Bukkit.getLogger().info("Starting NPC migration from YAML to database...");
        
        // Create backup first
        String backupPath = "plugins/FancyNpcs/backups";
        if (!createBackup(yamlStorage, backupPath)) {
            Bukkit.getLogger().warning("Failed to create backup, but continuing with migration...");
        }

        // Perform migration
        MigrationResult result = migrate(yamlStorage, databaseStorage);
        
        if (result.isSuccess()) {
            Bukkit.getLogger().info("NPC migration completed successfully!");
            
            // Validate migration
            ValidationResult validation = validate(yamlStorage, databaseStorage);
            if (validation.isValid()) {
                Bukkit.getLogger().info("Migration validation passed!");
            } else {
                Bukkit.getLogger().warning("Migration validation failed: " + validation);
            }
        } else {
            Bukkit.getLogger().severe("NPC migration failed: " + result.getErrorMessage());
        }

        return result;
    }

    /**
     * Migrates NPCs from database to YAML storage (rollback)
     *
     * @param databaseStorage the database storage
     * @param yamlStorage the YAML storage
     * @return migration result
     */
    public MigrationResult migrateDatabaseToYaml(
            de.oliver.fancylib.storage.DataStorage<NpcData> databaseStorage,
            de.oliver.fancylib.storage.DataStorage<NpcData> yamlStorage) {
        
        Bukkit.getLogger().info("Starting NPC rollback from database to YAML...");
        
        // Create backup first
        String backupPath = "plugins/FancyNpcs/backups";
        if (!createBackup(databaseStorage, backupPath)) {
            Bukkit.getLogger().warning("Failed to create backup, but continuing with rollback...");
        }

        // Perform migration
        MigrationResult result = migrate(databaseStorage, yamlStorage);
        
        if (result.isSuccess()) {
            Bukkit.getLogger().info("NPC rollback completed successfully!");
        } else {
            Bukkit.getLogger().severe("NPC rollback failed: " + result.getErrorMessage());
        }

        return result;
    }

    /**
     * Performs a dry run migration to check for potential issues
     *
     * @param sourceStorage the source storage
     * @param targetStorage the target storage
     * @return validation result indicating potential issues
     */
    public ValidationResult dryRun(
            de.oliver.fancylib.storage.DataStorage<NpcData> sourceStorage,
            de.oliver.fancylib.storage.DataStorage<NpcData> targetStorage) {
        
        Bukkit.getLogger().info("Performing NPC migration dry run...");
        
        try {
            java.util.Collection<NpcData> sourceData = sourceStorage.loadAll();
            java.util.List<String> issues = new java.util.ArrayList<>();
            
            for (NpcData npc : sourceData) {
                // Check for potential issues
                if (npc.getId() == null || npc.getId().isEmpty()) {
                    issues.add("NPC with empty ID: " + npc.getName());
                }
                
                if (npc.getName() == null || npc.getName().isEmpty()) {
                    issues.add("NPC with empty name: " + npc.getId());
                }
                
                if (npc.getLocation() == null) {
                    issues.add("NPC with null location: " + npc.getName());
                } else if (npc.getLocation().getWorld() == null) {
                    issues.add("NPC with null world: " + npc.getName());
                }
                
                if (npc.getCreator() == null) {
                    issues.add("NPC with null creator: " + npc.getName());
                }
            }
            
            boolean valid = issues.isEmpty();
            long sourceCount = sourceData.size();
            
            if (valid) {
                Bukkit.getLogger().info("Dry run completed successfully. " + sourceCount + " NPCs ready for migration.");
            } else {
                Bukkit.getLogger().warning("Dry run found " + issues.size() + " potential issues:");
                issues.forEach(issue -> Bukkit.getLogger().warning("  - " + issue));
            }
            
            return new ValidationResult(valid, sourceCount, 0, java.util.Collections.emptyList(), issues);
            
        } catch (Exception e) {
            Bukkit.getLogger().severe("Dry run failed: " + e.getMessage());
            e.printStackTrace();
            return new ValidationResult(false, 0, 0, java.util.Collections.emptyList(), java.util.Collections.singletonList(e.getMessage()));
        }
    }
}
