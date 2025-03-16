package de.oliver.quicke2e;

import de.oliver.quicke2e.config.Configuration;
import de.oliver.quicke2e.config.Context;
import de.oliver.quicke2e.eula.EulaService;
import de.oliver.quicke2e.paper.PaperDownloadService;
import de.oliver.quicke2e.startScript.StartScriptService;

public class Main {

    public static void main(String[] args) {
        Configuration config = new Configuration(
                "paper",
                "1.21.4",
                "latest",
                new String[]{},
                true,
                new String[]{"OliverHD"},
                "25565"
        );

        Context context = new Context(config);

        PaperDownloadService paper = new PaperDownloadService();
//        paper.downloadServerFile(config.type(), config.version(), config.build());

        EulaService eula = new EulaService();
        if (config.eula()) {
            eula.setEulaToTrue(String.format("servers/%s_%s_%s/eula.txt", config.type(), config.version(), config.build()));
        }

        StartScriptService startScript = new StartScriptService();
        startScript.writeStartScript(context);
    }

}
