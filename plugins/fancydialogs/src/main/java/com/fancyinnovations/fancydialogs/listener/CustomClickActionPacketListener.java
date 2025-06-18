package com.fancyinnovations.fancydialogs.listener;

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
        String actionId = packet.getPayload().get("action_id");
        String actionData = packet.getPayload().get("action_data");

        // TODO process the dialog action
    }

    public FS_PacketListener getPacketListener() {
        return packetListener;
    }
}
