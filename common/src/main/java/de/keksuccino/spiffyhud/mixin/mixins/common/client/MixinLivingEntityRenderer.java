package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.spiffyhud.util.rendering.EntityRenderingUtils;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @Unique private LivingEntityRenderState cachedRenderState_Spiffy = null;

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"))
    private void before_submit_Spiffy(LivingEntityRenderState livingEntityRenderState, PoseStack pose, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo info) {
        this.cachedRenderState_Spiffy = livingEntityRenderState;
    }

    @WrapOperation(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;multiply(II)I"))
    private int wrap_multiply_in_submit_Spiffy(int i1, int i2, Operation<Integer> original) {
        if ((this.cachedRenderState_Spiffy != null) && EntityRenderingUtils.getLivingEntityOpacity(this.cachedRenderState_Spiffy) < 1.0f) {
            return SpiffyRenderUtils.colorWithAlpha(original.call(i1, i2), EntityRenderingUtils.getLivingEntityOpacity(this.cachedRenderState_Spiffy));
        }
        return original.call(i1, i2);
    }

}
