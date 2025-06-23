package de.keksuccino.spiffyhud.customization;

import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetElement;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.spiffyhud.SpiffyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class VanillaHudElements {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String HOTBAR_IDENTIFIER = "spiffy_hotbar_dummy";
    public static final String CONTEXTUAL_BAR_IDENTIFIER = "spiffy_experience_bar_dummy";
    public static final String SELECTED_ITEM_NAME_IDENTIFIER = "spiffy_selected_item_name_dummy";
    public static final String SCOREBOARD_SIDEBAR_IDENTIFIER = "spiffy_scoreboard_sidebar_dummy";
    public static final String FOOD_BAR_IDENTIFIER = "spiffy_food_bar_dummy";
    public static final String ARMOR_BAR_IDENTIFIER = "spiffy_armor_bar_dummy";
    public static final String AIR_BAR_IDENTIFIER = "spiffy_air_bar_dummy";
    public static final String MOUNT_HEALTH_BAR_IDENTIFIER = "spiffy_mount_health_bar_dummy";
    public static final String PLAYER_HEALTH_BAR_IDENTIFIER = "spiffy_health_bar_dummy";
    /** The overlay message gets displayed above the hotbar and shows stuff like "Playing now: ..." when using the Jukebox. This is not the same as the text that displays the item name when switching the hotbar slot. **/
    public static final String OVERLAY_MESSAGE_IDENTIFIER = "spiffy_overlay_message_dummy";
    public static final String CROSSHAIR_IDENTIFIER = "spiffy_crosshair_dummy";
    /** The attack indicator element covers both indicator types (hotbar and crosshair). Make sure to also check if the hotbar or crosshair is hidden, because the attack indicator hides with these elements. **/
    public static final String ATTACK_INDICATOR_IDENTIFIER = "spiffy_attack_indicator_dummy";
    /** This is only the first line of the title. The subtitle is handled separately. **/
    public static final String TITLE_IDENTIFIER = "spiffy_title_dummy";
    /** This is not the subtitles overlay, but the second line of the "title" element. They are handled separately in Spiffy HUD. **/
    public static final String SUBTITLE_IDENTIFIER = "spiffy_subtitle_dummy";
    /** The boss bars element is basically the whole boss bar overlay, so all active boss bars and boss names are covered. **/
    public static final String BOSS_BARS_IDENTIFIER = "spiffy_boss_bars_dummy";
    /** The effects element is the whole effects overlay, so all active effects are covered. **/
    public static final String EFFECTS_IDENTIFIER = "spiffy_effects_dummy";

    /**
     * Checks if a Vanilla HUD element is hidden in Spiffy HUD.<br>
     * This is useful for when mods want to add support for their HUD elements that override Vanilla elements.
     *
     * @param elementIdentifier The identifier of the Vanilla HUD element. All identifiers can be found in {@link VanillaHudElements}.
     */
    public static boolean isHidden(@NotNull String elementIdentifier) {
        try {
            ScreenCustomizationLayer layer = ScreenCustomizationLayerHandler.getLayerOfScreen(SpiffyUtils.DUMMY_SPIFFY_OVERLAY_SCREEN);
            if (layer != null) {
                if (layer.getElementByInstanceIdentifier(elementIdentifier) instanceof VanillaWidgetElement e) {
                    return e.isHidden();
                }
            }
        } catch (Exception ex) {
            LOGGER.error("[SPIFFY HUD] Failed to check if Vanilla HUD element is hidden!", ex);
        }
        return false;
    }

}
