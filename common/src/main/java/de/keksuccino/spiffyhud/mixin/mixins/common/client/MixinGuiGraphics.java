package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaStack;
import de.keksuccino.spiffyhud.util.rendering.exclusion.IGuiGraphicsExclusionArea;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderType;
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
    
    @Override
    public int spiffyHud$popAllExclusionAreas() {
        int count = spiffyHud$exclusionAreaStack.size();
        spiffyHud$exclusionAreaStack.clear();
        return count;
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
    
    // ===== BLIT & SPRITE METHODS =====

    @Inject(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkInnerBlit(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset,
                                          float minU, float maxU, float minV, float maxV, CallbackInfo ci) {
        if (this.spiffyHud$shouldSkipRect(x1, y1, x2, y2)) {
            ci.cancel();
        }
    }

    @Inject(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkInnerBlitColored(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset,
                                                 float minU, float maxU, float minV, float maxV,
                                                 float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (this.spiffyHud$shouldSkipRect(x1, y1, x2, y2)) {
            ci.cancel();
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
