package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class SendToServerDialogAction implements DialogAction {

    public static final SendToServerDialogAction INSTANCE = new SendToServerDialogAction();

    private SendToServerDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(data);
        player.sendPluginMessage(FancyDialogsPlugin.get(), "BungeeCord", out.toByteArray());
    }
}
