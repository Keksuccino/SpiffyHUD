package de.keksuccino.spiffyhud.mixin.mixins.forge.client;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    /**
     * @reason Prevent blend state changes while Spiffy's blend lock is active.
     */
    @Inject(method = "enableBlend", at = @At("HEAD"), cancellable = true)
    private static void cancel_enableBlend_Spiffy(CallbackInfo info) {
        if (SpiffyRenderUtils.isBlendLocked()) info.cancel();
    }

    /**
     * @reason Prevent blend state changes while Spiffy's blend lock is active.
     */
    @Inject(method = "disableBlend", at = @At("HEAD"), cancellable = true)
    private static void cancel_disableBlend_Spiffy(CallbackInfo info) {
        if (SpiffyRenderUtils.isBlendLocked()) info.cancel();
    }

}
