package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.GameRule;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldGamerulesCMD extends FancyContext {

    public static final WorldGamerulesCMD INSTANCE = new WorldGamerulesCMD();

    @Command({"world gamerules list", "gamerules"})
    @Description("Shows all gamerules of the world")
    @CommandPermission("fancyworlds.commands.world.gamerules.list")
    public void list(
            BukkitCommandActor actor,
            FWorld world
    ) {
        // TODO: Show all gamerules of the world
    }

    @Command({"world gamerules set", "gamerules set"})
    @Description("Sets a gamerule of the world")
    @CommandPermission("fancyworlds.commands.world.gamerules.set")
    public void set(
            BukkitCommandActor actor,
            FWorld world,
            GameRule<?> gamerule,
            String value
    ) {
        // TODO: Set the gamerule of the world
    }

    @Command({"world gamerules reset", "gamerules reset"})
    @Description("Resets a gamerule of the world to its default value")
    @CommandPermission("fancyworlds.commands.world.gamerules.reset")
    public void reset(
            BukkitCommandActor actor,
            FWorld world,
            GameRule<?> gamerule
    ) {
        // TODO: Reset the gamerule of the world to its default value
    }

    @Command({"world gamerules reset", "gamerules reset_all"})
    @Description("Resets all gamerules of the world to their default values")
    @CommandPermission("fancyworlds.commands.world.gamerules.reset_all")
    public void resetAll(
            BukkitCommandActor actor,
            FWorld world
    ) {
        // TODO: Reset all gamerules of the world to their default values
    }
}
