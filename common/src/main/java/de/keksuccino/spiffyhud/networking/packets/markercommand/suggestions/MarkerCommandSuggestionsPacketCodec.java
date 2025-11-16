package de.keksuccino.spiffyhud.networking.packets.markercommand.suggestions;

import de.keksuccino.fancymenu.events.ticking.ClientTickEvent;
import de.keksuccino.fancymenu.networking.PacketCodec;
import de.keksuccino.fancymenu.networking.PacketHandler;
import de.keksuccino.fancymenu.platform.Services;
import de.keksuccino.fancymenu.util.event.acara.EventHandler;
import de.keksuccino.fancymenu.util.event.acara.EventListener;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MarkerCommandSuggestionsPacketCodec extends PacketCodec<MarkerCommandSuggestionsPacket> {

    private static final Logger LOGGER = LogManager.getLogger();
    private Screen lastScreen;

    public MarkerCommandSuggestionsPacketCodec() {
        super("spiffy_marker_command_suggestions", MarkerCommandSuggestionsPacket.class);
        if (Services.PLATFORM.isOnClient()) {
            EventHandler.INSTANCE.registerListenersOf(this);
        }
    }

    @EventListener
    public void onClientPostTick(ClientTickEvent.Post event) {
        if (!Services.PLATFORM.isOnClient()) {
            return;
        }
        try {
            Screen current = Minecraft.getInstance().screen;
            if (current instanceof ChatScreen && current != lastScreen) {
                MarkerCommandSuggestionsPacket packet = new MarkerCommandSuggestionsPacket();
                packet.groupSuggestions = getMarkerGroups();
                PacketHandler.sendToServer(packet);
            }
            lastScreen = current;
        } catch (Exception ex) {
            LOGGER.error("[SPIFFYHUD] Failed to send marker command suggestions packet.", ex);
        }
    }

    private static List<String> getMarkerGroups() {
        List<String> groups = MarkerStorage.getAllGroupIds();
        if (groups.isEmpty()) {
            groups.add("<no_marker_groups>");
        }
        return groups;
    }
}
