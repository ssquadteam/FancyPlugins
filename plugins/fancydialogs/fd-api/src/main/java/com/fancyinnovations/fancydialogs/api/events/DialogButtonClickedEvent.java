package com.fancyinnovations.fancydialogs.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DialogButtonClickedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final String dialogId;
    private final String buttonId;
    private final Map<String, String> payload;

    public DialogButtonClickedEvent(@NotNull Player player, @NotNull String dialogId, @NotNull String buttonId, @NotNull Map<String, String> payload) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.dialogId = dialogId;
        this.buttonId = buttonId;
        this.payload = payload;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
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

    public Map<String, String> getPayload() {
        return payload;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
