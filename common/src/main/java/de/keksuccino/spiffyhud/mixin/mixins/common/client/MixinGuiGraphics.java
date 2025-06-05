package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaStack;
import de.keksuccino.spiffyhud.util.rendering.exclusion.IGuiGraphicsExclusionArea;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FormattedCharSequence;
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
    
    // Intercept fill methods directly
    @Inject(method = "fill(IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusion(int minX, int minY, int maxX, int maxY, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            // Swap if needed
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
            
            // Check if fully excluded
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fill(IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusionWithZ(int minX, int minY, int maxX, int maxY, int z, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            // Swap if needed
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
            
            // Check if fully excluded
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFillExclusionRenderType(RenderType renderType, int minX, int minY, int maxX, int maxY, int color, CallbackInfo ci) {
        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            // Swap if needed
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
            
            // Check if fully excluded
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(minX, minY, maxX, maxY)) {
                ci.cancel();
            }
        }
    }
    
    // Intercept text rendering
    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkStringExclusion(Font font, String text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && text != null) {
            int width = font.width(text);
            int height = 9; // Font height
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                cir.setReturnValue(x + width);
            }
        }
    }
    
    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void spiffyHud$checkFormattedStringExclusion(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (!spiffyHud$exclusionAreaStack.isEmpty() && text != null) {
            int width = font.width(text);
            int height = 9; // Font height
            
            if (spiffyHud$exclusionAreaStack.isRectangleFullyExcluded(x, y, x + width, y + height)) {
                cir.setReturnValue(x + width);
            }
        }
    }

    

}
