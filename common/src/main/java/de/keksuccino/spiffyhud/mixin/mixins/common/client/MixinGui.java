package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.customization.SpiffyGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui {

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void after_setScreen_Spiffy(Screen screen, CallbackInfo info) {
        if ((Minecraft.getInstance().gui.screen() == null) && (Minecraft.getInstance().level != null)) {
            SpiffyGui.INSTANCE.onResize();
        }
    }

}
