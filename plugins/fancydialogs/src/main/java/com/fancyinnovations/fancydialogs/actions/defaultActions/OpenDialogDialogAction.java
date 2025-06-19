package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import org.bukkit.entity.Player;

public class OpenDialogDialogAction implements DialogAction {

    public static final OpenDialogDialogAction INSTANCE = new OpenDialogDialogAction();

    private OpenDialogDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        Dialog targetDialog = FancyDialogsPlugin.get().getDialogRegistry().get(data);
        if (targetDialog == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Dialog with ID '" + data + "' not found.");
            return;
        }

        targetDialog.open(player);
    }

}
