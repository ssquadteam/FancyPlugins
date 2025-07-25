package com.fancyinnovations.fancyholograms.trait;

import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitClass;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;

import java.util.HashMap;
import java.util.List;

public class HologramTraitRegistryImpl implements HologramTraitRegistry {

    private final HashMap<String, TraitInfo> traits;

    public HologramTraitRegistryImpl() {
        this.traits = new HashMap<>();
    }

    @Override
    public void register(Class<? extends HologramTrait> trait) {
        TraitInfo info = getInfo(trait);
        traits.put(info.name(), info);
    }

    @Override
    public void unregister(Class<? extends HologramTrait> trait) {
        TraitInfo info = getInfo(trait);
        traits.remove(info.name());
    }

    @Override
    public boolean isRegistered(Class<? extends HologramTrait> trait) {
        TraitInfo info = getInfo(trait);
        return traits.containsKey(info.name());
    }

    @Override
    public TraitInfo getTrait(String name) {
        return traits.get(name);
    }

    @Override
    public List<TraitInfo> getTraits() {
        return List.copyOf(traits.values());
    }

    private TraitInfo getInfo(Class<? extends HologramTrait> trait) {
        if (trait.isAnnotationPresent(HologramTraitClass.class)) {
            String name = trait.getAnnotation(HologramTraitClass.class).traitName();
            boolean isDefault = trait.getAnnotation(HologramTraitClass.class).defaultTrait();
            return new TraitInfo(name, trait, isDefault);
        }

        throw new IllegalArgumentException("Trait class " + trait.getName() + " is not annotated with HologramTraitClass");
    }
}
