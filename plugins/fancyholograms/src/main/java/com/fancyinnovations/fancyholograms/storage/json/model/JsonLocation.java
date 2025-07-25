package com.fancyinnovations.fancyholograms.storage.json.model;

public record JsonLocation(
        String world,
        Double x,
        Double y,
        Double z,
        Float yaw,
        Float pitch
) {
}
