package de.oliver.fancyholograms.storage.json;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.storage.HologramStorage;
import de.oliver.fancyholograms.storage.json.model.JsonDataUnion;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonStorage implements HologramStorage {

    private final JDB jdb;

    public JsonStorage() {
        this.jdb = new JDB("plugins/FancyHolograms/data/holograms");
    }

    @Override
    public void saveBatch(Collection<HologramData> holograms) {
        for (HologramData hologram : holograms) {
           save(hologram);
        }
    }

    @Override
    public void save(HologramData hologram) {
        JsonDataUnion union = switch (hologram.getType()) {
            case TEXT -> JsonAdapter.toUnion((TextHologramData) hologram);
            case ITEM -> JsonAdapter.toUnion((ItemHologramData) hologram);
            case BLOCK -> JsonAdapter.toUnion((BlockHologramData) hologram);
        };

        try {
            jdb.set(getKey(hologram), union);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to save hologram " + hologram.getName());
            FancyHolograms.get().getFancyLogger().error(e);
        }
    }

    @Override
    public void delete(HologramData hologram) {
        jdb.delete(getKey(hologram));
    }

    @Override
    public Collection<HologramData> loadAll(String world) {
        List<HologramData> holograms = new ArrayList<>();

        try {
            List<JsonDataUnion> allTextUnions = jdb.getAll("worlds/" + world + "/text", JsonDataUnion.class);
            allTextUnions.forEach(u -> holograms.add(JsonAdapter.fromJson(u)));

            List<JsonDataUnion> allItemUnions = jdb.getAll("worlds/" + world + "/item", JsonDataUnion.class);
            allItemUnions.forEach(u -> holograms.add(JsonAdapter.fromJson(u)));

            List<JsonDataUnion> allBlockUnions = jdb.getAll("worlds/" + world + "/block", JsonDataUnion.class);
            allBlockUnions.forEach(u -> holograms.add(JsonAdapter.fromJson(u)));
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms from world " + world);
            FancyHolograms.get().getFancyLogger().error(e);
        }

        return holograms;
    }

    public String getKey(HologramData data) {
        return "worlds/" + data.getLocation().getWorld().getName() + "/" + data.getType().toString().toLowerCase() + "/" + data.getName();
    }
}
