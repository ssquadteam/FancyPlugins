package de.oliver.deployment;

import com.google.gson.annotations.SerializedName;

public record Configuration(
        @SerializedName("project_id") String projectID,
        @SerializedName("plugin_jar_path") String pluginJarPath,
        String version,
        @SerializedName("supported_versions") String[] supportedVersions,
        String channel,
        @SerializedName("loaders") String[] loaders,
        boolean featured
) {
}
