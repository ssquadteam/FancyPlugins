package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramCreateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(sender.hasPermission("fancyholograms.hologram.create"))) {
            MessageHelper.error(sender, "You don't have the required permission to create a hologram");
            return false;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }
        if (args.length < 3) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        HologramType type = HologramType.getByName(args[1]);
        if (type == null) {
            MessageHelper.error(player, "Could not find type: " + args[1]);
            return false;
        }

        String name = args[2];

        if (FancyHologramsPlugin.get().getRegistry().get(name).isPresent()) {
            MessageHelper.error(player, "There already exists a hologram with this name");
            return false;
        }

        if (name.contains(".")) {
            MessageHelper.error(player, "The name of the hologram cannot contain a dot");
            return false;
        }

        DisplayHologramData displayData = null;
        switch (type) {
            case TEXT -> displayData = new TextHologramData(name, player.getLocation());
            case ITEM -> {
                displayData = new ItemHologramData(name, player.getLocation());
                displayData.setBillboard(Display.Billboard.FIXED);
            }
            case BLOCK -> {
                displayData = new BlockHologramData(name, player.getLocation());
                displayData.setBillboard(Display.Billboard.FIXED);
            }
        }
        displayData.setFilePath(name);

        final var holo = FancyHologramsPlugin.get().getHologramFactory().apply(displayData);
        if (!new HologramCreateEvent(holo, player).callEvent()) {
            MessageHelper.error(player, "Creating the hologram was cancelled");
            return false;
        }

        FancyHologramsPlugin.get().getController().refreshHologram(holo, Bukkit.getOnlinePlayers());

        FancyHologramsPlugin.get().getRegistry().register(holo);

        MessageHelper.success(player, "Created the hologram");
        return true;
    }
}
