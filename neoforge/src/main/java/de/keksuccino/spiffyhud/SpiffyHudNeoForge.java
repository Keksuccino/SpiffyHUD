package de.keksuccino.spiffyhud;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(SpiffyHud.MOD_ID)
public class SpiffyHudNeoForge {
    
    public SpiffyHudNeoForge(@NotNull IEventBus eventBus) {

        SpiffyHud.init();

        SpiffyHudNeoForgeServerEvents.registerAll();
        
    }

}
