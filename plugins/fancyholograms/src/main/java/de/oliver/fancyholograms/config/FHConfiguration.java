package de.oliver.fancyholograms.config;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import de.oliver.fancyholograms.api.HologramConfiguration;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;

public final class FHConfiguration implements HologramConfiguration {

    public static final String LOG_LEVEL_PATH = "settings.logging.level";

    public static final String MUTE_VERSION_NOTIFICATION_PATH = "settings.logging.version_notification";
    public static final String ENABLE_AUTOSAVE_PATH = "settings.saving.autosave.enabled";
    public static final String AUTOSAVE_INTERVAL_PATH = "settings.saving.autosave.interval";

    public static final String SAVE_ON_CHANGED_PATH = "settings.saving.save_on_changed";
    public static final String VISIBILITY_DISTANCE_PATH = "settings.visibility_distance";

    public static final String REGISTER_COMMANDS_PATH = "settings.register_commands";

    public static final String LANGUAGE_PATH = "settings.language";

    public static final String DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS_PATH = "experimental_features.disable_holograms_for_bedrock_players";
    public static final String DISABLE_HOLOGRAMS_FOR_OLD_CLIENTS = "experimental_features.disable_holograms_for_old_clients";

    private static final String CONFIG_FILE_PATH = "plugins/FancyHolograms/config.yml";
    private Config config;

    public void init() {
        config = new Config(FancyHologramsPlugin.get().getFancyLogger(), CONFIG_FILE_PATH);

        config.addField(new ConfigField<>(
                LOG_LEVEL_PATH,
                "The log level for the plugin (DEBUG, INFO, WARN, ERROR).",
                false,
                "INFO",
                false,
                String.class
        ));

        config.addField(new ConfigField<>(
                MUTE_VERSION_NOTIFICATION_PATH,
                "Whether version notifications are muted.",
                false,
                false,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                ENABLE_AUTOSAVE_PATH,
                "Whether autosave is enabled.",
                false,
                true,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                AUTOSAVE_INTERVAL_PATH,
                "The interval at which autosave is performed in minutes.",
                false,
                15,
                false,
                Integer.class
        ));

        config.addField(new ConfigField<>(
                SAVE_ON_CHANGED_PATH,
                "Whether the plugin should save holograms when they are changed.",
                false,
                true,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                VISIBILITY_DISTANCE_PATH,
                "The default visibility distance for holograms.",
                false,
                20,
                false,
                Integer.class
        ));

        config.addField(new ConfigField<>(
                REGISTER_COMMANDS_PATH,
                "Whether the plugin should register its commands.",
                false,
                true,
                false,
                Boolean.class
        ));

        /*
            FEATURE FLAGS
         */

        config.addField(new ConfigField<>(
                DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS_PATH,
                "Do not show holograms to bedrock players,",
                false,
                false,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                DISABLE_HOLOGRAMS_FOR_OLD_CLIENTS,
                "Do not show holograms to clients with a version older than 1.19.4.",
                false,
                false,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                LANGUAGE_PATH,
                "The language to use for the plugin.",
                false,
                "default",
                false,
                String.class
        ));

        config.reload();
    }

    public void reload() {
        config.reload();
    }

    public Config getConfig() {
        return config;
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

    @Override
    public boolean isHologramsForBedrockPlayersEnabled() {
        return !(boolean) config.get(DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS_PATH);
    }

    @Override
    public boolean isHologramsForOldClientsEnabled() {
        return !(boolean) config.get(DISABLE_HOLOGRAMS_FOR_OLD_CLIENTS);
    }

    @Override
    public String getLanguage() {
        return config.get(LANGUAGE_PATH);
    }
}
