package de.keksuccino.spiffyhud;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpiffyHudFabric implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger();
    
    @Override
    public void onInitialize() {

        // SpiffyHud.init(); moved to MixinScreenCustomization in common

        SpiffyHudFabricServerEvents.registerAll();

    }

}
