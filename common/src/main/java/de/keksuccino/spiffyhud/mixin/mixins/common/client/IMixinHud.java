package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Hud.class)
public interface IMixinHud {

    @Accessor("tickCount") int getTickCount_Spiffy();

    @Accessor("overlayMessageString") Component get_overlayMessageString_Spiffy();

    @Accessor("overlayMessageTime") int get_overlayMessageTime_Spiffy();

    @Accessor("toolHighlightTimer") int get_toolHighlightTimer_Spiffy();

}
