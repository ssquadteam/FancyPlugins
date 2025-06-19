package com.fancyinnovations.fancydialogs.api;

public interface DialogActionRegistry {

    /**
     * Registers a dialog action with the given ID.
     *
     * @param actionId The unique identifier for the action.
     * @param action   The dialog action to register.
     * @throws IllegalArgumentException if an action with the same ID is already registered.
     */
    void registerAction(String actionId, DialogAction action);

    /**
     * Retrieves a dialog action by its ID.
     *
     * @param actionId The unique identifier for the action.
     * @return The dialog action associated with the given ID, or null if not found.
     */
    DialogAction getAction(String actionId);

}
