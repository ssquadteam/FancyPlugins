package de.oliver.quicke2e;

import de.oliver.quicke2e.config.Configuration;
import de.oliver.quicke2e.paper.PaperDownloadService;

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

        PaperDownloadService paper = new PaperDownloadService();
        paper.downloadServerFile(config.type(), config.version(), config.build());
    }

}
