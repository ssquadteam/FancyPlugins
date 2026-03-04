package com.fancyinnovations.fancyworlds.commands.world;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WorldCMD {

    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("world")
                .then(new WorldCreateCMD().getCommand())
                .build();
    }

}
