package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaStack;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaVertexConsumer;
import de.keksuccino.spiffyhud.util.rendering.exclusion.IBufferSourceWithExclusionArea;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiBufferSource.BufferSource.class)
public class MixinBufferSource implements IBufferSourceWithExclusionArea {
    
    @Unique
    private ExclusionAreaStack spiffyHud$exclusionAreaStack;
    
    @Override
    public void spiffyHud$setExclusionAreaStack(ExclusionAreaStack stack) {
        this.spiffyHud$exclusionAreaStack = stack;
    }
    
    @Override
    public ExclusionAreaStack spiffyHud$getExclusionAreaStack() {
        return this.spiffyHud$exclusionAreaStack;
    }
    
    @Inject(method = "getBuffer", at = @At("RETURN"), cancellable = true)
    private void spiffyHud$wrapVertexConsumer(RenderType renderType, CallbackInfoReturnable<VertexConsumer> cir) {
        // TEMPORARILY DISABLED to avoid crashes
        // TODO: Fix the incomplete vertex issue
        /*
        if (spiffyHud$exclusionAreaStack != null && !spiffyHud$exclusionAreaStack.isEmpty()) {
            VertexConsumer original = cir.getReturnValue();
            cir.setReturnValue(new ExclusionAreaVertexConsumer(original, spiffyHud$exclusionAreaStack, renderType.mode()));
        }
        */
    }
}
