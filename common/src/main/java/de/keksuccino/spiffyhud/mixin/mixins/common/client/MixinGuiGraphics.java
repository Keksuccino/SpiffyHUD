package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaStack;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaVertexConsumer;
import de.keksuccino.spiffyhud.util.rendering.exclusion.IGuiGraphicsExclusionArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    /**
     * Redirect all calls to bufferSource.getBuffer() to apply exclusion areas when active.
     * This catches all the direct field accesses within GuiGraphics methods.
     * <p>
     * Note: If you encounter rendering methods not being affected by exclusion areas,
     * check if they call bufferSource.getBuffer() and add them to this list.
     */
    @Redirect(
            method = {
                    "*",
                    "blit*",
                    "blitSprite*",
                    "blitNineSlicedSprite",
                    "blitTiledSprite",
                    "fill*",
                    "fillGradient*",
                    "fillRenderType",
                    "innerBlit",
                    "drawString*",
                    "renderTooltipInternal",
                    "renderItemBar",
                    "renderItemCount",
                    "renderItemCooldown",
                    "renderFakeItem*"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer spiffyHud$redirectGetBuffer(MultiBufferSource.BufferSource instance, RenderType renderType) {
        VertexConsumer original = instance.getBuffer(renderType);

        if (!spiffyHud$exclusionAreaStack.isEmpty()) {
            return new ExclusionAreaVertexConsumer(original, spiffyHud$exclusionAreaStack, renderType.mode());
        }

        return original;
    }

}
