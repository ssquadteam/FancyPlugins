package de.oliver.quicke2e.paper;

import com.google.gson.annotations.SerializedName;

public record BuildsResponse(
        @SerializedName("project_id") String projectID,
        @SerializedName("project_name") String projectName,
        String version,
        String[] builds
) {
}
