package de.keksuccino.spiffyhud;

import de.keksuccino.spiffyhud.commands.SpiffyCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class SpiffyHudNeoForgeServerEvents {

    public static void registerAll() {

        NeoForge.EVENT_BUS.register(new SpiffyHudNeoForgeServerEvents());

    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SpiffyCommands.registerAll(event.getDispatcher());
    }

}
