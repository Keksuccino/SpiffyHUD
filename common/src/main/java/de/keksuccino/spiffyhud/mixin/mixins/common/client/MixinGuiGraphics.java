package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.spiffyhud.util.rendering.ExtendedGuiGraphics;
import de.keksuccino.spiffyhud.util.rendering.ItemRenderingUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics implements ExtendedGuiGraphics {

    @Shadow
    protected abstract void innerBlit(RenderPipeline renderType, ResourceLocation resourceLocation, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color);

    @WrapOperation(method = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForTopItem(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V"))
    private void wrap_updateForTopItem_in_renderItem_Spiffy(ItemModelResolver instance, ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level level, LivingEntity entity, int seed, Operation<Void> original) {
        if (ItemRenderingUtils.getItemOpacity() < 1.0F) {
            ItemRenderingUtils.submitCurrentItemOpacityFor(renderState);
        }
    }

    @Override
    @Unique
    public void blitMirrored_Spiffy(RenderPipeline renderType, ResourceLocation texture, int x, int y, int width, int height, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, int color) {

        RenderSystem.assertOnRenderThread();

        // Calculate UV coordinates (swapped for mirroring)
        float u1 = (u + uWidth) / textureWidth;  // Right edge becomes left
        float u2 = u / textureWidth;              // Left edge becomes right
        float v1 = v / textureHeight;
        float v2 = (v + vHeight) / textureHeight;

        // Call the inner blit method with swapped U coordinates
        this.innerBlit(renderType, texture, x, x + width, y, y + height, u1, u2, v1, v2, color);

    }

    @Override
    @Unique
    public void blitMirroredMatrix_Spiffy(RenderPipeline renderType, ResourceLocation texture, int x, int y, int width, int height, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, int color) {

        GuiGraphics self = (GuiGraphics)(Object)this;

        // Save current matrix state
        self.pose().pushMatrix();

        // Translate to the sprite position, scale X by -1, then translate back
        self.pose().translate(x + width / 2.0f, y + height / 2.0f);
        self.pose().scale(-1.0f, 1.0f);
        self.pose().translate(-width / 2.0f, -height / 2.0f);

        // Draw normally (the matrix transformation will handle the mirroring)
        self.blit(renderType, texture, 0, 0, u, v, width, height, textureWidth, textureHeight, color);

        // Restore matrix state
        self.pose().popMatrix();

    }

}
