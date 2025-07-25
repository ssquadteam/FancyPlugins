package com.fancyinnovations.fancyholograms.api.trait;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Experimental
public interface HologramTraitRegistry {

    @ApiStatus.Experimental
    void register(Class<? extends HologramTrait> trait);

    @ApiStatus.Experimental
    void unregister(Class<? extends HologramTrait> trait);

    @ApiStatus.Experimental
    boolean isRegistered(Class<? extends HologramTrait> trait);

    @ApiStatus.Experimental
    TraitInfo getTrait(String name);

    @ApiStatus.Experimental
    List<TraitInfo> getTraits();

    public record TraitInfo(
            String name,
            Class<? extends HologramTrait> clazz,
            boolean isDefault
    ) {

    }
}
