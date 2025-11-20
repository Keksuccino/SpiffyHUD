package de.keksuccino.spiffyhud;

import de.keksuccino.spiffyhud.commands.SpiffyCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class SpiffyHudFabricServerEvents {

    public static void registerAll() {

        registerCommands();

    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                SpiffyCommands.registerAll(dispatcher));
    }

}
