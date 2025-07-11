package de.keksuccino.spiffyhud;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(SpiffyHud.MOD_ID)
public class SpiffyHudForge {
    
    public SpiffyHudForge() {

        SpiffyHud.init();

        MinecraftForge.EVENT_BUS.register(new SpiffyHudEventHandler());

    }

}