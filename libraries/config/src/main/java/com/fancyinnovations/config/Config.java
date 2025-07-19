package com.fancyinnovations.config;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {

    private final ExtendedFancyLogger logger;
    private final File configFile;
    private final Map<String, ConfigField<?>> fields;
    private final Map<String, Object> values;

    public Config(ExtendedFancyLogger logger, String configFilePath) {
        this.logger = logger;
        this.configFile = new File(configFilePath);
        this.fields = new ConcurrentHashMap<>();
        this.values = new ConcurrentHashMap<>();
    }

    public void addField(ConfigField<?> field) {
        fields.put(field.path(), field);
    }

    public <T> T get(String path) {
        ConfigField<?> field = fields.get(path);
        if (field != null) {
            Object value = values.computeIfAbsent(path, k -> field.defaultValue());
            return (T) field.type().cast(value);
        }

        throw new IllegalArgumentException("No field found for path: " + path);
    }

    public void reload() {
        if (!configFile.exists()) {
            if (!configFile.mkdirs()) {
                logger.error("Failed to create directories for config file: " + configFile.getAbsolutePath());
                return;
            }

            try {
                if (!configFile.createNewFile()) {
                    logger.error("Failed to create config file: " + configFile.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                logger.error("Error creating config file: " + configFile.getAbsolutePath());
                logger.error(e);
                return;
            }

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(configFile);
            for (ConfigField<?> field : fields.values()) {
                setDefault(yaml, field);
            }

            saveYaml(yaml);
            return;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(configFile);
        boolean dirty = false;

        for (Map.Entry<String, ConfigField<?>> entry : fields.entrySet()) {
            String path = entry.getKey();
            ConfigField<?> field = entry.getValue();

            if (field.forRemoval()) {
                if (yaml.isSet(path)) {
                    logger.debug("Removing path '" + path + "' from config");
                    yaml.set(path, null);
                    dirty = true;
                }
                continue;
            }

            if (yaml.isSet(path)) {
                Object value = yaml.get(path);
                if (field.type().isInstance(value)) {
                    values.put(path, value);
                } else {
                    logger.warn("Value for path '" + path + "' is not of type '" + field.type().getSimpleName());
                    setDefault(yaml, field);
                    dirty = true;
                }
            } else {
                logger.debug("Path '" + path + "' not found in config");
                setDefault(yaml, field);
                dirty = true;
            }
        }

        if (dirty) {
            saveYaml(yaml);
        }
    }

    private void setDefault(YamlConfiguration yaml, ConfigField<?> field) {
        logger.debug("Setting default value for path '" + field.path() + "': " + field.defaultValue());
        yaml.set(field.path(), field.defaultValue());
        yaml.setInlineComments(field.path(), List.of(field.description()));
    }

    private void saveYaml(YamlConfiguration yaml) {
        try {
            yaml.save(configFile);
        } catch (IOException e) {
            logger.error("Error saving config file: " + configFile.getAbsolutePath());
            logger.error(e);
        }
    }
}
