package com.fancyinnovations.strata;

import com.fancyinnovations.strata.mojang.PistonVersionDetails;
import com.fancyinnovations.strata.workspace.WorkspaceService;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

public class Main {

    public static void main(String[] args) {
        Strata strata = new Strata("tools/strata/strata-cache");
        strata.init();

        PistonVersionDetails latest = strata.getMojangService().getLatestSnapshot();
        strata.getMojangService().downloadServerBundle(latest);
        strata.getExtractorService().extractServerBundle(latest.id());
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(latest.id()),
                latest.id()
        );

        String gitDir = "tools/strata/minecraft-source/src/main/java";
        strata.getWorkspaceService().initGitDirectory(gitDir);

        try {
            Thread.sleep(1000); // Sleep for a short time to ensure the file system is ready
        } catch (InterruptedException e) {
            strata.getLogger().error(
                    "Interrupted while waiting for file system to be ready",
                    ThrowableProperty.of(e)
            );
        }

        strata.getWorkspaceService().copyDecompiledSources(latest.id(), gitDir);
        strata.getWorkspaceService().copyDataAndAssets(latest.id(), "tools/strata/minecraft-source/src/main/resources");
        strata.getWorkspaceService().gitCommit(gitDir, "Add decompiled sources");
        strata.getWorkspaceService().gitTag(gitDir, WorkspaceService.DECOMPILED_SOURCES_TAG);
    }

}
