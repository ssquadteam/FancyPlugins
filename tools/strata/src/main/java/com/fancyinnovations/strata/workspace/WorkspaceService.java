package com.fancyinnovations.strata.workspace;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class WorkspaceService {

    private final Strata strata;

    public WorkspaceService(Strata strata) {
        this.strata = strata;
    }

    public void copyDecompiledSources(String sourceDir, String targetDir) {
        // Delete all existing files in the target directory
        try {
            if (Files.exists(Paths.get(targetDir))) {
                Files.walk(Paths.get(targetDir))
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (file.isDirectory()) {
                                return; // Skip directories
                            }
                            if (!file.delete()) {
                                strata.getLogger().error("Failed to delete file: " + file.getAbsolutePath());
                            } else {
                                strata.getLogger().debug("Deleted file: " + file.getAbsolutePath());
                            }
                        });

                strata.getLogger().info("Cleared existing target directory: " + targetDir);
            }
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to clear existing target directory: " + targetDir,
                    ThrowableProperty.of(e)
            );
            return;
        }

        // Only copy .java files (recursive) and ignore empty directories
        try {
            Files.walk(Paths.get(sourceDir))
                    .forEach(path -> {
                                if (Files.isDirectory(path)) {
                                    return; // Skip directories
                                }
                                if (!path.toString().endsWith(".java")) {
                                    return; // Skip non-java files
                                }

                                Path targetPath = Paths.get(targetDir).resolve(Paths.get(sourceDir).relativize(path));
                                try {
                                    Files.createDirectories(targetPath.getParent());
                                    Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                                    strata.getLogger().debug("Copy: " + path + " -> " + targetPath);
                                } catch (Exception e) {
                                    strata.getLogger().error(
                                            "Failed to copy file: " + path,
                                            ThrowableProperty.of(e)
                                    );
                                }
                            }
                    );
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to copy decompiled sources from " + sourceDir + " to " + targetDir,
                    ThrowableProperty.of(e)
            );
            return;
        }

        strata.getLogger().info("Copying decompiled sources completed");
    }

}
