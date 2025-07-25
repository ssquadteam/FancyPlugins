package com.fancyinnovations.fancyholograms.storage;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;

import java.util.Collection;

public class StorageMigrator {

    public void migrate() {
        if (!YamlHologramStorage.HOLOGRAMS_CONFIG_FILE.exists()) {
            FancyHologramsPlugin.get().getFancyLogger().debug("No holograms.yml file found, skipping migration.");
            return;
        }

        FancyHologramsPlugin.get().getFancyLogger().info("Migrating holograms.yml to JSON format...");

        HologramStorage yamlStorage = new YamlHologramStorage();
        Collection<HologramData> data = yamlStorage.loadAll();
        for (HologramData d : data) {
            d.setFilePath("migrated/" + d.getName());
            Hologram hologram = FancyHologramsPlugin.get().getHologramFactory().apply(d);
            FancyHologramsPlugin.get().getRegistry().register(hologram);
            FancyHologramsPlugin.get().getFancyLogger().info("Migrated hologram " + hologram.getData().getName());
        }

        if (!YamlHologramStorage.HOLOGRAMS_CONFIG_FILE.renameTo(YamlHologramStorage.HOLOGRAMS_CONFIG_FILE.getParentFile().toPath().resolve("holograms-old.yml").toFile())) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to rename holograms.yml to holograms-old.yml");
        }

        FancyHologramsPlugin.get().getFancyLogger().info("Migration completed");
    }

}
