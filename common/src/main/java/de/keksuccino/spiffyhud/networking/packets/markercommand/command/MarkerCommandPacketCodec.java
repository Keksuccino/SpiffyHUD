package de.keksuccino.spiffyhud.networking.packets.markercommand.command;

import de.keksuccino.fancymenu.networking.PacketCodec;

public class MarkerCommandPacketCodec extends PacketCodec<MarkerCommandPacket> {

    public MarkerCommandPacketCodec() {
        super("spiffy_marker_command", MarkerCommandPacket.class);
    }
}
