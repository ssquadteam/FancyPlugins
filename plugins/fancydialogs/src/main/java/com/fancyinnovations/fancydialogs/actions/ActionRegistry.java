package com.fancyinnovations.fancydialogs.actions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionRegistry {

    private final Map<String, DialogAction> actions;

    public ActionRegistry() {
        this.actions = new ConcurrentHashMap<>();

        registerDefaultActions();
    }

    private void registerDefaultActions() {

    }

    public void registerAction(String actionId, DialogAction action) {
        if (actions.containsKey(actionId)) {
            throw new IllegalArgumentException("Action with ID " + actionId + " is already registered.");
        }

        actions.put(actionId, action);
    }

    public DialogAction getAction(String actionId) {
        return actions.get(actionId);
    }
}
