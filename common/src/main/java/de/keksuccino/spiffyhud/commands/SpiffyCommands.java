package de.keksuccino.spiffyhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public class SpiffyCommands {

    public static void registerAll(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {

        SpiffyMarkerCommand.register(dispatcher);

    }

}
