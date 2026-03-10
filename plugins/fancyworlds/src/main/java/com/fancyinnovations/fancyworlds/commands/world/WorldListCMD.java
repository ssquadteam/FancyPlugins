package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;

public class WorldListCMD extends FancyContext {

    public static final WorldListCMD INSTANCE = new WorldListCMD();

    @Command({"world list", "worlds"})
    @Description("Shows a list of all loaded worlds")
    @CommandPermission("fancyworlds.commands.world.list")
    public void version(
            final BukkitCommandActor actor
    ) {
        WorldService service = WorldService.get();
        Collection<FWorld> worlds = service.getAllWorlds();

        if (worlds.isEmpty()) {
            translator.translate("commands.world.list.no_worlds")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.world.list.header")
                .withPrefix()
                .replace("worldCount", String.valueOf(worlds.size()))
                .send(actor.sender());

        for (FWorld world : worlds) {
            if (world.isWorldLoaded()) {
                translator.translate("commands.world.list.entry_loaded")
                        .replace("worldName", world.getName())
                        .replace("playerCount", String.valueOf(world.getBukkitWorld().getPlayerCount()))
                        .replace("entityCount", String.valueOf(world.getBukkitWorld().getEntityCount()))
                        .replace("chunkCount", String.valueOf(world.getBukkitWorld().getChunkCount()))
                        .send(actor.sender());
            } else {
                translator.translate("commands.world.list.entry_unloaded")
                        .replace("worldName", world.getName())
                        .send(actor.sender());
            }
        }
    }

}
