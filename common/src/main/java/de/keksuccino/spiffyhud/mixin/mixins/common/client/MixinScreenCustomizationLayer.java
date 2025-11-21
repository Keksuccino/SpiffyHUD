package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.events.screen.RenderScreenEvent;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.elements.eraser.EraserElement;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ScreenCustomizationLayer.class, remap = false)
public abstract class MixinScreenCustomizationLayer {

    @Shadow @NotNull public List<AbstractElement> allElements;

    @Shadow public abstract @NotNull String getScreenIdentifier();

    @Unique
    private int spiffyHud$aggressiveEraserDepth = 0;

    @Inject(method = "onRenderPre", at = @At("HEAD"))
    private void applyAggressiveErasers_Spiffy(RenderScreenEvent.Pre event, CallbackInfo info) {
        if (!this.shouldManageAggressive_Spiffy(event.getScreen())) {
            this.spiffyHud$aggressiveEraserDepth = 0;
            return;
        }

        GuiGraphics graphics = event.getGraphics();
        this.spiffyHud$aggressiveEraserDepth = 0;

        for (AbstractElement abstractElement : this.allElements) {
            if ((abstractElement instanceof EraserElement eraser) && eraser.shouldRender() && (eraser.aggressionLevel == EraserElement.AggressionLevel.AGGRESSIVE)) {
                this.spiffyHud$aggressiveEraserDepth++;
                ExclusionAreaUtil.pushExclusionArea(graphics, eraser.getAbsoluteX(), eraser.getAbsoluteY(), eraser.getAbsoluteX() + eraser.getAbsoluteWidth(), eraser.getAbsoluteY() + eraser.getAbsoluteHeight());
            }
        }
    }

    @Inject(method = "onRenderPost", at = @At("RETURN"))
    private void clearAggressiveErasers_Spiffy(RenderScreenEvent.Post event, CallbackInfo info) {
        if ((this.spiffyHud$aggressiveEraserDepth <= 0) || !this.shouldManageAggressive_Spiffy(event.getScreen())) {
            this.spiffyHud$aggressiveEraserDepth = 0;
            return;
        }

        GuiGraphics graphics = event.getGraphics();
        while (this.spiffyHud$aggressiveEraserDepth-- > 0) {
            ExclusionAreaUtil.popExclusionArea(graphics);
        }
    }

    @Unique
    private boolean shouldManageAggressive_Spiffy(Screen screen) {
        if (!(screen instanceof SpiffyOverlayScreen)) {
            return false;
        }
        if (!SpiffyUtils.isSpiffyIdentifier(this.getScreenIdentifier())) {
            return false;
        }
        if (SpiffyGui.INSTANCE.isRenderingHudContext()) {
            return false;
        }
        return true;
    }

}
