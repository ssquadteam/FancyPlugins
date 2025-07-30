package com.fancyinnovations.fancyholograms.trait.builtin;

import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@HologramTraitClass(traitName = "debug_trait")
public class DebugTrait extends HologramTrait {

    @Override
    public void onAttach() {
        logger.info("DebugTrait onAttach");
    }

    @Override
    public void onSpawn(Player player) {
        logger.info("DebugTrait onSpawn for player: " + player.getName());
    }

    @Override
    public void onDespawn(Player player) {
        logger.info("DebugTrait onDespawn for player: " + player.getName());
    }

    @Override
    public void onUpdate(Player player) {
        logger.info("DebugTrait onUpdate for player: " + player.getName());
    }

    @Override
    public void onModify() {
        logger.info("DebugTrait onModify");
    }

    @Override
    public void onRegister() {
        logger.info("DebugTrait onRegister");
    }

    @Override
    public void onUnregister() {
        logger.info("DebugTrait onUnregister");
    }

    @Override
    public void load() {
        logger.info("DebugTrait load");
    }

    @Override
    public void save() {
        logger.info("DebugTrait save");
    }
}
