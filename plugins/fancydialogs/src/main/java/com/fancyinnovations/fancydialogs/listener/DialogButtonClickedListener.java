package com.fancyinnovations.fancydialogs.listener;

import com.fancyinnovations.fancydialogs.api.ConfirmationDialog;
import com.fancyinnovations.fancydialogs.api.events.DialogButtonClickedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DialogButtonClickedListener implements Listener {

    @EventHandler
    public void onButtonClicked(DialogButtonClickedEvent event) {
        if (event.getDialogId().startsWith("confirmation_dialog_")) {
            ConfirmationDialog dialog = ConfirmationDialog.CACHE.get(event.getDialogId());
            if (dialog == null) {
                return;
            }

            if (event.getButtonId().equals(dialog.getConfirmButtonId())) {
                dialog.getOnConfirm().run();
                ConfirmationDialog.CACHE.remove(event.getDialogId());
            } else if (event.getButtonId().equals(dialog.getCancelButtonId())) {
                dialog.getOnCancel().run();
                ConfirmationDialog.CACHE.remove(event.getDialogId());
            }
        }
    }

}
