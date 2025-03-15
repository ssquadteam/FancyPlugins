package de.oliver.deployment;

import com.google.gson.annotations.SerializedName;

public record Configuration(
        @SerializedName("project_id") String projectID,
        @SerializedName("plugin_jar_path") String pluginJarPath,
        @SerializedName("changelog_path") String changeLogPath,
        @SerializedName("version_path") String versionPath,
        @SerializedName("supported_versions") String[] supportedVersions,
        String channel,
        @SerializedName("loaders") String[] loaders,
        boolean featured
) {
}
