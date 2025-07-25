package com.fancyinnovations.fancyholograms.api.trait;

import com.fancyinnovations.fancyholograms.api.events.HologramTraitAttachedEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@HologramTraitClass(traitName = "trait_trait")
public class HologramTraitTrait extends HologramTrait {

    private final List<HologramTrait> traits;
    private Configuration configuration;

    public HologramTraitTrait(Hologram hologram) {
        this.configuration = new Configuration(new ArrayList<>());
        this.traits = new ArrayList<>();
        attachHologram(hologram);
    }

    public void addTrait(HologramTrait trait) {
        if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
            return;
        }

        trait.attachHologram(hologram);
        this.traits.add(trait);
        this.configuration.traits().add(trait.getName());
        try {
            storage.set(hologram.getData().getName(), configuration);
        } catch (IOException e) {
            logger.error("Failed to save configuration for HologramTraitTrait");
            logger.error(e);
        }
    }

    @Override
    public void onAttach() {
        // Attach all default traits to the hologram
        List<HologramTraitRegistry.TraitInfo> registeredTraits = api.getTraitRegistry().getTraits();
        for (HologramTraitRegistry.TraitInfo ti : registeredTraits) {
            if (!ti.isDefault()) {
                continue;
            }

            try {
                HologramTrait trait = ti.clazz().getConstructor().newInstance();
                if (!new HologramTraitAttachedEvent(hologram, trait, true).callEvent()) {
                    continue;
                }

                trait.attachHologram(hologram);
                this.traits.add(trait);
            } catch (Exception e) {
                logger.error("Failed to instantiate default trait " + ti.name());
                logger.error(e);
            }

            logger.debug("Attached default trait " + ti.name() + " to hologram " + hologram.getData().getName());
        }

        // Attach all traits that are already attached to the hologram
        try {
            configuration = storage.get(hologram.getData().getName(), Configuration.class);
        } catch (IOException e) {
            logger.error("Failed to load configuration for HologramTraitTrait");
            logger.error(e);
            return;
        }
        if (configuration == null) {
            return;
        }

        for (String traitName : configuration.traits()) {
            HologramTraitRegistry.TraitInfo traitInfo = api.getTraitRegistry().getTrait(traitName);
            if (traitInfo == null) {
                logger.warn("Trait " + traitName + " is not registered");
                continue;
            }

            try {
                HologramTrait trait = traitInfo.clazz().getConstructor().newInstance();

                if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
                    return;
                }

                trait.attachHologram(hologram);
                this.traits.add(trait);
            } catch (Exception e) {
                logger.error("Failed to instantiate trait " + traitName);
                logger.error(e);
            }
        }
    }

    @Override
    public void onSpawn(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onSpawn(player);
        }
    }

    @Override
    public void onDespawn(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onDespawn(player);
        }
    }

    @Override
    public void onUpdate(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onUpdate(player);
        }
    }

    @Override
    public void onModify() {
        for (HologramTrait trait : this.traits) {
            trait.onModify();
        }
    }

    @Override
    public void onRegister() {
        for (HologramTrait trait : this.traits) {
            trait.onRegister();
        }
    }

    @Override
    public void onUnregister() {
        for (HologramTrait trait : this.traits) {
            trait.onUnregister();
        }
    }

    @Override
    public void load() {
        for (HologramTrait trait : this.traits) {
            trait.load();
        }
    }

    @Override
    public void save() {
        for (HologramTrait trait : this.traits) {
            trait.save();
        }
    }

    public List<HologramTrait> getTraits() {
        return traits;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public record Configuration(
            List<String> traits
    ) {

    }
}
