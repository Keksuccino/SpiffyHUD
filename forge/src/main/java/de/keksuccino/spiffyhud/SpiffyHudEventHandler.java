package de.keksuccino.spiffyhud;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.spiffyhud.customization.SpiffyGui;
import de.keksuccino.spiffyhud.customization.VanillaHudElements;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElement;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpiffyHudEventHandler {

    private SpiffyGui spiffyGui = null;

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Pre event) {

        if (this.spiffyGui == null) this.spiffyGui = SpiffyGui.INSTANCE;

        // Renders Spiffy's overlay to the HUD
        if (!Minecraft.getInstance().options.hideGui) {
            spiffyGui.render(event.getGuiGraphics(), -10000000, -10000000, event.getPartialTick());
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) // Highest priority to stop mods from rendering custom stuff to overlay elements if the element is hidden
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {

        NamedGuiOverlay overlay = event.getOverlay();

        if ((overlay == VanillaGuiOverlay.TITLE_TEXT.type()) && VanillaHudElements.isHidden(VanillaHudElements.TITLE_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.SUBTITLES.type()) && VanillaHudElements.isHidden(VanillaHudElements.SUBTITLE_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.HOTBAR.type()) && VanillaHudElements.isHidden(VanillaHudElements.HOTBAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.JUMP_BAR.type()) && VanillaHudElements.isHidden(VanillaHudElements.JUMP_METER_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.EXPERIENCE_BAR.type()) && VanillaHudElements.isHidden(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.ITEM_NAME.type()) && VanillaHudElements.isHidden(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.SCOREBOARD.type()) && VanillaHudElements.isHidden(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.FOOD_LEVEL.type()) && VanillaHudElements.isHidden(VanillaHudElements.FOOD_BAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.ARMOR_LEVEL.type()) && VanillaHudElements.isHidden(VanillaHudElements.ARMOR_BAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.AIR_LEVEL.type()) && VanillaHudElements.isHidden(VanillaHudElements.AIR_BAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.MOUNT_HEALTH.type()) && VanillaHudElements.isHidden(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.PLAYER_HEALTH.type()) && VanillaHudElements.isHidden(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.RECORD_OVERLAY.type()) && VanillaHudElements.isHidden(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.CROSSHAIR.type()) && VanillaHudElements.isHidden(VanillaHudElements.CROSSHAIR_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.BOSS_EVENT_PROGRESS.type()) && VanillaHudElements.isHidden(VanillaHudElements.BOSS_BARS_IDENTIFIER)) {
            event.setCanceled(true);
            return;
        }
        if ((overlay == VanillaGuiOverlay.POTION_ICONS.type()) && VanillaHudElements.isHidden(VanillaHudElements.EFFECTS_IDENTIFIER)) {
            event.setCanceled(true);
        }

        // OVERLAYS
        if ((overlay == VanillaGuiOverlay.VIGNETTE.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.VIGNETTE)) {
            event.setCanceled(true);
        }
        if ((overlay == VanillaGuiOverlay.FROSTBITE.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.POWDER_SNOW)) {
            event.setCanceled(true);
        }
        if ((overlay == VanillaGuiOverlay.HELMET.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PUMPKIN)) {
            event.setCanceled(true);
        }
        if ((overlay == VanillaGuiOverlay.SPYGLASS.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.SPYGLASS)) {
            event.setCanceled(true);
        }
        if ((overlay == VanillaGuiOverlay.PORTAL.type()) && OverlayRemoverElement.isOverlayTypeHidden(OverlayRemoverElement.OverlayType.PORTAL)) {
            event.setCanceled(true);
        }

    }
}
