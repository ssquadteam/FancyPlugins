package com.fancyinnovations.strata;

import com.fancyinnovations.strata.mojang.PistonVersionDetails;
import com.fancyinnovations.strata.workspace.WorkspaceService;

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

        String gitDir = "tools/strata/strata-sources/" + latest.id() + "/src/main/java";
        strata.getWorkspaceService().initGitDirectory(gitDir);

        strata.getWorkspaceService().copyDecompiledSources(latest.id(), gitDir);
        strata.getWorkspaceService().gitCommit(gitDir, "Add decompiled sources");
        strata.getWorkspaceService().gitTag(gitDir, WorkspaceService.DECOMPILED_SOURCES_TAG);
    }

}
