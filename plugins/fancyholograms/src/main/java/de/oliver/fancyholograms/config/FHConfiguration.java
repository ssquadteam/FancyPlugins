package de.oliver.fancyholograms.config;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import de.oliver.fancyholograms.api.HologramConfiguration;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;

public final class FHConfiguration implements HologramConfiguration {

    private static final String CONFIG_FILE_PATH = "plugins/FancyHolograms/config.yml";
    private static final String MUTE_VERSION_NOTIFICATION_PATH = "mute_version_notification";
    private static final String ENABLE_AUTOSAVE_PATH = "enable_autosave";
    private static final String AUTOSAVE_INTERVAL_PATH = "autosave_interval";
    private static final String SAVE_ON_CHANGED_PATH = "save_on_changed";
    private static final String VISIBILITY_DISTANCE_PATH = "visibility_distance";
    private static final String REGISTER_COMMANDS_PATH = "register_commands";
    private static final String LOG_LEVEL_PATH = "log_level";

    private Config config;

    public void init() {
        config = new Config(FancyHologramsPlugin.get().getFancyLogger(), CONFIG_FILE_PATH);
        
        config.addField(new ConfigField<>(
                MUTE_VERSION_NOTIFICATION_PATH,
                "Whether version notifications are muted.",
                false,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                ENABLE_AUTOSAVE_PATH,
                "Whether autosave is enabled.",
                false,
                true,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                AUTOSAVE_INTERVAL_PATH,
                "The interval at which autosave is performed in minutes.",
                false,
                15,
                Integer.class
        ));

        config.addField(new ConfigField<>(
                SAVE_ON_CHANGED_PATH,
                "Whether the plugin should save holograms when they are changed.",
                false,
                true,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                VISIBILITY_DISTANCE_PATH,
                "The default visibility distance for holograms.",
                false,
                20,
                Integer.class
        ));

        config.addField(new ConfigField<>(
                REGISTER_COMMANDS_PATH,
                "Whether the plugin should register its commands.",
                false,
                true,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                LOG_LEVEL_PATH,
                "The log level for the plugin (DEBUG, INFO, WARN, ERROR).",
                false,
                "INFO",
                String.class
        ));

        config.reload();
    }

    public void reload() {
        config.reload();
    }

    @Override
    public boolean areVersionNotificationsMuted() {
        return config.get(MUTE_VERSION_NOTIFICATION_PATH);
    }

    @Override
    public boolean isAutosaveEnabled() {
        return config.get(ENABLE_AUTOSAVE_PATH);
    }

    @Override
    public int getAutosaveInterval() {
        return config.get(AUTOSAVE_INTERVAL_PATH);
    }

    @Override
    public boolean isSaveOnChangedEnabled() {
        return config.get(SAVE_ON_CHANGED_PATH);
    }

    @Override
    public int getDefaultVisibilityDistance() {
        return config.get(VISIBILITY_DISTANCE_PATH);
    }

    @Override
    public boolean isRegisterCommands() {
        return config.get(REGISTER_COMMANDS_PATH);
    }

    @Override
    public String getLogLevel() {
        return config.get(LOG_LEVEL_PATH);
    }
}
