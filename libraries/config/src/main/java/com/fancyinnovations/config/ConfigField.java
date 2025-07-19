package com.fancyinnovations.config;

public record ConfigField<T>(
        String path,
        String description,
        T defaultValue,
        Class<T> type
) {
}
