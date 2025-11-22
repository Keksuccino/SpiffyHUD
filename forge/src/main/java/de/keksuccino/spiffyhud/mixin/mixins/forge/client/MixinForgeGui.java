package de.keksuccino.spiffyhud.mixin.mixins.forge.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.util.rendering.gui.GuiGraphics;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeGui.class)
public class MixinForgeGui extends Gui {

    //unused dummy constructor
    @SuppressWarnings("all")
    private MixinForgeGui() {
        super(null, null);
    }

    @Unique private static final Logger LOGGER_SPIFFY = LogManager.getLogger();
    @Unique private SpiffyGui spiffyGui = null;
    @Unique private float cachedPartial_Spiffy = 0;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;setSeed(J)V"))
    private void before_setSeed_in_render_Spiffy(PoseStack poseStack, float partial, CallbackInfo ci) {

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;
        this.cachedPartial_Spiffy = partial;

    }

    /**
     * @reason Hides the title and subtitle if they are hidden in Spiffy HUD.
     */
    @WrapOperation(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"))
    private int wrap_drawString_in_renderTitle_Spiffy(Font instance, PoseStack pose, FormattedCharSequence sequence, float p_92747_, float p_92748_, int p_92749_, Operation<Integer> original) {
        if (this.title != null) {
            if ((sequence == this.title.getVisualOrderText()) && VanillaHudElements.isHidden(VanillaHudElements.TITLE_IDENTIFIER)) return 0;
        }
        if (this.subtitle != null) {
            if ((sequence == this.subtitle.getVisualOrderText()) && VanillaHudElements.isHidden(VanillaHudElements.SUBTITLE_IDENTIFIER)) return 0;
        }
        return original.call(instance, pose, sequence, p_92747_, p_92748_, p_92749_);
    }

    /**
     * @reason Returns { true } to cancel rendering of overlay elements if they are hidden in Spiffy HUD.
     */
    @Inject(method = "pre", at = @At("HEAD"), cancellable = true, remap = false) //use HEAD to stop mods from rendering custom stuff to overlay elements if the element is hidden
    private void head_Pre_Spiffy(NamedGuiOverlay overlay, PoseStack poseStack, CallbackInfoReturnable<Boolean> info) {

        // Gets rendered after almost everything else
        if (overlay == VanillaGuiOverlay.CHAT_PANEL.type()) {

            if (!Minecraft.getInstance().options.hideGui) {
                spiffyGui.render(GuiGraphics.currentGraphics(), -10000000, -10000000, this.cachedPartial_Spiffy);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            }

        }

        // -----------------------------------------

        if ((overlay == VanillaGuiOverlay.HOTBAR.type()) && VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.JUMP_BAR.type()) && VanillaHudElements.isHidden(VanillaHudElements.JUMP_METER_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.EXPERIENCE_BAR.type()) && VanillaHudElements.isHidden(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.ITEM_NAME.type()) && VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.SCOREBOARD.type()) && VanillaHudElements.isHidden(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.FOOD_LEVEL.type()) && VanillaHudElements.isHidden(VanillaHudElements.FOOD_BAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.ARMOR_LEVEL.type()) && VanillaHudElements.isHidden(VanillaHudElements.ARMOR_BAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.AIR_LEVEL.type()) && VanillaHudElements.isHidden(VanillaHudElements.AIR_BAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.MOUNT_HEALTH.type()) && VanillaHudElements.isHidden(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.PLAYER_HEALTH.type()) && VanillaHudElements.isHidden(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.RECORD_OVERLAY.type()) && VanillaHudElements.isHidden(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.CROSSHAIR.type()) && VanillaHudElements.isHidden(VanillaHudElements.CROSSHAIR_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.BOSS_EVENT_PROGRESS.type()) && VanillaHudElements.isHidden(VanillaHudElements.BOSS_BARS_IDENTIFIER)) {
            info.setReturnValue(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.POTION_ICONS.type()) && VanillaHudElements.isHidden(VanillaHudElements.EFFECTS_IDENTIFIER)) {
            info.setReturnValue(true);
        }

        // OVERLAYS
        if ((overlay == VanillaGuiOverlay.VIGNETTE.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.VIGNETTE)) {
            info.setReturnValue(true);
        }
        if ((overlay == VanillaGuiOverlay.FROSTBITE.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.POWDER_SNOW)) {
            info.setReturnValue(true);
        }
        if ((overlay == VanillaGuiOverlay.HELMET.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN)) {
            info.setReturnValue(true);
        }
        if ((overlay == VanillaGuiOverlay.SPYGLASS.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.SPYGLASS)) {
            info.setReturnValue(true);
        }
        if ((overlay == VanillaGuiOverlay.PORTAL.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PORTAL)) {
            info.setReturnValue(true);
        }

    }

}
