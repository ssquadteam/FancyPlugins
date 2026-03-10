package com.fancyinnovations.fancyworlds.main;

import com.fancyinnovations.fancyworlds.api.FancyWorlds;
import com.fancyinnovations.fancyworlds.api.FancyWorldsConfig;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;
import com.fancyinnovations.fancyworlds.commands.fancyworlds.FWConfigCMD;
import com.fancyinnovations.fancyworlds.commands.fancyworlds.FWVersionCMD;
import com.fancyinnovations.fancyworlds.config.FancyWorldsConfigImpl;
import com.fancyinnovations.fancyworlds.worlds.service.WorldServiceImpl;
import com.fancyinnovations.fancyworlds.worlds.storage.fake.FakeWorldStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.logging.PluginMiddleware;
import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.FancySpacesVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class FancyWorldsPlugin extends JavaPlugin implements FancyWorlds {

    private static FancyWorldsPlugin INSTANCE;
    private final ExtendedFancyLogger fancyLogger;

    private FancyWorldsConfigImpl fancyWorldsConfig;
    private VersionFetcher versionFetcher;
    private VersionConfig versionConfig;
    private Translator translator;

    private WorldStorage worldStorage;
    private WorldService worldService;

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
        fancyLogger.info("Loading FancyWorlds version %s...".formatted(getDescription().getVersion()));

        // Config
        fancyWorldsConfig = new FancyWorldsConfigImpl();
        fancyWorldsConfig.init();
        fancyWorldsConfig.reload();

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(fancyWorldsConfig.getLogLevel());
        } catch (IllegalArgumentException e) {
            logLevel = LogLevel.INFO;
        }
        fancyLogger.setCurrentLevel(logLevel);

        // Version checking
        versionFetcher = new FancySpacesVersionFetcher("FancyWorlds");
        versionConfig = new VersionConfig(this, versionFetcher);
        versionConfig.load();

        // Translator
        registerTranslator();

        // Services
        worldStorage = new FakeWorldStorage();
        worldService = new WorldServiceImpl(worldStorage);

        fancyLogger.info("Successfully loaded FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        fancyLogger.info("Enabling FancyWorlds version %s...".formatted(getDescription().getVersion()));

        if (!fancyWorldsConfig.areVersionNotificationsMuted()) {
            checkForNewerVersion();
        }
        if (versionConfig.isDevelopmentBuild()) {
            fancyLogger.warn("""
                    
                    --------------------------------------------------
                    You are using a development build of FancyWorlds.
                    Please be aware that there might be bugs in this version.
                    If you find any bugs, please report them on our discord server (https://discord.gg/ZUgYCEJUEx).
                    Read more about the risks of using a development build here: https://fancyinnovations.com/docs/general/development-guidelines/versioning#build
                    --------------------------------------------------
                    """);
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("FancyDialogs")) {
            fancyLogger.error("FancyDialogs plugin is not installed or not enabled. Please install and enable FancyDialogs to use FancyWorlds.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();

        fancyLogger.info("Successfully enabled FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        fancyLogger.info("Disabling FancyWorlds version %s...".formatted(getDescription().getVersion()));

        fancyLogger.info("Successfully disabled FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    private void registerCommands() {
        Lamp.Builder<BukkitCommandActor> lampBuilder = BukkitLamp
                .builder(this);

        // parameter types
//        lampBuilder.parameterTypes(builder -> {
//            builder.addParameterType(Hologram.class, HologramCommandType.INSTANCE);
//            builder.addParameterType(HologramTraitRegistry.TraitInfo.class, TraitCommandType.INSTANCE);
//            builder.addParameterType(de.oliver.fancylib.colors.GlowingColor.class, GlowingColorCommandType.INSTANCE);
//        });

        // exception handlers
//        lampBuilder
//                .exceptionHandler(HologramCommandType.INSTANCE)
//                .exceptionHandler(TraitCommandType.INSTANCE)
//                .exceptionHandler(GlowingColorCommandType.INSTANCE);

        Lamp<BukkitCommandActor> lamp = lampBuilder.build();

        // fancyworlds commands
        lamp.register(FWVersionCMD.INSTANCE);
        lamp.register(FWConfigCMD.INSTANCE);

        // world commands
//        lamp.register(TraitCMD.INSTANCE);
    }

    public void registerTranslator() {
        translator = new Translator(
                new TextConfig(
                        "#ffcc24", // color to highlight important information
                        "gray", // text color for regular messages
                        "#81E366",
                        "#E3CA66",
                        "#E36666",
                        "<color:#ba8813>[</color><gradient:#ffae00:#fffb00:#ffae00>FancyWorlds</gradient><color:#ba8813>]</color> <gray>"
                )
        );

        translator.loadLanguages(getDataFolder().getAbsolutePath());
        Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(fancyWorldsConfig.getLanguage()))
                .findFirst()
                .orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);
    }

    private void checkForNewerVersion() {
        final var current = new ComparableVersion(versionConfig.getVersion());

        supplyAsync(getVersionFetcher()::fetchNewestVersion).thenApply(Objects::requireNonNull).whenComplete((newest, error) -> {
            if (error != null || newest.compareTo(current) <= 0) {
                return; // could not get the newest version or already on latest
            }

            fancyLogger.warn("""
                    
                    -------------------------------------------------------
                    You are not using the latest version of the FancyWorlds plugin.
                    Please update to the newest version (%s).
                    %s
                    -------------------------------------------------------
                    """.formatted(newest, getVersionFetcher().getDownloadUrl()));
        });
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    @Override
    public FancyWorldsConfig getFancyWorldsConfig() {
        return fancyWorldsConfig;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public VersionConfig getVersionConfig() {
        return versionConfig;
    }

    public Translator getTranslator() {
        return translator;
    }

    @Override
    public WorldStorage getWorldStorage() {
        return worldStorage;
    }

    @Override
    public WorldService getWorldService() {
        return worldService;
    }
}
