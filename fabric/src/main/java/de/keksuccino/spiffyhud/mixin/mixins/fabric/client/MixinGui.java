package de.keksuccino.spiffyhud.mixin.mixins.fabric.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import de.keksuccino.fancymenu.util.rendering.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui {

    @Shadow @Final private static ResourceLocation PUMPKIN_BLUR_LOCATION;
    @Shadow @Final private static ResourceLocation POWDER_SNOW_OUTLINE_LOCATION;
    @Shadow private Component overlayMessageString;
    @Shadow private Component title;
    @Shadow private Component subtitle;

    @Unique private SpiffyGui spiffyGui = null;

    @Shadow protected abstract int getVehicleMaxHearts(LivingEntity $$0);

    @Shadow protected abstract LivingEntity getPlayerVehicleWithHealth();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 2))
    private void before_render_Chat_Spiffy(PoseStack pose, float partial, CallbackInfo info) {

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        if (!Minecraft.getInstance().options.hideGui) {
            spiffyGui.render(GuiGraphics.currentGraphics(), -10000000, -10000000, partial);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }

    }

    /**
     * @reason Hide the hotbar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean wrap_renderHotbar_in_render_Spiffy(Gui instance, float l1, PoseStack j1) {
        return !VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER);
    }

    /**
     * @reason Hide the jump meter when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderJumpMeter(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
    private boolean wrap_renderJumpMeter_in_render_Spiffy(Gui instance, PoseStack p_93034_, int p_93035_) {
        return !VanillaHudElements.isHidden(VanillaHudElements.JUMP_METER_IDENTIFIER);
    }

    /**
     * @reason Hide the EXP bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
    private boolean wrap_renderExperienceBar_in_render_Spiffy(Gui instance, PoseStack k, int l) {
        return !VanillaHudElements.isHidden(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the selected item name when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean wrap_renderSelectedItemName_in_render_Spiffy(Gui instance, PoseStack mutablecomponent) {
        return !VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER);
    }

    /**
     * @reason Hide the scoreboard sidebar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/scores/Objective;)V"))
    private boolean wrap_displayScoreboardSidebar_in_render_Spiffy(Gui instance, PoseStack component1, Objective score) {
        return !VanillaHudElements.isHidden(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER);
    }

    /**
     * @reason Hide the crosshair when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean wrap_renderCrosshair_in_render_Spiffy(Gui instance, PoseStack posestack) {
        return !VanillaHudElements.isHidden(VanillaHudElements.CROSSHAIR_IDENTIFIER);
    }

    /**
     * @reason Hide the boss overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean wrap_BossOverlay_render_in_render_Spiffy(BossHealthOverlay instance, PoseStack l) {
        return !VanillaHudElements.isHidden(VanillaHudElements.BOSS_BARS_IDENTIFIER);
    }

    /**
     * @reason Hide the mount health bar when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean wrap_renderVehicleHealth_in_render_Spiffy(Gui instance, PoseStack j2) {
        return !VanillaHudElements.isHidden(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the overlay message, title and subtitle when hidden by Spiffy HUD.
     */
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"))
    private int wrap_drawString_in_render_Spiffy(Font instance, PoseStack pose, Component component, float p_92766_, float p_92767_, int p_92768_, Operation<Integer> original) {
        if (component != null) {
            if ((component == this.overlayMessageString) && VanillaHudElements.isHidden(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER)) return 0;
            if ((component == this.title) && VanillaHudElements.isHidden(VanillaHudElements.TITLE_IDENTIFIER)) return 0;
            if ((component == this.subtitle) && VanillaHudElements.isHidden(VanillaHudElements.SUBTITLE_IDENTIFIER)) return 0;
        }
        return original.call(instance, pose, component, p_92766_, p_92767_, p_92768_);
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
    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"))
    private boolean wrap_renderHearts_in_renderPlayerHealth_Spiffy(Gui instance, PoseStack k2, Player flag2, int flag3, int j1, int k1, int l1, float i2, int j2, int flag, int i1, boolean b) {
        return !VanillaHudElements.isHidden(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER);
    }

    /**
     * @reason Hide the effects overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean wrap_renderEffects_in_render_Spiffy(Gui instance, PoseStack k) {
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
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/world/entity/Entity;)V"))
    private boolean wrap_renderVignette_in_render_Spiffy(Gui instance, Entity entity) {
        return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.VIGNETTE);
    }

    /**
     * @reason Hide the spyglass overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSpyglassOverlay(F)V"))
    private boolean wrap_renderSpyglassOverlay_in_render_Spiffy(Gui instance, float p_168676_) {
        return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.SPYGLASS);
    }

    /**
     * @reason Hide the pumpkin overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderTextureOverlay(Lnet/minecraft/resources/ResourceLocation;F)V"))
    private boolean wrap_pumpkin_overlay_rendering_in_render_Spiffy(Gui instance, ResourceLocation location, float p_168710_) {
        if (location == PUMPKIN_BLUR_LOCATION) {
            return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN);
        }
        return true;
    }

    /**
     * @reason Hide the powder snow overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderTextureOverlay(Lnet/minecraft/resources/ResourceLocation;F)V"))
    private boolean wrap_powder_snow_overlay_rendering_in_render_Spiffy(Gui instance, ResourceLocation location, float p_168710_) {
        if (location == POWDER_SNOW_OUTLINE_LOCATION) {
            return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.POWDER_SNOW);
        }
        return true;
    }

    /**
     * @reason Hide the portal overlay when hidden by Spiffy HUD.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPortalOverlay(F)V"))
    private boolean wrap_renderPortalOverlay_in_render_Spiffy(Gui instance, float p_93008_) {
        return !OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PORTAL);
    }

}