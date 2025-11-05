package com.fancyinnovations.fancydialogs.listener;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
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
                if (dialog.getExpectedUserInput() != null && !dialog.getExpectedUserInput().isEmpty()) {
                    if (!event.getPayload().containsKey("confirmation_user_input")) {
                        FancyDialogsPlugin.get().getFancyLogger().warn("Confirmation dialog expected user input but none was provided.");
                        return;
                    }

                    String userInput = event.getPayload().get("confirmation_user_input");
                    if (!userInput.equals(dialog.getExpectedUserInput())) {
                        FancyDialogsPlugin.get().getTranslator()
                                .translate("confirmation_dialog.input_mismatch")
                                .send(event.getPlayer());
                        return;
                    }
                }

                dialog.getOnConfirm().run();
                ConfirmationDialog.CACHE.remove(event.getDialogId());
            } else if (event.getButtonId().equals(dialog.getCancelButtonId())) {
                dialog.getOnCancel().run();
                ConfirmationDialog.CACHE.remove(event.getDialogId());
            }
        }
    }

}
