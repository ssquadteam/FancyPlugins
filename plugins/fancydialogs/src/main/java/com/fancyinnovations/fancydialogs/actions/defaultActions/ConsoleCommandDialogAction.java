package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleCommandDialogAction implements DialogAction {

    public static final ConsoleCommandDialogAction INSTANCE = new ConsoleCommandDialogAction();

    private ConsoleCommandDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        String command = data;
        if (player != null) {
            command = data.replace("{player}", player.getName());
        }

        String finalCommand = command;

        // Execute the command on the server console
        Bukkit.getScheduler().runTask(FancyDialogsPlugin.get(), () -> {
            try {
                player.getServer().dispatchCommand(player.getServer().getConsoleSender(), finalCommand);
            } catch (Exception e) {
                player.sendMessage("§cFailed to execute command: " + finalCommand);
            }
        });
    }
}
