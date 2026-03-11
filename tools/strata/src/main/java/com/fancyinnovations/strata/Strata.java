package com.fancyinnovations.strata;

import com.fancyinnovations.strata.decompiler.DecompilerService;
import com.fancyinnovations.strata.extractor.ExtractorService;
import com.fancyinnovations.strata.mojang.MojangService;
import com.google.gson.Gson;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;

import java.io.File;
import java.util.List;

public class Strata {

    public static Gson GSON = new Gson();

    private final ExtendedFancyLogger logger;
    private final File cacheDir;

    private final MojangService mojangService;
    private final ExtractorService extractorService;
    private final DecompilerService decompilerService;

    public Strata(String cacheDirPath) {
        logger = new ExtendedFancyLogger(
                "Strata",
                LogLevel.INFO,
                List.of(new ConsoleAppender()),
                List.of()
        );

        cacheDir = new File(cacheDirPath);
        if (!cacheDir.exists()) {
            boolean created = cacheDir.mkdirs();
            if (created) {
                logger.info("Created cache directory at " + cacheDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create cache directory at " + cacheDir.getAbsolutePath());
            }
        }

        mojangService = new MojangService(this);
        extractorService = new ExtractorService(this);
        decompilerService = new DecompilerService(this);
    }

    public void init() {
        logger.info("Initializing Strata...");

        mojangService.loadCache();
    }

    public ExtendedFancyLogger getLogger() {
        return logger;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public MojangService getMojangService() {
        return mojangService;
    }

    public ExtractorService getExtractorService() {
        return extractorService;
    }

    public DecompilerService getDecompilerService() {
        return decompilerService;
    }
}
