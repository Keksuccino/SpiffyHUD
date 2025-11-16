package de.keksuccino.spiffyhud.networking.packets.markercommand.command;

import de.keksuccino.fancymenu.networking.Packet;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerActionConfig;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerRemovalConfig;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandEditField;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandOperation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

public class MarkerCommandPacket extends Packet {

    public MarkerCommandOperation operation;
    public MarkerActionConfig actionConfig;
    public MarkerRemovalConfig removalConfig;
    public Set<MarkerCommandEditField> editFields;

    @Override
    public boolean processPacket(@Nullable ServerPlayer sender) {
        if (sender != null) {
            return false;
        }
        return MarkerCommandPacketLogic.handle(this);
    }
}
