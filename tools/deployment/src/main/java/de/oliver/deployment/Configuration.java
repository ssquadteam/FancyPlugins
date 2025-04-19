package de.oliver.deployment;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record Configuration(
        @SerializedName("project_name") String projectName,
        @SerializedName("project_id") String projectID,
        @SerializedName("plugin_jar_path") String pluginJarPath,
        @SerializedName("changelog_path") String changeLogPath,
        @SerializedName("version_path") String versionPath,
        @SerializedName("supported_versions") String[] supportedVersions,
        String channel,
        @SerializedName("loaders") String[] loaders,
        boolean featured
) {

    public String readVersion() {
        try {
            return Files.readString(Path.of(versionPath));
        } catch (IOException e) {
            return "unknown";
        }
    }

}
