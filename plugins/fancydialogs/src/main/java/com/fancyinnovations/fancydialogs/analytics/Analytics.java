package com.fancyinnovations.fancydialogs.analytics;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyanalytics.sdk.events.Event;
import de.oliver.fancylib.VersionConfig;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class Analytics {

    private final FancyAnalyticsAPI api;

    public Analytics() {
        api = new FancyAnalyticsAPI("ebed5533-b25e-44b3-894c-6898f64f5033", "2DNDnGE0NmQwYWE5ZTYzMDQzYTZJNoFa");
        api.getConfig().setDisableLogging(true);
    }

    private void registerMetrics() {
        api.registerMinecraftPluginMetrics(FancyDialogsPlugin.get());
        api.getExceptionHandler().registerLogger(FancyDialogsPlugin.get().getLogger());
        api.getExceptionHandler().registerLogger(Bukkit.getLogger());
        api.getExceptionHandler().registerLogger(FancyDialogsPlugin.get().getFancyLogger());

        api.registerStringMetric(new MetricSupplier<>("language", () -> FancyDialogsPlugin.get().getTranslator().getSelectedLanguage().getLanguageCode()));

        api.registerStringMetric(new MetricSupplier<>("release_channel", () -> FancyDialogsPlugin.get().getVersionConfig().getChannel()));
        api.registerStringMetric(new MetricSupplier<>("release_platform", () -> FancyDialogsPlugin.get().getVersionConfig().getPlatform()));

        api.registerStringMetric(new MetricSupplier<>("server_size", () -> {
            long onlinePlayers = Bukkit.getOnlinePlayers().size();

            if (onlinePlayers == 0) {
                return "empty";
            }

            if (onlinePlayers <= 25) {
                return "small";
            }

            if (onlinePlayers <= 100) {
                return "medium";
            }

            if (onlinePlayers <= 500) {
                return "large";
            }

            return "very_large";
        }));

        api.registerNumberMetric(new MetricSupplier<>("amount_dialogs", () -> (double) FancyDialogsPlugin.get().getDialogRegistry().getAll().size()));

        api.registerNumberMetric(new MetricSupplier<>("amount_body_elements", () -> {
            long count = 0;
            for (Dialog dialog : FancyDialogsPlugin.get().getDialogRegistry().getAll()) {
                count += dialog.getData().body().size();
            }

            return (double) count;
        }));

        api.registerNumberMetric(new MetricSupplier<>("amount_input_elements", () -> {
            long count = 0;
            for (Dialog dialog : FancyDialogsPlugin.get().getDialogRegistry().getAll()) {
                count += dialog.getData().inputs().all().size();
            }

            return (double) count;
        }));

        api.registerNumberMetric(new MetricSupplier<>("amount_buttons", () -> {
            long count = 0;
            for (Dialog dialog : FancyDialogsPlugin.get().getDialogRegistry().getAll()) {
                count += dialog.getData().buttons().size();
            }

            return (double) count;
        }));

        api.registerNumberMetric(new MetricSupplier<>("amount_actions", () -> {
            long count = 0;
            for (Dialog dialog : FancyDialogsPlugin.get().getDialogRegistry().getAll()) {
                for (DialogButton button : dialog.getData().buttons()) {
                    count += button.actions().size();
                }
            }

            return (double) count;
        }));
    }

    private void checkIfVersionUpdated() {
        VersionConfig versionConfig = FancyDialogsPlugin.get().getVersionConfig();

        String currentVersion = versionConfig.getVersion();
        String lastVersion = "N/A";

        File versionFile = new File(FancyDialogsPlugin.get().getDataFolder(), "version.yml");
        if (!versionFile.exists()) {
            try {
                Files.write(versionFile.toPath(), currentVersion.getBytes());
            } catch (IOException e) {
                FancyDialogsPlugin.get().getFancyLogger().warn("Could not write version file.");
                return;
            }
        } else {
            try {
                lastVersion = new String(Files.readAllBytes(versionFile.toPath()));
            } catch (IOException e) {
                FancyDialogsPlugin.get().getFancyLogger().warn("Could not read version file.");
                return;
            }
        }

        if (!lastVersion.equals(currentVersion)) {
            FancyDialogsPlugin.get().getFancyLogger().info("Plugin has been updated from version " + lastVersion + " to " + currentVersion + ".");
            api.sendEvent(
                    new Event("PluginVersionUpdated", new HashMap<>())
                            .withProperty("from", lastVersion)
                            .withProperty("to", currentVersion)
                            .withProperty("commit_hash", versionConfig.getCommitHash())
                            .withProperty("channel", versionConfig.getChannel())
                            .withProperty("platform", versionConfig.getPlatform())
            );

            try {
                Files.write(versionFile.toPath(), currentVersion.getBytes());
            } catch (IOException e) {
                FancyDialogsPlugin.get().getFancyLogger().warn("Could not write version file.");
            }
        }
    }

    public void start() {
        registerMetrics();
        api.initialize();
        checkIfVersionUpdated();
    }
}
