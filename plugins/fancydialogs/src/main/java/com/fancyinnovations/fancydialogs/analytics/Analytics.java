package com.fancyinnovations.fancydialogs.analytics;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import org.bukkit.Bukkit;

public class Analytics {

    private final FancyAnalyticsAPI api;

    public Analytics() {
        api = new FancyAnalyticsAPI("ebed5533-b25e-44b3-894c-6898f64f5033", "2DNDnGE0NmQwYWE5ZTYzMDQzYTZJNoFa");
        api.getConfig().setDisableLogging(true);
    }

    public void registerMetrics() {
        api.registerMinecraftPluginMetrics(FancyDialogsPlugin.get());
        api.getExceptionHandler().registerLogger(FancyDialogsPlugin.get().getLogger());
        api.getExceptionHandler().registerLogger(Bukkit.getLogger());
        api.getExceptionHandler().registerLogger(FancyDialogsPlugin.get().getFancyLogger());

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
    }

    public void start() {
        api.initialize();
    }

}
