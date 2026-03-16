package com.fancyinnovations.strata.patcher;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import io.codechicken.diffpatch.cli.CliOperation;
import io.codechicken.diffpatch.cli.DiffOperation;
import io.codechicken.diffpatch.util.Input;
import io.codechicken.diffpatch.util.LogLevel;
import io.codechicken.diffpatch.util.Output;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class PatcherService {

    private final Strata strata;

    public PatcherService(Strata strata) {
        this.strata = strata;
    }

    public void rebuildFilePatches(String originalSourcePath, String patchedSourcePath, String patchesPath) {
        // Delete existing patches
        try {
            Path patchesDir = Path.of(patchesPath);
            if (patchesDir.toFile().exists()) {
                Files.walk(patchesDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to clear existing patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("patchesPath", patchesPath)
            );
            return;
        }

        try {
            CliOperation.Result<DiffOperation.DiffSummary> result = DiffOperation.builder()
                    .baseInput(Input.MultiInput.folder(Path.of(originalSourcePath)))
                    .changedInput(Input.MultiInput.folder(Path.of(patchedSourcePath)))
                    .patchesOutput(Output.MultiOutput.folder(Path.of(patchesPath)))
                    .autoHeader(true)
                    .ignorePrefix(".git")
                    .ignorePrefix("META-INF")
                    .ignorePrefix("data/")
                    .ignorePrefix("assets/")
                    .ignorePrefix("version.json")
                    .ignorePrefix("flightrecorder-config.jfc")
                    .context(3)
                    .logTo(s -> strata.getLogger().error(s))
                    .level(LogLevel.ERROR)
                    .summary(false)
                    .build()
                    .operate();

            strata.getLogger().info(
                    "Finished rebuilding file patches",
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to rebuild file patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        }
    }

}
