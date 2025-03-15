package de.oliver.deployment.modrinth;

import com.google.gson.annotations.SerializedName;

public record CreateVersionRequest(
        String name,
        @SerializedName("version_number") String versionName,
        String changelog,
        String[] dependencies,
        @SerializedName("game_versions") String[] gameVersions,
        @SerializedName("version_type") String versionType,
        String[] loaders,
        boolean featured,
        String status,
        @SerializedName("requested_status") String requestedStatus,
        @SerializedName("project_id") String projectId,
        @SerializedName("file_parts") String[] fileParts,
        @SerializedName("primary_file") String primaryFile
) {
}
