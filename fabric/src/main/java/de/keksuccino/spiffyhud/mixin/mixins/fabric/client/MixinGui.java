package de.keksuccino.spiffyhud.mixin.mixins.fabric.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.eraser.EraserElement;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PlayerRideableJumping;
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

@Mixin(Gui.class)
public class MixinGui {

    @Shadow private Component title;
    @Shadow private Component subtitle;
    @Shadow @Final private static ResourceLocation POWDER_SNOW_OUTLINE_LOCATION;

    @Unique private static final Logger LOGGER_SPIFFY = LogManager.getLogger();
    @Unique private SpiffyGui spiffyGui = null;
    @Unique private int aggressionLevelNormalCount_Spiffy = 0;
    @Unique private int aggressionLevelAggressiveCount_Spiffy = 0;

    @Inject(method = "render", at = @At("HEAD"), order = -1000)
    private void before_render_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        this.aggressionLevelNormalCount_Spiffy = 0;
        this.aggressionLevelAggressiveCount_Spiffy = 0;

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        // Applies all Eraser element areas to the GUI before rendering of Vanilla elements starts
        try {
            ScreenCustomizationLayer layer = this.spiffyGui.getLayer();
            if (layer == null) layer = ScreenCustomizationLayerHandler.getLayerOfScreen(SpiffyOverlayScreen.DUMMY_INSTANCE);
            if (layer != null) {
                ScreenCustomizationLayer finalLayer = layer;
                Runnable task = () -> {
                    try {
                        finalLayer.allElements.forEach(abstractElement -> {
                            if ((abstractElement instanceof EraserElement eraser) && eraser.shouldRender() && (eraser.aggressionLevel == EraserElement.AggressionLevel.AGGRESSIVE)) {
                                this.aggressionLevelAggressiveCount_Spiffy++;
                                ExclusionAreaUtil.pushExclusionArea(graphics, eraser.getAbsoluteX(), eraser.getAbsoluteY(), eraser.getAbsoluteX() + eraser.getAbsoluteWidth(), eraser.getAbsoluteY() + eraser.getAbsoluteHeight());
                            }
                        });
                        finalLayer.allElements.forEach(abstractElement -> {
                            if ((abstractElement instanceof EraserElement eraser) && eraser.shouldRender() && (eraser.aggressionLevel == EraserElement.AggressionLevel.NORMAL)) {
                                this.aggressionLevelNormalCount_Spiffy++;
                                ExclusionAreaUtil.pushExclusionArea(graphics, eraser.getAbsoluteX(), eraser.getAbsoluteY(), eraser.getAbsoluteX() + eraser.getAbsoluteWidth(), eraser.getAbsoluteY() + eraser.getAbsoluteHeight());
                            }
                        });
                    } catch(Exception ex) {
                        LOGGER_SPIFFY.error("[SPIFFY HUD] Failed to apply Eraser element areas to Gui! (IN TASK RUNNABLE)", ex);
                    }
                };
                if (Minecraft.getInstance().screen instanceof SpiffyOverlayScreen) {
                    task.run();
                } else {
                    this.spiffyGui.runLayerTask(task);
                }
            }
        } catch (Exception ex) {
            LOGGER_SPIFFY.error("[SPIFFY HUD] Failed to apply Eraser element areas to Gui! (OUTSIDE TASK RUNNABLE)", ex);
        }

    }

    @Inject(method = "renderChat", at = @At("HEAD"))
    private void before_renderChat_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        while (this.aggressionLevelNormalCount_Spiffy > 0) {
            ExclusionAreaUtil.popExclusionArea(graphics);
            this.aggressionLevelNormalCount_Spiffy--;
        }

        // This will render Spiffy's overlay AFTER all Vanilla elements
        if (!Minecraft.getInstance().options.hideGui) {
            spiffyGui.render(graphics, -10000000, -10000000, deltaTracker.getGameTimeDeltaTicks());
        }

    }

    @Inject(method = "render", at = @At("TAIL"), order = 20000)
    private void after_render_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        while (this.aggressionLevelAggressiveCount_Spiffy > 0) {
            ExclusionAreaUtil.popExclusionArea(graphics);
            this.aggressionLevelAggressiveCount_Spiffy--;
        }

    }

    /**
     * @reason Hide the hotbar when hidden by Spiffy HUD.
     */
    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderHotbarAndDecorations_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the jump meter when hidden by Spiffy HUD.
     */
    @Inject(method = "renderJumpMeter", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderJumpMeter_Spiffy(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.JUMP_METER_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the EXP bar when hidden by Spiffy HUD.
     */
    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderExperienceBar_Spiffy(GuiGraphics guiGraphics, int x, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the EXP bar when hidden by Spiffy HUD.
     */
    @Inject(method = "renderExperienceLevel", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderExperienceLevel_Spiffy(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the selected item name when hidden by Spiffy HUD.
     */
    @Inject(method = "renderSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderSelectedItemName_Spiffy(GuiGraphics guiGraphics, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the scoreboard sidebar when hidden by Spiffy HUD.
     */
    @Inject(method = "displayScoreboardSidebar", at = @At(value = "HEAD"), cancellable = true)
    private void before_displayScoreboardSidebar_Spiffy(GuiGraphics guiGraphics, Objective objective, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the crosshair when hidden by Spiffy HUD.
     */
    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderCrosshair_Spiffy(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.CROSSHAIR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the mount health bar when hidden by Spiffy HUD.
     */
    @Inject(method = "renderVehicleHealth", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderVehicleHealth_Spiffy(GuiGraphics guiGraphics, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the overlay message when hidden by Spiffy HUD.
     */
    @Inject(method = "renderOverlayMessage", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderOverlayMessage_Spiffy(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the title and subtitle messages when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I"))
    private boolean wrap_drawStringWithBackdrop_in_renderTitle_Spiffy(GuiGraphics instance, Font font, Component component, int i1, int i2, int i3, int i4) {
        if (component != null) {
            if ((component == this.title) && VanillaHudElements.isHidden(VanillaHudElements.TITLE_IDENTIFIER)) return false;
            if ((component == this.subtitle) && VanillaHudElements.isHidden(VanillaHudElements.SUBTITLE_IDENTIFIER)) return false;
        }
        return true;
    }

    /**
     * @reason Hide the player armor bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderArmor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private boolean wrap_renderArmor_in_renderPlayerHealth_Spiffy(GuiGraphics l, Player k, int j, int p_332897_, int p_332999_, int p_330861_) {
        return !VanillaHudElements.isHidden(VanillaHudElements.ARMOR_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the player food bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V"))
    private boolean wrap_renderFood_in_renderPlayerHealth_Spiffy(Gui instance, GuiGraphics resourcelocation1, Player resourcelocation2, int k, int resourcelocation) {
        return !VanillaHudElements.isHidden(VanillaHudElements.FOOD_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the player health bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"))
    private boolean wrap_renderHearts_in_renderPlayerHealth_Spiffy(Gui instance, GuiGraphics j2, Player flag3, int flag4, int i1, int j1, int k1, float l1, int i2, int flag1, int l, boolean b) {
        return !VanillaHudElements.isHidden(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the player air bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderAirBubbles(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;III)V"))
    private boolean wrap_renderAirBubbles_in_renderPlayerHealth_Spiffy(Gui instance, GuiGraphics k1, Player j1, int k, int l, int i1) {
        return !VanillaHudElements.isHidden(VanillaHudElements.AIR_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the effects overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "renderEffects", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderEffects_Spiffy(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.EFFECTS_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the hotbar attack indicator when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float wrap_getAttackStrengthScale_in_renderHotbar_Spiffy(LocalPlayer instance, float v, Operation<Float> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.ATTACK_INDICATOR_IDENTIFIER)) return 1.0f; //indicator only gets rendered when attack strength is not at 100%
        return original.call(instance, v);
    }

    /**
     * @reason Hide the crosshair attack indicator when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float wrap_getAttackStrengthScale_in_renderCrosshair_Spiffy(LocalPlayer instance, float v, Operation<Float> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.ATTACK_INDICATOR_IDENTIFIER)) return 1.0f; //indicator only gets rendered when attack strength is not at 100%
        return original.call(instance, v);
    }

    /**
     * @reason Hide the vignette overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "renderVignette", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderVignette_Spiffy(GuiGraphics guiGraphics, Entity entity, CallbackInfo info) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.VIGNETTE)) info.cancel();
    }

    /**
     * @reason Hide the spyglass overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "renderSpyglassOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderSpyglassOverlay_Spiffy(GuiGraphics guiGraphics, float scopeScale, CallbackInfo info) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.SPYGLASS)) info.cancel();
    }

    /**
     * @reason Hide the pumpkin overlay when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack wrap_getItemBySlot_in_renderCameraOverlays_Spiffy(LocalPlayer instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN)) return ItemStack.EMPTY;
        return original.call(instance, equipmentSlot);
    }

    /**
     * @reason Hide the powder snow overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "renderTextureOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderTextureOverlay_Spiffy(GuiGraphics guiGraphics, ResourceLocation location, float alpha, CallbackInfo info) {
        if ((location == POWDER_SNOW_OUTLINE_LOCATION) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.POWDER_SNOW)) info.cancel();
    }

    /**
     * @reason Hide the portal overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "renderPortalOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderPortalOverlay_Spiffy(GuiGraphics guiGraphics, float alpha, CallbackInfo info) {
        if (OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PORTAL)) info.cancel();
    }

    /**
     * @reason Patch out the Z -90 translate() call that seems pretty useless (could be wrong tho) and breaks Spiffy.
     */
    @WrapOperation(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void wrap_translate_in_renderHotbar_Spiffy(PoseStack instance, float $$0, float $$1, float $$2, Operation<Void> original) {
        original.call(instance, 0.0F, 0.0F, 0.0F);
    }

}