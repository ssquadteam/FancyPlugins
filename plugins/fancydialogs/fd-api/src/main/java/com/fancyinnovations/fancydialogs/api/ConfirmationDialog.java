package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmationDialog {

    public static final Map<String, ConfirmationDialog> CACHE = new ConcurrentHashMap<>();

    private final String question;
    private String title;
    private String confirmText;
    private String cancelText;

    private Dialog dialog;
    private String confirmButtonId;
    private String cancelButtonId;
    private Runnable onConfirm;
    private Runnable onCancel;

    public ConfirmationDialog(String title, String question, String confirmText, String cancelText, Runnable onConfirm, Runnable onCancel) {
        this.title = title;
        this.question = question;
        this.confirmText = confirmText;
        this.cancelText = cancelText;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    public ConfirmationDialog(String question) {
        this.question = question;
    }

    public ConfirmationDialog withTitle(String title) {
        this.title = title;
        return this;
    }

    public ConfirmationDialog withConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    public ConfirmationDialog withCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public ConfirmationDialog withOnConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    public ConfirmationDialog withOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public void ask(Player player) {
        buildDialog();
        dialog.open(player);
        CACHE.put(dialog.getId(), this);
    }

    private void buildDialog() {
        if (title == null || title.isEmpty()) {
            title = "Confirmation";
        }

        if (confirmText == null || confirmText.isEmpty()) {
            confirmText = "Yes";
        }

        if (cancelText == null || cancelText.isEmpty()) {
            cancelText = "No";
        }

        DialogButton confirmBtn = new DialogButton(
                confirmText,
                confirmText,
                List.of(
                        new DialogButton.DialogAction("confirm", "")
                )
        );
        this.confirmButtonId = confirmBtn.id();

        DialogButton cancelBtn = new DialogButton(
                cancelText,
                cancelText,
                List.of(
                        new DialogButton.DialogAction("cancel", "")
                )
        );
        this.cancelButtonId = cancelBtn.id();

        DialogData dialogData = new DialogData(
                "confirmation_dialog_" + UUID.randomUUID(),
                title,
                false,
                List.of(new DialogBodyData(question)),
                List.of(confirmBtn, cancelBtn)
        );

        this.dialog = FancyDialogs.get().createDialog(dialogData);
    }

    public String getConfirmButtonId() {
        return confirmButtonId;
    }

    public String getCancelButtonId() {
        return cancelButtonId;
    }

    public Runnable getOnConfirm() {
        return onConfirm;
    }

    public Runnable getOnCancel() {
        return onCancel;
    }
}
