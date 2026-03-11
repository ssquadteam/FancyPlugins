package com.fancyinnovations.fancyholograms.listeners;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.events.HologramSpawnEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.event.bedrock.SessionJoinEvent;

import java.util.concurrent.TimeUnit;

public class BedrockPlayerListener implements Listener {

    @EventHandler
    public void onHologramShow(final HologramSpawnEvent event) {
        if (!FancyHologramsPlugin.get().getFHConfiguration().isHologramsForBedrockPlayersEnabled() && PluginUtils.isFloodgateEnabled()) {
            boolean isBedrockPlayer = FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
            if (isBedrockPlayer) {
                FancyHolograms.get().getFancyLogger().debug("Cancelled hologram spawn for Bedrock player: " + event.getPlayer().getName());
                event.setCancelled(true);
            }
        }
    }

    @Subscribe
    public void onBedrockPlayerJoin(final SessionJoinEvent event) {
        String username = event.connection().javaUsername();
        Player player = Bukkit.getPlayer(username);
        if (player == null) {
            FancyHolograms.get().getFancyLogger().debug("Could not find player object for Bedrock player: " + username);
            return;
        }

        FancyHologramsPlugin.get().getHologramThread().schedule(() -> {
                    for (Hologram hologram : FancyHolograms.get().getRegistry().getAll()) {
                        hologram.despawnFrom(player);
                        hologram.removeViewer(player.getUniqueId());
                        FancyHologramsPlugin.get().getController().refreshHologram(hologram, player);
                    }
                },
                FancyHologramsPlugin.get().getHologramConfiguration().getSpawnDelayOnJoin(),
                TimeUnit.MILLISECONDS
        );
    }

}
