package com.fancyinnovations.fancydialogs.api.data;

import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DialogData(
        @NotNull String id,
        @NotNull String title,
        boolean canCloseWithEscape,
        @NotNull List<DialogBodyData> body,
        @NotNull DialogInputs inputs,
        @NotNull List<DialogButton> buttons
) {

    public DialogButton getButtonById(@NotNull String buttonId) {
        for (DialogButton button : buttons) {
            if (button.id().equals(buttonId)) {
                return button;
            }
        }

        return null;
    }

}
