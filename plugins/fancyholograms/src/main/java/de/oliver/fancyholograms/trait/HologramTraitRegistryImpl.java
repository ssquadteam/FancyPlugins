package de.oliver.fancyholograms.trait;

import de.oliver.fancyholograms.api.trait.HologramTrait;
import de.oliver.fancyholograms.api.trait.HologramTraitRegistry;

import java.util.HashSet;
import java.util.Set;

public class HologramTraitRegistryImpl implements HologramTraitRegistry {

    private final Set<Class<? extends HologramTrait>> traits;

    public HologramTraitRegistryImpl() {
        this.traits = new HashSet<>();
    }

    @Override
    public boolean register(Class<? extends HologramTrait> trait) {
        return traits.add(trait);
    }

    @Override
    public boolean unregister(Class<? extends HologramTrait> trait) {
        return traits.remove(trait);
    }

    @Override
    public boolean isRegistered(Class<? extends HologramTrait> trait) {
        return traits.contains(trait);
    }

    @Override
    public Set<Class<? extends HologramTrait>> getRegisteredTraits() {
        return Set.copyOf(traits);
    }
}
