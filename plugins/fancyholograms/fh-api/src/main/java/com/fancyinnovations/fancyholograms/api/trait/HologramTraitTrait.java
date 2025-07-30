package com.fancyinnovations.fancyholograms.api.trait;

import com.fancyinnovations.fancyholograms.api.events.HologramTraitAttachedEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@HologramTraitClass(traitName = "trait_trait")
public class HologramTraitTrait extends HologramTrait {

    private final List<HologramTrait> traits;

    public HologramTraitTrait() {
        this.traits = new ArrayList<>();
    }

    public void addTrait(HologramTrait trait) {
        this.traits.add(trait);

        // Attach the trait to the hologram if hologram already exists
        if (hologram != null) {
            if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
                return;
            }

            trait.attachHologram(hologram);
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
                this.traits.add(trait);
            } catch (Exception e) {
                logger.error("Failed to instantiate default trait " + ti.name());
                logger.error(e);
            }

            logger.debug("Attached default trait " + ti.name() + " to hologram " + hologram.getData().getName());
        }

        // Attach all traits that were added to the hologram
        for (HologramTrait trait : traits) {
            if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
                continue;
            }

            trait.attachHologram(hologram);
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

}
