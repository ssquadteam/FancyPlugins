package com.fancyinnovations.fancydialogs.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DialogButtonClickedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final String dialogId;
    private final String buttonId;

    public DialogButtonClickedEvent(@NotNull Player player, @NotNull String dialogId, @NotNull String buttonId) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.dialogId = dialogId;
        this.buttonId = buttonId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getDialogId() {
        return dialogId;
    }

    public String getButtonId() {
        return buttonId;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
