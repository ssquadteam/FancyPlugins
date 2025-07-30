package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TraitsCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (args.length == 4) {
            return List.of("add", "remove");
        } else if (args.length == 5) {
            return FancyHolograms.get().getTraitRegistry().getTraits()
                    .stream()
                    .filter(ti -> !ti.isDefault())
                    .filter(ti -> {
                        if (args[3].equalsIgnoreCase("add")) {
                            return !hologram.getData().getTraitTrait().isTraitAttached(ti.clazz());
                        } else if (args[3].equalsIgnoreCase("remove")) {
                            return hologram.getData().getTraitTrait().isTraitAttached(ti.clazz());
                        }

                        return true;
                    })
                    .map(HologramTraitRegistry.TraitInfo::name)
                    .toList();
        }

        return List.of();
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(player.hasPermission("fancyholograms.hologram.edit.traits"))) {
            MessageHelper.error(player, "You don't have the required permission to change traits of a hologram.");
            return false;
        }

        // /hologram edit <name> traits <add|remove> <trait name>

        if (args.length < 5) {
            MessageHelper.error(player, "Usage: /hologram edit <name> traits <add|remove> <trait name>");
            return false;
        }

        String action = args[3];
        String traitName = args[4];
        if (traitName == null || traitName.isEmpty()) {
            MessageHelper.error(player, "You must specify a trait name.");
            return false;
        }

        HologramTraitRegistry.TraitInfo traitInfo = FancyHolograms.get().getTraitRegistry().getTrait(traitName);
        if (traitInfo == null) {
            MessageHelper.error(player, "Trait '" + traitName + "' does not exist.");
            return false;
        }

        switch (action.toLowerCase()) {
            case "add": {
                if (hologram.getData().getTraitTrait().isTraitAttached(traitInfo.clazz())) {
                    MessageHelper.error(player, "Trait '" + traitName + "' is already attached to hologram '" + hologram.getData().getName() + "'.");
                    return false;
                }

                hologram.getData().getTraitTrait().addTrait(traitInfo.clazz());
                MessageHelper.success(player, "Trait '" + traitName + "' has been added to hologram '" + hologram.getData().getName() + "'.");
                return true;
            }
            case "remove": {
                if (!hologram.getData().getTraitTrait().isTraitAttached(traitInfo.clazz())) {
                    MessageHelper.error(player, "Trait '" + traitName + "' is not attached to hologram '" + hologram.getData().getName() + "'.");
                    return false;
                }

                hologram.getData().getTraitTrait().removeTrait(traitInfo.clazz());
                MessageHelper.success(player, "Trait '" + traitName + "' has been removed from hologram '" + hologram.getData().getName() + "'.");
                return true;
            }
            default: {
                MessageHelper.error(player, "Invalid action. Use 'add' or 'remove'.");
                return false;
            }
        }
    }
}
