package com.fancyinnovations.fancydialogs.listener;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.actions.DialogAction;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.events.DialogButtonClickedEvent;
import de.oliver.fancysitula.api.packets.FS_ServerboundCustomClickActionPacket;
import de.oliver.fancysitula.api.packets.FS_ServerboundPacket;
import de.oliver.fancysitula.api.utils.FS_PacketListener;
import de.oliver.fancysitula.factories.FancySitula;

public class CustomClickActionPacketListener {

    private static CustomClickActionPacketListener INSTANCE;

    private final FS_PacketListener packetListener;

    public CustomClickActionPacketListener() {
        packetListener = FancySitula.PACKET_LISTENER_FACTORY.createPacketListener(FS_ServerboundPacket.Type.CUSTOM_CLICK_ACTION);
        packetListener.addListener(this::onPacketReceived);
    }

    public static CustomClickActionPacketListener get() {
        if (INSTANCE == null) {
            INSTANCE = new CustomClickActionPacketListener();
        }
        return INSTANCE;
    }

    private void onPacketReceived(FS_PacketListener.PacketReceivedEvent event) {
        if (!(event.packet() instanceof FS_ServerboundCustomClickActionPacket packet)) {
            return; // Ignore if the packet is not of the expected type
        }

        if (!packet.getId().namespace().equals("fancysitula") && !packet.getId().namespace().equals("fancydialogs_dialog_action")) {
            return; // Ignore packets not related to FancyDialogs
        }

        String dialogId = packet.getPayload().get("dialog_id");
        String buttonId = packet.getPayload().get("button_id");

        new DialogButtonClickedEvent(event.player(), dialogId, buttonId).callEvent();

        Dialog dialog = FancyDialogsPlugin.get().getDialogRegistry().get(dialogId);
        if (dialog == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Received action for unknown dialog: " + dialogId);
            return;
        }

        DialogButton btn = dialog.getData().getButtonById(buttonId);
        if (btn == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Received action for unknown button: " + buttonId + " in dialog: " + dialogId);
            return;
        }

        for (DialogButton.DialogAction btnAction : btn.actions()) {
            DialogAction action = FancyDialogsPlugin.get().getActionRegistry().getAction(btnAction.name());
            if (action == null) {
                FancyDialogsPlugin.get().getFancyLogger().warn("Received action for unknown action: " + btnAction.name() + " in button: " + buttonId);
                continue;
            }

            action.execute(event.player(), dialog, btnAction.data());
        }
    }

    public FS_PacketListener getPacketListener() {
        return packetListener;
    }
}
