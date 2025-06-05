package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaStack;
import de.keksuccino.spiffyhud.util.rendering.exclusion.IGuiGraphicsExclusionArea;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Function;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics implements IGuiGraphicsExclusionArea {
    
    @Unique
    private final ExclusionAreaStack spiffyHud$exclusionAreaStack = new ExclusionAreaStack();

    @Override
    public void spiffyHud$pushExclusionArea(int x1, int y1, int x2, int y2) {
        spiffyHud$exclusionAreaStack.push(new ScreenRectangle(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.abs(x2 - x1),
                Math.abs(y2 - y1)
        ));
    }

    @Override
    public void spiffyHud$popExclusionArea() {
        spiffyHud$exclusionAreaStack.pop();
    }

    @Override
    public void spiffyHud$clearExclusionAreas() {
        spiffyHud$exclusionAreaStack.clear();
    }

    @Override
    public boolean spiffyHud$hasExclusionAreas() {
        return !spiffyHud$exclusionAreaStack.isEmpty();
    }
    
    // ===== FILL METHODS =====
    
    @Inject(method = "fill(IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusion(int minX, int minY, int maxX, int maxY, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (minX > maxX) {
                int temp = minX;
                minX = maxX;
                maxX = temp;
            }
            if (minY > maxY) {
                int temp = minY;
                minY = maxY;
                maxY = temp;
            }
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fill(IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusionWithZ(int minX, int minY, int maxX, int maxY, int z, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (minX > maxX) {
                int temp = minX;
                minX = maxX;
                maxX = temp;
            }
            if (minY > maxY) {
                int temp = minY;
                minY = maxY;
                maxY = temp;
            }
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusionRenderType(RenderType renderType, int minX, int minY, int maxX, int maxY, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (minX > maxX) {
                int temp = minX;
                minX = maxX;
                maxX = temp;
            }
            if (minY > maxY) {
                int temp = minY;
                minY = maxY;
                maxY = temp;
            }
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fill(Lnet/minecraft/client/renderer/RenderType;IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusionRenderTypeZ(RenderType renderType, int minX, int minY, int maxX, int maxY, int z, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (minX > maxX) {
                int temp = minX;
                minX = maxX;
                maxX = temp;
            }
            if (minY > maxY) {
                int temp = minY;
                minY = maxY;
                maxY = temp;
            }
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    // ===== TEXT METHODS =====
    
    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkStringExclusion(Font font, String text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && text != null) {
            int width = font.width(text);
            int height = 9;
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                cir.setReturnValue(x + width);
            }
        }
    }
    
    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFormattedStringExclusion(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && text != null) {
            int width = font.width(text);
            int height = 9;
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                cir.setReturnValue(x + width);
            }
        }
    }
    
    // ===== BLIT SPRITE METHODS =====
    
    @Inject(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitSprite1(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitSprite2(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height, int blitOffset, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitSprite3(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + uWidth, y + vHeight)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitSprite4(Function<ResourceLocation, RenderType> renderTypeGetter, TextureAtlasSprite sprite, int x, int y, int width, int height, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitSprite5(Function<ResourceLocation, RenderType> renderTypeGetter, TextureAtlasSprite sprite, int x, int y, int width, int height, int blitOffset, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    // ===== BLIT METHODS =====
    
    @Inject(method = "blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlit1(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + uWidth, y + vHeight)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlit2(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + uWidth, y + vHeight)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlit3(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int width, int height, int textureWidth, int textureHeight, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + uWidth, y + vHeight)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlit4(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int width, int height, int textureWidth, int textureHeight, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + uWidth, y + vHeight)) {
                ci.cancel();
            }
        }
    }
    
    // ===== SPECIAL BLIT METHODS =====
    
    @Inject(method = "blitTiledSprite", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitTiledSprite(Function<ResourceLocation, RenderType> renderTypeGetter, TextureAtlasSprite sprite, int x, int y, int width, int height, int uPosition, int vPosition, int spriteWidth, int spriteHeight, int nineSliceWidth, int nineSliceHeight, int blitOffset, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "blitNineSlicedSprite", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkBlitNineSlicedSprite(Function<ResourceLocation, RenderType> renderTypeGetter, TextureAtlasSprite sprite, GuiSpriteScaling.NineSlice nineSlice, int x, int y, int blitOffset, int width, int height, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    // ===== ITEM RENDERING METHODS =====
    
    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItem1(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItem2(ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItem3(ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderFakeItem1(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderFakeItem(Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderFakeItem2(ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItemEntity(LivingEntity entity, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItemEntityLevel(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItemEntityLevelOffset(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItemDecorations1(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderItemDecorations2(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + 16, y + 16)) {
                ci.cancel();
            }
        }
    }
    
    // ===== OTHER RENDERING METHODS =====
    
    @Inject(method = "renderOutline", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkRenderOutline(int x, int y, int width, int height, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fillGradient(IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillGradient1(int x1, int y1, int x2, int y2, int colorFrom, int colorTo, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fillGradient(IIIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillGradient2(int x1, int y1, int x2, int y2, int z, int colorFrom, int colorTo, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "fillGradient(Lnet/minecraft/client/renderer/RenderType;IIIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillGradient3(RenderType renderType, int x1, int y1, int x2, int y2, int colorFrom, int colorTo, int z, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }

}
