package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.spiffyhud.util.rendering.ExtendedGuiGraphics;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaStack;
import de.keksuccino.spiffyhud.util.rendering.exclusion.IGuiGraphicsExclusionArea;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics implements IGuiGraphicsExclusionArea, ExtendedGuiGraphics {
    
    @Unique
    private final ExclusionAreaStack spiffyHud$exclusionAreaStack = new ExclusionAreaStack();

    @Shadow
    protected abstract void innerBlit(RenderPipeline renderType, ResourceLocation resourceLocation, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color);

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

    @Unique
    @Override
    public void spiffyHud$pushExclusionArea(int x1, int y1, int x2, int y2) {
        spiffyHud$exclusionAreaStack.push(new ScreenRectangle(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.abs(x2 - x1),
                Math.abs(y2 - y1)
        ));
    }

    @Unique
    @Override
    public void spiffyHud$popExclusionArea() {
        spiffyHud$exclusionAreaStack.pop();
    }

    @Unique
    @Override
    public void spiffyHud$clearExclusionAreas() {
        spiffyHud$exclusionAreaStack.clear();
    }

    @Unique
    @Override
    public boolean spiffyHud$hasExclusionAreas() {
        return !spiffyHud$exclusionAreaStack.isEmpty();
    }

    @Unique
    @Override
    public int spiffyHud$popAllExclusionAreas() {
        int count = spiffyHud$exclusionAreaStack.size();
        spiffyHud$exclusionAreaStack.clear();
        return count;
    }
    
    // ===== FILL METHODS =====
    
    @Inject(method = "fill(IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusion(int minX, int minY, int maxX, int maxY, int color, CallbackInfo ci) {
        if (spiffyHud$shouldSkipRect(minX, minY, maxX, maxY)) {
            ci.cancel();
        }
    }

    @Inject(method = "fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusion(RenderPipeline pipeline, int minX, int minY, int maxX, int maxY, int color, CallbackInfo ci) {
        if (spiffyHud$shouldSkipRect(minX, minY, maxX, maxY)) {
            ci.cancel();
        }
    }

    @Inject(method = "fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;IIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusion(RenderPipeline pipeline, TextureSetup textureSetup, int minX, int minY, int maxX, int maxY, CallbackInfo ci) {
        if (spiffyHud$shouldSkipRect(minX, minY, maxX, maxY)) {
            ci.cancel();
        }
    }
    
    // ===== TEXT METHODS =====
    
    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkStringExclusion(Font font, String text, int x, int y, int color, boolean dropShadow, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && text != null) {
            int width = font.width(text);
            int height = 9;
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFormattedStringExclusion(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && text != null) {
            int width = font.width(text);
            int height = 9;
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    // ===== BLIT & SPRITE METHODS =====

    @Inject(method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIFFFFI)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkInnerBlit(RenderPipeline pipeline, ResourceLocation atlasLocation, int x0, int x1, int y0, int y1,
                                          float minU, float maxU, float minV, float maxV, int color, CallbackInfo ci) {
        if (this.spiffyHud$shouldSkipRect(x0, y0, x1, y1)) {
            ci.cancel();
        }
    }
    
    // ===== ITEM RENDERING METHODS =====
    
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItem(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
            ci.cancel();
        }
    }
    
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItemDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
            ci.cancel();
        }
    }
    
    // ===== OTHER RENDERING METHODS =====
    
    @Inject(method = "submitOutline", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkSubmitOutline(int x, int y, int width, int height, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
            ci.cancel();
        }
    }
    
    @Inject(method = "fillGradient(IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillGradient(int x1, int y1, int x2, int y2, int colorFrom, int colorTo, CallbackInfo ci) {
        if (spiffyHud$shouldSkipRect(x1, y1, x2, y2)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean spiffyHud$shouldSkipRect(int rawX1, int rawY1, int rawX2, int rawY2) {
        if (spiffyHud$exclusionAreaStack.isEmpty()) {
            return false;
        }
        int minX = Math.min(rawX1, rawX2);
        int maxX = Math.max(rawX1, rawX2);
        int minY = Math.min(rawY1, rawY2);
        int maxY = Math.max(rawY1, rawY2);
        return spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY);
    }

}
