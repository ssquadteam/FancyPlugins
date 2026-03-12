package com.fancyinnovations.strata;

import com.fancyinnovations.strata.mojang.PistonVersionDetails;

public class Main {

    public static void main(String[] args) {
        Strata strata = new Strata("tools/strata/strata-cache");
        strata.init();

        PistonVersionDetails latest = strata.getMojangService().getLatestSnapshot();
        strata.getMojangService().downloadServerBundle(latest);
        strata.getExtractorService().extractServerBundle(latest.id());
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(latest.id()),
                "tools/strata/strata-cache/decompiled/" + latest.id()
        );
        strata.getWorkspaceService().copyDecompiledSources("tools/strata/strata-cache/decompiled/" + latest.id(), "tools/strata/strata-sources/" + latest.id() + "/src/main/java");
    }

}
