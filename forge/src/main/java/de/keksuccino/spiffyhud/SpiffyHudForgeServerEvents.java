package de.keksuccino.spiffyhud;

import de.keksuccino.spiffyhud.commands.SpiffyCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpiffyHudForgeServerEvents {

    public static void registerAll() {

        MinecraftForge.EVENT_BUS.register(new SpiffyHudForgeServerEvents());

    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SpiffyCommands.registerAll(event.getDispatcher());
    }

}
