package com.fancyinnovations.fancydialogs.api.data;

import java.util.List;
import java.util.UUID;

public class DialogButton {

    private final String label;
    private final String tooltip;
    private final List<DialogAction> actions;
    private transient String id;

    public DialogButton(String label, String tooltip, List<DialogAction> actions) {
        this.id = UUID.randomUUID().toString();
        this.label = label;
        this.tooltip = tooltip;
        this.actions = actions;
    }

    public String id() {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public String label() {
        return label;
    }

    public String tooltip() {
        return tooltip;
    }

    public List<DialogAction> actions() {
        return actions;
    }

    public record DialogAction(
            String name,
            String data
    ) {

    }

}
