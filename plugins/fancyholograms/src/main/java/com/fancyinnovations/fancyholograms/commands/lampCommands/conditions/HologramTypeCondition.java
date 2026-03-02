package com.fancyinnovations.fancyholograms.commands.lampCommands.conditions;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.process.CommandCondition;

import java.util.Arrays;
import java.util.List;

public class HologramTypeCondition implements CommandCondition<BukkitCommandActor> {

    public static final HologramTypeCondition INSTANCE = new HologramTypeCondition();

    private HologramTypeCondition() {

    }

    @Override
    public void test(@NotNull ExecutionContext<BukkitCommandActor> context) {
        boolean isOnlyForTypes = context.command().annotations().contains(IsHologramType.class);
        if (!isOnlyForTypes) {
            return;
        }
        HologramType[] allowedTypes = context.command().annotations().get(IsHologramType.class).types();

        Hologram hologram = context.getResolvedArgument("hologram");
        boolean hologramIsOfAllowedType = false;
        for (HologramType allowedType : allowedTypes) {
            if (hologram.getData().getType() == allowedType) {
                hologramIsOfAllowedType = true;
                break;
            }
        }


        if (!hologramIsOfAllowedType) {

            List<String> allowedTypeNames = Arrays.stream(allowedTypes)
                    .map(HologramType::name)
                    .toList();
            throw new CommandErrorException("This command can only be executed on holograms of the following types: " + String.join(", ", allowedTypeNames));
        }
    }
}
