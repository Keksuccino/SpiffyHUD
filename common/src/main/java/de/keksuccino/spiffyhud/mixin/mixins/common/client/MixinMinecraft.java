package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.elements.chatcustomizer.ChatCustomizerHandler;
import de.keksuccino.spiffyhud.util.death.DeathPointStorage;
import de.keksuccino.spiffyhud.util.player.CameraRotationObserver;
import de.keksuccino.spiffyhud.util.player.PlayerPositionObserver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "tick", at = @At("HEAD"))
    private void head_tick_Spiffy(CallbackInfo info) {
        if ((Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {
            CameraRotationObserver.tick();
            PlayerPositionObserver.tick();
            ChatCustomizerHandler.tick();
            DeathPointStorage.tick(Minecraft.getInstance().player);
            SpiffyGui.INSTANCE.tick();
        }
    }

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void after_resizeDisplay_Spiffy(CallbackInfo info) {
        if ((Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {
            SpiffyGui.INSTANCE.onResize();
        }
    }

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void after_setScreen_Spiffy(Screen s, CallbackInfo info) {
        if ((Minecraft.getInstance().screen == null) && (Minecraft.getInstance().level != null)) {
            SpiffyGui.INSTANCE.onResize();
        }
    }

}
