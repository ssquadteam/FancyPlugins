package de.oliver.fancylib.storage;

/**
 * Enumeration of supported storage types
 */
public enum StorageType {
    /**
     * YAML file-based storage (legacy)
     */
    YAML("yaml"),
    
    /**
     * JSON file-based storage
     */
    JSON("json"),
    
    /**
     * Database storage (MySQL, PostgreSQL, SQLite)
     */
    DATABASE("database"),
    
    /**
     * Hybrid storage - database with YAML fallback
     */
    HYBRID("hybrid");

    private final String identifier;

    StorageType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static StorageType fromString(String type) {
        for (StorageType storageType : values()) {
            if (storageType.identifier.equalsIgnoreCase(type)) {
                return storageType;
            }
        }
        throw new IllegalArgumentException("Unknown storage type: " + type);
    }
}
