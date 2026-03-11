package com.fancyinnovations.strata.decompiler;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.jetbrains.java.decompiler.api.Decompiler;
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

import java.io.File;
import java.nio.file.Files;

public class DecompilerService {

    private final Strata strata;

    public DecompilerService(Strata strata) {
        this.strata = strata;
    }

    public void decompile(String inputJarPath, String outputDirPath) {
        File inputFile = new File(inputJarPath);
        if (!inputFile.exists()) {
            strata.getLogger().error("Input JAR file does not exist: " + inputJarPath);
            return;
        }

        File outputDir = new File(outputDirPath);
        if (outputDir.exists()) {
            try {
                Files.walk(outputDir.toPath())
                        .map(java.nio.file.Path::toFile)
                        .forEach(File::delete);
                outputDir.delete();
            } catch (Exception e) {
                strata.getLogger().error("Failed to clear existing output directory: " + outputDirPath, ThrowableProperty.of(e));
                return;
            }
        }

        Decompiler decompiler = Decompiler.builder()
                .inputs(inputFile)
                .output(new DirectoryResultSaver(outputDir))
                .logger(new IFernflowerLogger() {
                    @Override
                    public void writeMessage(String s, Severity severity) {
                        LogLevel logLevel = switch (severity) {
                            case TRACE -> LogLevel.DEBUG;
                            case INFO -> LogLevel.INFO;
                            case WARN -> LogLevel.WARN;
                            case ERROR -> LogLevel.ERROR;
                        };

                        strata.getLogger().log(logLevel, s);
                    }

                    @Override
                    public void writeMessage(String s, Severity severity, Throwable throwable) {
                        LogLevel logLevel = switch (severity) {
                            case TRACE -> LogLevel.DEBUG;
                            case INFO -> LogLevel.INFO;
                            case WARN -> LogLevel.WARN;
                            case ERROR -> LogLevel.ERROR;
                        };

                        strata.getLogger().log(logLevel, s, ThrowableProperty.of(throwable));
                    }
                })
                .build();

        decompiler.decompile();

        strata.getLogger().info("Decompilation completed. Output directory: " + outputDirPath);
    }

}
