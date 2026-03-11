package com.fancyinnovations.fancyworlds.commands.other;

import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.World;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SeedCMD extends FancyContext {

    public static final SeedCMD INSTANCE = new SeedCMD();

    @Command("seed")
    @Description("Shows the seed of the world")
    @CommandPermission("fancyworlds.commands.world.unlink")
    public void seed(
            final BukkitCommandActor actor,
            @Optional World world
    ) {
        if (world == null) {
            world = actor.requirePlayer().getWorld();
        }

        long seed = world.getSeed();

        translator.translate("commands.seed.result")
                .withPrefix()
                .replace("worldName", world.getName())
                .replace("seed", String.valueOf(seed))
                .send(actor.sender());
    }
}
