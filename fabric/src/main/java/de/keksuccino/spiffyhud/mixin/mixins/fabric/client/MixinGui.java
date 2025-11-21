package de.keksuccino.spiffyhud.mixin.mixins.fabric.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.eraser.EraserElement;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
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
public abstract class MixinGui {

    @Shadow private Component overlayMessageString;
    @Shadow private Component title;
    @Shadow private Component subtitle;

    @Unique
    private static final Logger LOGGER_SPIFFY = LogManager.getLogger();
    @Unique
    private SpiffyGui spiffyGui = null;
    @Unique
    private int aggressionLevelNormalCount_Spiffy = 0;
    @Unique
    private int aggressionLevelAggressiveCount_Spiffy = 0;

    @Shadow protected abstract int getVehicleMaxHearts(LivingEntity $$0);

    @Shadow protected abstract LivingEntity getPlayerVehicleWithHealth();

    @Shadow @Final private static ResourceLocation PUMPKIN_BLUR_LOCATION;

    @Shadow @Final private static ResourceLocation POWDER_SNOW_OUTLINE_LOCATION;

    /**
     * @reason Apply eraser exclusion areas before Vanilla rendering begins.
     */
    @Inject(method = "render", at = @At("HEAD"), order = -1000)
    private void before_render_Spiffy(GuiGraphics graphics, float partialTick, CallbackInfo info) {

        this.aggressionLevelNormalCount_Spiffy = 0;
        this.aggressionLevelAggressiveCount_Spiffy = 0;

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        Minecraft minecraft = Minecraft.getInstance();
        Screen previousScreen = minecraft.screen;
        SpiffyOverlayScreen overlayScreen = null;
        boolean swappedScreen = false;

        try {
            overlayScreen = this.spiffyGui.getOverlayScreen();
            if ((overlayScreen != null) && (previousScreen != overlayScreen)) {
                minecraft.screen = overlayScreen;
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
            LOGGER_SPIFFY.error("[SPIFFY HUD] Failed to apply Eraser element areas to Gui!", ex);
        } finally {
            if (swappedScreen) {
                minecraft.screen = previousScreen;
            }
        }

    }

    @Inject(method = "renderChat", at = @At("HEAD"))
    private void before_renderChat_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        while (this.aggressionLevelNormalCount_Spiffy > 0) {
            ExclusionAreaUtil.popExclusionArea(graphics);
            this.aggressionLevelNormalCount_Spiffy--;
        }

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        if (!Minecraft.getInstance().options.hideGui) {
            RenderSystem.enableBlend();
            graphics.pose().pushPose();
            this.spiffyGui.render(graphics, -10000000, -10000000, deltaTracker.getGameTimeDeltaTicks());
            graphics.pose().popPose();
            RenderSystem.disableBlend();
        }

    }

    @Inject(method = "render", at = @At("TAIL"), order = 20000)
    private void after_render_Spiffy(GuiGraphics graphics, float partial, CallbackInfo info) {

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
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderJumpMeter(Lnet/minecraft/world/entity/PlayerRideableJumping;Lnet/minecraft/client/gui/GuiGraphics;I)V"))
    private boolean wrap_renderJumpMeter_in_render_Spiffy(Gui instance, PlayerRideableJumping $$0, GuiGraphics $$1, int $$2) {
        return !VanillaHudElements.isHidden(VanillaHudElements.JUMP_METER_IDENTIFIER);
    }

    /**
     * @reason Hide the EXP bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V"))
    private boolean wrap_renderExperienceBar_in_render_Spiffy(Gui instance, GuiGraphics $$0, int $$1) {
        return !VanillaHudElements.isHidden(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the selected item name when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean wrap_renderSelectedItemName_in_render_Spiffy(Gui instance, GuiGraphics $$0) {
        return !VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER);
    }

    /**
     * @reason Hide the scoreboard sidebar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V"))
    private boolean wrap_displayScoreboardSidebar_in_render_Spiffy(Gui instance, GuiGraphics $$0, Objective $$1) {
        return !VanillaHudElements.isHidden(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER);
    }

    /**
     * @reason Hide the crosshair when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean wrap_renderCrosshair_in_render_Spiffy(Gui instance, GuiGraphics $$0) {
        return !VanillaHudElements.isHidden(VanillaHudElements.CROSSHAIR_IDENTIFIER);
    }

    /**
     * @reason Hide the boss overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean wrap_BossOverlay_render_in_render_Spiffy(BossHealthOverlay instance, GuiGraphics $$0) {
        return !VanillaHudElements.isHidden(VanillaHudElements.BOSS_BARS_IDENTIFIER);
    }

    /**
     * @reason Hide the mount health bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean wrap_renderVehicleHealth_in_render_Spiffy(Gui instance, GuiGraphics $$0) {
        return !VanillaHudElements.isHidden(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the overlay message, title and subtitle when hidden by Spiffy HUD.
     */
    @Inject(method = "renderOverlayMessage", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderOverlayMessage_Spiffy(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (VanillaHudElements.isHidden(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER)) info.cancel();
    }

    /**
     * @reason Hide the title message when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I", ordinal = 0))
    private int wrap_drawStringWithBackdrop_title_Spiffy(GuiGraphics instance, Font font, Component component, int i1, int i2, int i3, int i4, Operation<Integer> original) {
        if (this.spiffyHud$shouldRenderTitleComponent(component, this.title, VanillaHudElements.TITLE_IDENTIFIER)) {
            return original.call(instance, font, component, i1, i2, i3, i4);
        }
        return 0;
    }

    /**
     * @reason Hide the subtitle message when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I", ordinal = 1))
    private int wrap_drawStringWithBackdrop_subtitle_Spiffy(GuiGraphics instance, Font font, Component component, int i1, int i2, int i3, int i4, Operation<Integer> original) {
        if (this.spiffyHud$shouldRenderTitleComponent(component, this.subtitle, VanillaHudElements.SUBTITLE_IDENTIFIER)) {
            return original.call(instance, font, component, i1, i2, i3, i4);
        }
        return 0;
    }

    /**
     * @reason Hide the player armor bar when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
    private int wrap_getArmorValue_in_renderPlayerHealth_Spiffy(Player instance, Operation<Integer> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.ARMOR_BAR_IDENTIFIER)) return 0;
        return original.call(instance);
    }

    /**
     * @reason Hide the player food bar when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int wrap_getVehicleMaxHearts_in_renderPlayerHealth_Spiffy(Gui instance, LivingEntity livingEntity, Operation<Integer> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.FOOD_BAR_IDENTIFIER)) return 1000; //player food does not get rendered when
        return original.call(instance, livingEntity);
    }

    /**
     * @reason Revert patch to getVehicleMaxHearts() from method above.
     */
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVisibleVehicleHeartRows(I)I"))
    private int wrap_getVisibleVehicleHeartRows_in_renderPlayerHealth_Spiffy(Gui instance, int $$0, Operation<Integer> original) {
        return original.call(instance, this.getVehicleMaxHearts(this.getPlayerVehicleWithHealth()));
    }

    /**
     * @reason Hide the player air bar when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAirSupply()I"))
    private int wrap_getAirSupply_in_renderPlayerHealth_Spiffy(Player instance, Operation<Integer> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.AIR_BAR_IDENTIFIER)) return 1000000000; //air bar is invisible when air is >= max air, so just set a very high air here to hide the bar
        return original.call(instance);
    }

    /**
     * @reason Hide the player air bar when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean wrap_isEyeInFluid_in_renderPlayerHealth_Spiffy(Player instance, TagKey<?> tagKey, Operation<Boolean> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.AIR_BAR_IDENTIFIER)) return false;
        return original.call(instance, tagKey);
    }

    /**
     * @reason Hide the player health bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"))
    private boolean wrap_renderHearts_in_renderPlayerHealth_Spiffy(Gui instance, GuiGraphics $$0, Player $$1, int $$2, int $$3, int $$4, int $$5, float $$6, int $$7, int $$8, int $$9, boolean $$10) {
        return !VanillaHudElements.isHidden(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the effects overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean wrap_renderEffects_in_render_Spiffy(Gui instance, GuiGraphics $$0) {
        return !VanillaHudElements.isHidden(VanillaHudElements.EFFECTS_IDENTIFIER);
    }

    /**
     * @reason Hide the hotbar attack indicator when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
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
    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float wrap_getAttackStrengthScale_in_renderCrosshair_Spiffy(LocalPlayer instance, float v, Operation<Float> original) {
        if (VanillaHudElements.isHidden(VanillaHudElements.ATTACK_INDICATOR_IDENTIFIER)) return 1.0f; //indicator only gets rendered when attack strength is not at 100%
        return original.call(instance, v);
    }

    /**
     * @reason Hide the vignette overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"))
    private boolean wrap_renderVignette_in_render_Spiffy(Gui instance, GuiGraphics $$0, Entity $$1) {
        return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.VIGNETTE);
    }

    /**
     * @reason Hide the spyglass overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSpyglassOverlay(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
    private boolean wrap_renderSpyglassOverlay_in_render_Spiffy(Gui instance, GuiGraphics $$0, float $$1) {
        return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.SPYGLASS);
    }

    /**
     * @reason Hide the pumpkin overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderTextureOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;F)V"))
    private boolean wrap_pumpkin_overlay_rendering_in_render_Spiffy(Gui instance, GuiGraphics graphics, ResourceLocation location, float f) {
        if (location == PUMPKIN_BLUR_LOCATION) {
            return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN);
        }
        return true;
    }

    /**
     * @reason Hide the powder snow overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderTextureOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;F)V"))
    private boolean wrap_powder_snow_overlay_rendering_in_render_Spiffy(Gui instance, GuiGraphics graphics, ResourceLocation location, float f) {
        if (location == POWDER_SNOW_OUTLINE_LOCATION) {
            return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.POWDER_SNOW);
        }
        return true;
    }

    /**
     * @reason Hide the portal overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPortalOverlay(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
    private boolean wrap_renderPortalOverlay_in_render_Spiffy(Gui instance, GuiGraphics $$0, float $$1) {
        return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PORTAL);
    }

}
