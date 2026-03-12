package com.fancyinnovations.strata.workspace;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class WorkspaceService {

    private static final String INITIAL_TAG = "strata/initial";
    private static final String AUTHOR = "Strata <strata@fancyinnovations.com>";

    private final Strata strata;

    public WorkspaceService(Strata strata) {
        this.strata = strata;
    }

    public void copyDecompiledSources(String versionID, String targetDir) {
        // Delete all existing files in the target directory
        try {
            if (Files.exists(Paths.get(targetDir))) {
                Files.walk(Paths.get(targetDir))
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (file.isDirectory()) {
                                return; // Skip directories
                            }
                            if (file.getAbsolutePath().contains(".git")) {
                                return; // Skip .git files
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

        Path cacheDir = strata.getCacheDir().toPath();
        Path sourceDir = cacheDir.resolve("decompiled").resolve(versionID);

        // Only copy .java files (recursive) and ignore empty directories
        try {
            Files.walk(sourceDir)
                    .forEach(path -> {
                                if (Files.isDirectory(path)) {
                                    return; // Skip directories
                                }
                                if (!path.toString().endsWith(".java")) {
                                    return; // Skip non-java files
                                }

                                Path targetPath = Paths.get(targetDir).resolve(sourceDir.relativize(path));
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

    public void initGitDirectory(String gitDir) {
        // check if .git exists in the target directory, if not create it and run git init
        if (Files.exists(Paths.get(gitDir, ".git"))) {
            strata.getLogger().info("Git repository already initialized in: " + gitDir);
            return;
        }

        // git init
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("git", "init", "--initial-branch=strata");
            processBuilder.directory(new File(gitDir));
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                strata.getLogger().info("Initialized Git repository in: " + gitDir);
            } else {
                strata.getLogger().error("Failed to initialize Git repository in: " + gitDir + " (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to run git init in: " + gitDir,
                    ThrowableProperty.of(e)
            );
        }

        // initial commit
        gitCommit(gitDir, "Initial commit");

        // create initial tag
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "git",
                    "tag",
                    INITIAL_TAG
            );
            processBuilder.directory(new File(gitDir));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                strata.getLogger().info("Created initial tag: " + INITIAL_TAG);
            } else {
                strata.getLogger().error("Failed to create initial tag: " + INITIAL_TAG + " (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to create initial tag: " + INITIAL_TAG,
                    ThrowableProperty.of(e)
            );
        }
    }

    public void gitCommit(String gitDir, String message) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("git", "add", ".");
            processBuilder.directory(new File(gitDir));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process addProcess = processBuilder.start();
            int addExitCode = addProcess.waitFor();

            if (addExitCode != 0) {
                strata.getLogger().error("Failed to stage changes in: " + gitDir + " (exit code: " + addExitCode + ")");
                return;
            }

            processBuilder = new ProcessBuilder(
                    "git",
                    "commit",
                    "--allow-empty",
                    "--no-gpg-sign",
                    "--author=" + AUTHOR,
                    "-m",
                    message
            );
            processBuilder.directory(new File(gitDir));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process commitProcess = processBuilder.start();
            int commitExitCode = commitProcess.waitFor();

            if (commitExitCode == 0) {
                strata.getLogger().info("Committed changes in: " + gitDir);
            } else {
                strata.getLogger().error("Failed to commit changes in: " + gitDir + " (exit code: " + commitExitCode + ")");
            }

        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to run git commit in: " + gitDir,
                    ThrowableProperty.of(e)
            );
        }
    }

}
