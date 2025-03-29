package de.oliver.deployment.git;

import java.io.IOException;

public class GitService {

    public static String getCommitHash() {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "rev-parse", "HEAD");
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new java.io.File("."));

        try {
            Process process = processBuilder.start();
            return new String(process.getInputStream().readAllBytes()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "unknown";
    }

    public static String getCommitMessage() {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=%B");
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new java.io.File("."));

        try {
            Process process = processBuilder.start();
            return new String(process.getInputStream().readAllBytes()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "unknown";
    }

}
