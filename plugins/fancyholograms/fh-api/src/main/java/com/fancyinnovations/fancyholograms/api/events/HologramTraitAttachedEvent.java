package com.fancyinnovations.fancyholograms.api.events;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HologramTraitAttachedEvent extends HologramEvent {

    private static final HandlerList handlerList = new HandlerList();

    @NotNull
    private final HologramTrait trait;
    private final boolean isDefaultTrait;


    public HologramTraitAttachedEvent(@NotNull final Hologram hologram, @NotNull final HologramTrait trait, boolean isDefaultTrait) {
        super(hologram, !Bukkit.isPrimaryThread());
        this.trait = trait;
        this.isDefaultTrait = isDefaultTrait;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull HologramTrait getTrait() {
        return trait;
    }

    public boolean isDefaultTrait() {
        return isDefaultTrait;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
