package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.World;

public class WorldCreateCMD {

    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(this::execute)
                );
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        String worldName = StringArgumentType.getString(ctx, "name");
        FWorldImpl fWorld = new FWorldImpl(worldName);
        World world = fWorld.toWorldCreator().createWorld();

        ctx.getSource().getSender().sendPlainMessage("World '" + world.getName() + "' created successfully!");
        return Command.SINGLE_SUCCESS;
    }

}
