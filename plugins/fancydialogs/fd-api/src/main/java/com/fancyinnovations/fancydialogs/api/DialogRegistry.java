package com.fancyinnovations.fancydialogs.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface DialogRegistry {

    /**
     * Registers a new dialog in the registry.
     *
     * @param dialog the dialog to register
     */
    void register(@NotNull Dialog dialog);

    /**
     * Unregisters a dialog from the registry.
     *
     * @param id the ID of the dialog to unregister
     */
    void unregister(@NotNull String id);

    /**
     * Retrieves a dialog by its ID.
     *
     * @param id the ID of the dialog to retrieve
     * @return the dialog, or null if not found
     */
    Dialog get(String id);

    /**
     * Retrieves all registered dialogs.
     *
     * @return a collection of all registered dialogs
     */
    Collection<Dialog> getAll();

    /**
     * Clears all registered dialogs.
     */
    void clear();

}
