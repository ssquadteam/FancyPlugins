package de.oliver.quicke2e.steps.startServer;

import de.oliver.quicke2e.config.Context;

public class StartServerService {

    public void startServer(Context context) {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", context.serverFileName());
        processBuilder.directory(context.serverEnvPath().toFile());
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            processBuilder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
