package com.fancyinnovations.fancydialogs.api.data.inputs;

public class DialogTextField extends DialogInput {

    private final String placeholder;
    private final int maxLength;
    private final int maxLines;

    public DialogTextField(String key, String label, int order, String placeholder, int maxLength, int maxLines) {
        super(key, label, order);
        this.placeholder = placeholder;
        this.maxLength = maxLength;
        this.maxLines = maxLines;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMaxLines() {
        return maxLines;
    }
}
