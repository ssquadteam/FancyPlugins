package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerLoadedListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoaded(@NotNull final PlayerClientLoadedWorldEvent event) {
        for (final Hologram hologram : FancyHolograms.get().getRegistry().getAll()) {
            FancyHolograms.get().getController().refreshHologram(hologram, event.getPlayer());
        }
    }
}
