package com.fancyinnovations.fancydialogs.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DialogData(
        @NotNull String id,
        @NotNull String title,
        boolean canCloseWithEscape,
        @NotNull List<DialogBodyData> body,
        @NotNull List<DialogButton> buttons
) {

}
