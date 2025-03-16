package de.oliver.quicke2e;

import de.oliver.quicke2e.config.Configuration;
import de.oliver.quicke2e.config.Context;
import de.oliver.quicke2e.steps.eula.EulaService;
import de.oliver.quicke2e.steps.paper.PaperDownloadService;
import de.oliver.quicke2e.steps.startScript.StartScriptService;
import de.oliver.quicke2e.steps.startServer.StartServerService;

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
        paper.downloadServerFile(context);

        EulaService eula = new EulaService();
        if (config.eula()) {
            eula.setEulaToTrue(String.format("%s/eula.txt", context.serverEnvPath().toString()));
        }

        StartScriptService startScript = new StartScriptService();
        startScript.writeStartScript(context);

        StartServerService startServer = new StartServerService();
        startServer.startServer(context);
    }

}
