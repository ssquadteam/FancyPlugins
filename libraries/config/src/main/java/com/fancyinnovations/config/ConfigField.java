package com.fancyinnovations.config;

public record ConfigField<T>(
        String path,
        String description,
        boolean forRemoval,
        T defaultValue,
        Class<T> type
) {
}
