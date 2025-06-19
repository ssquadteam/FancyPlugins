package com.fancyinnovations.fancydialogs.actions;

import com.fancyinnovations.fancydialogs.api.Dialog;
import org.bukkit.entity.Player;

public interface DialogAction {

    void execute(Player player, Dialog dialog, String data);

}
