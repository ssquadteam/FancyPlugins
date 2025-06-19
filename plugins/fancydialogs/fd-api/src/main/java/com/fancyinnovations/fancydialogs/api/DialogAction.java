package com.fancyinnovations.fancydialogs.api;

import org.bukkit.entity.Player;

public interface DialogAction {

    void execute(Player player, Dialog dialog, String data);

}
