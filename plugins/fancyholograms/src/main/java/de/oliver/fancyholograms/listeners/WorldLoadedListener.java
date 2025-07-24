package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadedListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (Hologram hologram : FancyHologramsPlugin.get().getRegistry().getAll()) {
            HologramData data = hologram.getData();

            if (data.getLocation().getWorld() == null && data.getWorldName().equals(event.getWorld().getName())) {
                data.getLocation().setWorld(event.getWorld());
            }
        }
    }

}
