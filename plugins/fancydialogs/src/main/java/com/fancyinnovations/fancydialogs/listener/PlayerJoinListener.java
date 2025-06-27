package com.fancyinnovations.fancydialogs.listener;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CustomClickActionPacketListener.get().getPacketListener().inject(event.getPlayer());

        boolean hasJoinedBefore = FancyDialogsPlugin.get().getJoinedPlayersCache().checkIfPlayerJoined(event.getPlayer().getUniqueId());
        if (FancyDialogsPlugin.get().getFancyDialogsConfig().getLogLevel().equalsIgnoreCase("debug")) {
            hasJoinedBefore = false;
        }

        if (!hasJoinedBefore) {
            String welcomeDialogID = FancyDialogsPlugin.get().getFancyDialogsConfig().getWelcomeDialogID();
            Dialog dialog = FancyDialogsPlugin.get().getDialogRegistry().get(welcomeDialogID);
            if (dialog != null) {
                dialog.open(event.getPlayer());
            } else {
                FancyDialogsPlugin.get().getLogger().warning("Welcome dialog with ID " + welcomeDialogID + " not found.");
                return;
            }

            FancyDialogsPlugin.get().getJoinedPlayersCache().addPlayer(event.getPlayer().getUniqueId());
            FancyDialogsPlugin.get().getFancyLogger().debug("Player " + event.getPlayer().getName() + " has joined for the first time and the welcome dialog has been opened.");
        }
    }

}
