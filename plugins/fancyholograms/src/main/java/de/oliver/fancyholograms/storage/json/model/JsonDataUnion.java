package de.oliver.fancyholograms.storage.json.model;

public record JsonDataUnion(
        JsonHologramData hologram_data,
        JsonDisplayHologramData display_data,

        JsonTextHologramData text_data,
        JsonItemHologramData item_data,
        JsonBlockHologramData block_data
) {
}
