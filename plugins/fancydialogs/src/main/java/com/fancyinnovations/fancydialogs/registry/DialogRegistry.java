package com.fancyinnovations.fancydialogs.registry;

import com.fancyinnovations.fancydialogs.api.Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DialogRegistry implements com.fancyinnovations.fancydialogs.api.DialogRegistry {

    private final Map<String, Dialog> dialogs;

    public DialogRegistry() {
        this.dialogs = new ConcurrentHashMap<>();
    }

    @Override
    public void register(@NotNull Dialog dialog) {
        dialogs.put(dialog.getId(), dialog);
    }

    @Override
    public void unregister(@NotNull String id) {
        dialogs.remove(id);
    }

    @Override
    public Dialog get(@NotNull String id) {
        return dialogs.get(id);
    }

    @Override
    public Collection<Dialog> getAll() {
        return dialogs.values();
    }

    @Override
    public void clear() {
        dialogs.clear();
    }
}
