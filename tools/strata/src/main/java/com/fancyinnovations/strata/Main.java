package com.fancyinnovations.strata;

import com.fancyinnovations.strata.mojang.PistonVersionDetails;
import com.fancyinnovations.strata.workspace.WorkspaceService;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

public class Main {

    /**
     *  For minecraft-source
     */
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

        sleep(1000);

        strata.getWorkspaceService().copyDecompiledSources(latest.id(), gitDir);
        strata.getWorkspaceService().copyDataAndAssets(latest.id(), "tools/strata/minecraft-source/src/main/resources");
        strata.getWorkspaceService().gitCommit(gitDir, "Add decompiled sources");
        strata.getWorkspaceService().gitTag(gitDir, WorkspaceService.DECOMPILED_SOURCES_TAG);
    }

    /**
     * For minecraft-diff
     */
    public static void main2(String[] args) {
        Strata strata = new Strata("tools/strata/strata-cache");
        strata.init();

        String version = "26.1-pre-2";

        PistonVersionDetails ver = strata.getMojangService().getVersion(version);
        strata.getMojangService().downloadServerBundle(ver);
        strata.getExtractorService().extractServerBundle(ver.id());
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(ver.id()),
                ver.id()
        );

        String gitDir = "tools/strata/minecraft-diff/src";
        strata.getWorkspaceService().copyDecompiledSources(ver.id(), gitDir);

        sleep(1000);

        strata.getWorkspaceService().gitCommit(gitDir, "Update to " + version);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}
