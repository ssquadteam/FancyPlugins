package com.fancyinnovations.fancydialogs.api.data.inputs;

public abstract class DialogInput {

    protected final String key;
    protected final String label;
    protected final int order;

    public DialogInput(String key, String label, int order) {
        this.key = key;
        this.label = label;
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public int getOrder() {
        return order;
    }
}
