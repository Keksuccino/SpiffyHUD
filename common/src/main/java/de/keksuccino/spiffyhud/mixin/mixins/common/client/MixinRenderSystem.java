package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = "enableBlend", at = @At("HEAD"), cancellable = true, remap = false)
    private static void _head_enableBlend_Spiffy(CallbackInfo info) {
        if (SpiffyRenderUtils.isBlendLocked()) info.cancel();
    }

    @Inject(method = "disableBlend", at = @At("HEAD"), cancellable = true, remap = false)
    private static void _head_disableBlend_Spiffy(CallbackInfo info) {
        if (SpiffyRenderUtils.isBlendLocked()) info.cancel();
    }

}
