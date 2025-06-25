package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PictureInPictureRenderer.class)
public class MixinPictureInPictureRenderer {

    @Unique private PoseStack cached_pose_Spiffy = null;

    @WrapOperation(method = "prepare", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void wrap_translate_in_prepare_Spiffy(PoseStack instance, float x, float y, float z, Operation<Void> original) {
        this.cached_pose_Spiffy = instance;
        instance.pushPose();
        original.call(instance, x, y, z);
    }

    @Inject(method = "prepare", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/pip/PictureInPictureRenderer;blitTexture(Lnet/minecraft/client/gui/render/state/pip/PictureInPictureRenderState;Lnet/minecraft/client/gui/render/state/GuiRenderState;)V", shift = At.Shift.AFTER))
    private void after_prepare_Spiffy(PictureInPictureRenderState pipRenderState, GuiRenderState guiRenderState, int i, CallbackInfo info) {
        if (this.cached_pose_Spiffy != null) {
            this.cached_pose_Spiffy.scale(1.0F, 1.0F, 1.0F);
            this.cached_pose_Spiffy.popPose();
        }
        this.cached_pose_Spiffy = null;
    }

}
