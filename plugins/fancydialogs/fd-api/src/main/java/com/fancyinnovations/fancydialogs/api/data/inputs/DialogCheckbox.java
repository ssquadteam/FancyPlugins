package com.fancyinnovations.fancydialogs.api.data.inputs;

public class DialogCheckbox extends DialogInput {

    private final boolean initial;

    public DialogCheckbox(String key, String label, int order, boolean initial) {
        super(key, label, order);
        this.initial = initial;
    }

    public boolean isInitial() {
        return initial;
    }
}
