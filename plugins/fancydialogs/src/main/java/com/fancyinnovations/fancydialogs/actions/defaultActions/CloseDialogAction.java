package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.actions.DialogAction;
import com.fancyinnovations.fancydialogs.api.Dialog;
import org.bukkit.entity.Player;

public class CloseDialogAction implements DialogAction {

    public static final CloseDialogAction INSTANCE = new CloseDialogAction();

    private CloseDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        dialog.close(player);
    }

}
