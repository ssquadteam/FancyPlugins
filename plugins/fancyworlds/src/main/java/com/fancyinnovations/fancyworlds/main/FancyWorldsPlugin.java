package com.fancyinnovations.fancyworlds.main;

import com.fancyinnovations.fancyworlds.api.FancyWorlds;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.logging.PluginMiddleware;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FancyWorldsPlugin extends JavaPlugin implements FancyWorlds {

    private static FancyWorldsPlugin INSTANCE;
    private final ExtendedFancyLogger fancyLogger;

    public FancyWorldsPlugin() {
        INSTANCE = this;
        
        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyWorlds/logs/FW-logs-" + date + ".txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JsonAppender jsonAppender = new JsonAppender(false, false, true, logsFile.getPath());
        this.fancyLogger = new ExtendedFancyLogger(
                "FancyWorlds",
                LogLevel.INFO,
                List.of(consoleAppender, jsonAppender),
                List.of(new PluginMiddleware(this))
        );
    }

    public static FancyWorldsPlugin get() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        fancyLogger.info("Successfully loaded FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        fancyLogger.info("Successfully enabled FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        fancyLogger.info("Successfully disabled FancyWorlds version %s".formatted(getDescription().getVersion()));
    }
}
