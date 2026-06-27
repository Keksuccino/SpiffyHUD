package de.keksuccino.spiffyhud.mixin.mixins.fabric.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.eraser.EraserElement;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinGui;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class MixinGui {

    @Unique
    private static final Logger LOGGER_SPIFFY = LogManager.getLogger();
    @Unique
    private SpiffyGui spiffyGui = null;
    @Unique
    private int aggressionLevelNormalCount_Spiffy = 0;
    @Unique
    private int aggressionLevelAggressiveCount_Spiffy = 0;

    @Shadow
    private Component title;
    @Shadow private Component subtitle;
    @Shadow @Final private static Identifier POWDER_SNOW_OUTLINE_LOCATION;


    /**
     * @reason Apply eraser exclusion areas before Vanilla rendering begins.
     */
    @Inject(method = "extractRenderState", at = @At("HEAD"), order = -1000)
    private void before_render_Spiffy(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        this.aggressionLevelNormalCount_Spiffy = 0;
        this.aggressionLevelAggressiveCount_Spiffy = 0;

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        Minecraft minecraft = Minecraft.getInstance();
        Screen previousScreen = minecraft.gui.screen();
        SpiffyOverlayScreen overlayScreen = null;
        boolean swappedScreen = false;

        try {
            overlayScreen = this.spiffyGui.getOverlayScreen();
            if ((overlayScreen != null) && (previousScreen != overlayScreen)) {
                ((IMixinGui) minecraft.gui).set_screen_Spiffy(overlayScreen);
                swappedScreen = true;
            }
            ScreenCustomizationLayer layer = ScreenCustomizationLayerHandler.getLayerOfScreen((overlayScreen != null) ? overlayScreen : SpiffyUtils.DUMMY_SPIFFY_OVERLAY_SCREEN);
            if (layer != null) {
                for (AbstractElement abstractElement : layer.allElements) {
                    if ((abstractElement instanceof EraserElement eraser) && eraser.shouldRender() && (eraser.aggressionLevel == EraserElement.AggressionLevel.AGGRESSIVE)) {
                        this.aggressionLevelAggressiveCount_Spiffy++;
                        ExclusionAreaUtil.pushExclusionArea(graphics, eraser.getAbsoluteX(), eraser.getAbsoluteY(), eraser.getAbsoluteX() + eraser.getAbsoluteWidth(), eraser.getAbsoluteY() + eraser.getAbsoluteHeight());
                    }
                }
                for (AbstractElement abstractElement : layer.allElements) {
                    if ((abstractElement instanceof EraserElement eraser) && eraser.shouldRender() && (eraser.aggressionLevel == EraserElement.AggressionLevel.NORMAL)) {
                        this.aggressionLevelNormalCount_Spiffy++;
                        ExclusionAreaUtil.pushExclusionArea(graphics, eraser.getAbsoluteX(), eraser.getAbsoluteY(), eraser.getAbsoluteX() + eraser.getAbsoluteWidth(), eraser.getAbsoluteY() + eraser.getAbsoluteHeight());
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER_SPIFFY.error("[SPIFFY HUD] Failed to apply Eraser element areas to HUD!", ex);
        } finally {
            if (swappedScreen) {
                ((IMixinGui) minecraft.gui).set_screen_Spiffy(previousScreen);
            }
        }

    }

    @Inject(method = "extractChat", at = @At("HEAD"))
    private void before_renderChat_Spiffy(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        while (this.aggressionLevelNormalCount_Spiffy > 0) {
            ExclusionAreaUtil.popExclusionArea(graphics);
            this.aggressionLevelNormalCount_Spiffy--;
        }

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        if (!Minecraft.getInstance().gui.hud.isHidden()) {
            graphics.pose().pushMatrix();
            this.spiffyGui.extractRenderState(graphics, -10000000, -10000000, deltaTracker.getGameTimeDeltaTicks());
            graphics.pose().popMatrix();
        }

    }

    @Inject(method = "extractRenderState", at = @At("TAIL"), order = 20000)
    private void after_render_Spiffy(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        while (this.aggressionLevelAggressiveCount_Spiffy > 0) {
            ExclusionAreaUtil.popExclusionArea(graphics);
            this.aggressionLevelAggressiveCount_Spiffy--;
        }

    }

    /**
     * @reason Hide the hotbar when hidden by Spiffy HUD.
     */
    @Inject(method = "extractHotbarAndDecorations", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderHotbarAndDecorations_Spiffy(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER)) info.cancel();
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractItemHotbar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private boolean wrap_renderItemHotbar_in_renderHotbarAndDecorations_Spiffy(Hud instance, GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        return !VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER);
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;extractHotbar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private boolean wrap_SpectatorGui_renderHotbar_in_renderHotbarAndDecorations_Spiffy(SpectatorGui instance, GuiGraphicsExtractor guiGraphics) {
        return !VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER);
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"))
    private boolean wrap_renderExperienceLevel_in_renderHotbarAndDecorations_Spiffy(GuiGraphicsExtractor guiGraphics, Font font, int i) {
        return !VanillaHudElements.isHidden(VanillaHudElements.CONTEXTUAL_BAR_IDENTIFIER);
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private boolean wrap_renderBackground_in_renderHotbarAndDecorations_Spiffy(ContextualBar instance, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        return !VanillaHudElements.isHidden(VanillaHudElements.CONTEXTUAL_BAR_IDENTIFIER);
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private boolean wrap_ContextualBar_render_in_renderHotbarAndDecorations_Spiffy(ContextualBar instance, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        return !VanillaHudElements.isHidden(VanillaHudElements.CONTEXTUAL_BAR_IDENTIFIER);
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractSelectedItemName(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private boolean wrap_renderSelectedItemName_in_renderHotbarAndDecorations_Spiffy(Hud instance, GuiGraphicsExtractor guiGraphics) {
        return !VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER);
    }

    @WrapWithCondition(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;extractAction(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private boolean wrap_renderAction_in_renderHotbarAndDecorations_Spiffy(SpectatorGui instance, GuiGraphicsExtractor guiGraphics) {
        return !VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER);
    }

    /**
     * @reason Hide the scoreboard sidebar when hidden by Spiffy HUD.
     */
    @Inject(method = "displayScoreboardSidebar", at = @At(value = "HEAD"), cancellable = true)
    private void before_displayScoreboardSidebar_Spiffy(GuiGraphicsExtractor guiGraphics, Objective objective, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the crosshair when hidden by Spiffy HUD.
     */
    @Inject(method = "extractCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderCrosshair_Spiffy(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.CROSSHAIR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the mount health bar when hidden by Spiffy HUD.
     */
    @Inject(method = "extractVehicleHealth", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderVehicleHealth_Spiffy(GuiGraphicsExtractor guiGraphics, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the overlay message when hidden by Spiffy HUD.
     */
    @Inject(method = "extractOverlayMessage", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderOverlayMessage_Spiffy(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the title message when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "extractTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V", ordinal = 0))
    private void wrap_drawStringWithBackdrop_title_Spiffy(GuiGraphicsExtractor instance, Font font, Component text, int x, int y, int width, int color, Operation<Void> original) {
        if (this.spiffyHud$shouldRenderTitleComponent(text, this.title, VanillaHudElements.TITLE_IDENTIFIER)) {
            original.call(instance, font, text, x, y, width, color);
        }
    }

    /**
     * @reason Hide the subtitle message when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "extractTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V", ordinal = 1))
    private void wrap_drawStringWithBackdrop_subtitle_Spiffy(GuiGraphicsExtractor instance, Font font, Component text, int x, int y, int width, int color, Operation<Void> original) {
        if (this.spiffyHud$shouldRenderTitleComponent(text, this.subtitle, VanillaHudElements.SUBTITLE_IDENTIFIER)) {
            original.call(instance, font, text, x, y, width, color);
        }
    }

    /**
     * @reason Hide the player armor bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractArmor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private boolean wrap_renderArmor_in_renderPlayerHealth_Spiffy(GuiGraphicsExtractor l, Player k, int j, int p_332897_, int p_332999_, int p_330861_) {
        return !VanillaHudElements.isHidden(VanillaHudElements.ARMOR_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the player food bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractFood(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;II)V"))
    private boolean wrap_renderFood_in_renderPlayerHealth_Spiffy(Hud instance, GuiGraphicsExtractor resourcelocation1, Player resourcelocation2, int k, int resourcelocation) {
        return !VanillaHudElements.isHidden(VanillaHudElements.FOOD_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the player health bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractHearts(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"))
    private boolean wrap_renderHearts_in_renderPlayerHealth_Spiffy(Hud instance, GuiGraphicsExtractor j2, Player flag3, int flag4, int i1, int j1, int k1, float l1, int i2, int flag1, int l, boolean b) {
        return !VanillaHudElements.isHidden(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the player air bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractAirBubbles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;III)V"))
    private boolean wrap_renderAirBubbles_in_renderPlayerHealth_Spiffy(Hud instance, GuiGraphicsExtractor k1, Player j1, int k, int l, int i1) {
        return !VanillaHudElements.isHidden(VanillaHudElements.AIR_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the effects overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "extractEffects", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderEffects_Spiffy(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.EFFECTS_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the hotbar attack indicator when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float wrap_getAttackStrengthScale_in_renderHotbar_Spiffy(LocalPlayer instance, float v, Operation<Float> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.ATTACK_INDICATOR_IDENTIFIER)) return 1.0f; //indicator only gets rendered when attack strength is not at 100%
        return original.call(instance, v);
    }

    @Unique
    private boolean spiffyHud$shouldRenderTitleComponent(Component component, Component expected, String identifier) {
        if ((component != null) && (component == expected) && VanillaHudElements.isHidden(identifier)) {
            return false;
        }
        return true;
    }

    /**
     * @reason Hide the crosshair attack indicator when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float wrap_getAttackStrengthScale_in_renderCrosshair_Spiffy(LocalPlayer instance, float v, Operation<Float> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.ATTACK_INDICATOR_IDENTIFIER)) return 1.0f; //indicator only gets rendered when attack strength is not at 100%
        return original.call(instance, v);
    }

    /**
     * @reason Hide the vignette overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "extractVignette", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderVignette_Spiffy(GuiGraphicsExtractor guiGraphics, Entity entity, CallbackInfo info) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.VIGNETTE)) info.cancel();
    }

    /**
     * @reason Hide the spyglass overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "extractSpyglassOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderSpyglassOverlay_Spiffy(GuiGraphicsExtractor guiGraphics, float scopeScale, CallbackInfo info) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.SPYGLASS)) info.cancel();
    }

    /**
     * @reason Hide the pumpkin overlay when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "extractCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack wrap_getItemBySlot_in_renderCameraOverlays_Spiffy(LocalPlayer instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN)) return ItemStack.EMPTY;
        return original.call(instance, equipmentSlot);
    }

    /**
     * @reason Hide the powder snow overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "extractTextureOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderTextureOverlay_Spiffy(GuiGraphicsExtractor guiGraphics, Identifier location, float alpha, CallbackInfo info) {
        if ((location == POWDER_SNOW_OUTLINE_LOCATION) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.POWDER_SNOW)) info.cancel();
    }

    /**
     * @reason Hide the portal overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "extractPortalOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderPortalOverlay_Spiffy(GuiGraphicsExtractor guiGraphics, float alpha, CallbackInfo info) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PORTAL)) info.cancel();
    }

    // Fabric/Vanilla: Skip contextual bar background when hidden
    @WrapOperation(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"), require = 0)
    private void wrap_renderBackground_Spiffy(ContextualBar instance, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (this.isContextualBarHidden_Spiffy()) return;
        original.call(instance, graphics, deltaTracker);
    }

    // Fabric/Vanilla: Skip contextual bar foreground when hidden
    @WrapOperation(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"), require = 0)
    private void wrap_renderContextualBar_Spiffy(ContextualBar renderer, GuiGraphicsExtractor graphics, DeltaTracker delta, Operation<Void> original) {
        if (this.isContextualBarHidden_Spiffy()) return;
        original.call(renderer, graphics, delta);
    }

    // Fabric/Vanilla: Skip experience level number when hidden
    @WrapOperation(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"), require = 0)
    private void wrap_renderExperienceLevel_Spiffy(GuiGraphicsExtractor graphics, Font font, int level, Operation<Void> original) {
        if (this.isContextualBarHidden_Spiffy()) return;
        original.call(graphics, font, level);
    }

    @Unique
    private boolean isContextualBarHidden_Spiffy() {
        return VanillaHudElements.isHidden(VanillaHudElements.CONTEXTUAL_BAR_IDENTIFIER);
    }

}
