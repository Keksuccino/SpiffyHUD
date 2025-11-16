package de.keksuccino.spiffyhud.networking.packets.markercommand.command;

import de.keksuccino.fancymenu.networking.Packet;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerActionConfig;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerRemovalConfig;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandOperation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class MarkerCommandPacket extends Packet {

    public MarkerCommandOperation operation;
    public MarkerActionConfig actionConfig;
    public MarkerRemovalConfig removalConfig;

    @Override
    public boolean processPacket(@Nullable ServerPlayer sender) {
        if (sender != null) {
            return false;
        }
        return MarkerCommandPacketLogic.handle(this);
    }
}
