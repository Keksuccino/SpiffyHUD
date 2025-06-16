package de.keksuccino.spiffyhud.mixin.mixins.forge.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.SpiffyRenderer;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
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
public class MixinGui {

    @Shadow
    private Component title;
    @Shadow private Component subtitle;

    @Shadow @Final
    private static ResourceLocation PUMPKIN_BLUR_LOCATION;

    @Shadow @Final private static ResourceLocation POWDER_SNOW_OUTLINE_LOCATION;

    /**
     * @reason Hide the hotbar when hidden by Spiffy HUD and renders Spiffy's overlay.
     */
    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderHotbarAndDecorations_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {

        SpiffyRenderer.render(graphics, deltaTracker);

        SpiffyRenderer.startStencil(graphics);

        if (VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER)) info.cancel();

    }

    @Inject(method = "render", at = @At("RETURN"))
    private void after_render_Spiffy(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo info) {
        SpiffyRenderer.finishStencil(graphics);
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
     * @reason Hide the pumpkin overlay and the powder snow overlay when hidden by Spiffy HUD.
     */
    @Inject(method = "renderTextureOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void before_renderTextureOverlay_Spiffy(GuiGraphics guiGraphics, ResourceLocation location, float alpha, CallbackInfo info) {
        if ((location == PUMPKIN_BLUR_LOCATION) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN)) info.cancel();
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
