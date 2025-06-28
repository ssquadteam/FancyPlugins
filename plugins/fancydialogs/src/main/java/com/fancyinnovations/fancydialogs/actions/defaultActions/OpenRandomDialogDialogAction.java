package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import org.bukkit.entity.Player;

public class OpenRandomDialogDialogAction implements DialogAction {

    public static final OpenRandomDialogDialogAction INSTANCE = new OpenRandomDialogDialogAction();

    private OpenRandomDialogDialogAction() {
    }

    private static String pickRandomDialogId(String[] ids) {
        int randomIndex = (int) (Math.random() * ids.length);
        return ids[randomIndex].trim();
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        String[] ids = data.split(",");
        if (ids.length == 0) {
            FancyDialogsPlugin.get().getFancyLogger().warn("No dialog IDs provided in data: " + data);
            return;
        }

        String randomDialogId = pickRandomDialogId(ids);

        Dialog targetDialog = FancyDialogsPlugin.get().getDialogRegistry().get(randomDialogId);
        if (targetDialog == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Dialog with ID '" + data + "' not found.");
            return;
        }

        targetDialog.open(player);
    }
}
