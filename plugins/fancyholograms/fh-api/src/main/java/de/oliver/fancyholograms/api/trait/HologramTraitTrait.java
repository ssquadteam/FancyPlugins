package de.oliver.fancyholograms.api.trait;

import de.oliver.fancyholograms.api.events.HologramTraitAttachedEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@HologramTraitClass(traitName = "trait_trait")
public class HologramTraitTrait extends HologramTrait {

    private final List<HologramTrait> traits;

    public HologramTraitTrait(Hologram hologram) {
        this.traits = new ArrayList<>();
        attachHologram(hologram);
    }

    public void addTrait(HologramTrait trait) {
        if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
            return;
        }

        trait.attachHologram(hologram);
        this.traits.add(trait);
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
                if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
                    continue;
                }

                trait.attachHologram(hologram);
                this.traits.add(trait);
            } catch (Exception e) {
                logger.error("Failed to instantiate trait " + ti.name());
                logger.error(e);
            }

            logger.debug("Attached default trait " + ti.name() + " to hologram " + hologram.getData().getName());
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
}
