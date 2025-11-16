package de.keksuccino.spiffyhud;

import de.keksuccino.spiffyhud.commands.SpiffyCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class SpiffyHudFabricServerEvents {

    private SpiffyHudFabricServerEvents() {
    }

    public static void registerAll() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                SpiffyCommands.registerAll(dispatcher));
    }
}
