package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.List;

public class DialogSelect extends DialogInput {

    private final List<Entry> options;

    public DialogSelect(String key, String label, int order, List<Entry> options) {
        super(key, label, order);
        this.options = options;
    }

    public List<Entry> getOptions() {
        return options;
    }

    public record Entry(
            String value,
            String display,
            boolean initial
    ) {

    }

}
