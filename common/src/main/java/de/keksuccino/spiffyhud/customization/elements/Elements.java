package de.keksuccino.spiffyhud.customization.elements;

import de.keksuccino.fancymenu.customization.element.ElementRegistry;
import de.keksuccino.spiffyhud.customization.elements.chatcustomizer.ChatCustomizerElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.huderaser.HudEraserElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.overlayremover.OverlayRemoverElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.player.PlayerElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.playernbthelper.PlayerNbtHelperElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.singlelinetext.SingleLineTextElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.slot.SlotElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.air.VanillaLikePlayerAirElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.armor.VanillaLikePlayerArmorElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.attackindicator.VanillaLikeAttackIndicatorElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.bossbars.VanillaLikeBossOverlayElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.effects.VanillaLikeEffectsElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.experience.VanillaLikeExperienceElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.food.VanillaLikePlayerFoodElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.hotbar.VanillaLikeHotbarElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.jumpmeter.VanillaLikeJumpMeterElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.mounthealth.VanillaLikeMountHealthElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.playerhealth.VanillaLikePlayerHealthElementBuilder;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.scoreboard.VanillaLikeScoreboardElementBuilder;

public class Elements {

    public static final VanillaLikeHotbarElementBuilder VANILLA_LIKE_HOTBAR = new VanillaLikeHotbarElementBuilder();
    public static final VanillaLikeScoreboardElementBuilder VANILLA_LIKE_SCOREBOARD = new VanillaLikeScoreboardElementBuilder();
    public static final VanillaLikePlayerHealthElementBuilder VANILLA_LIKE_PLAYER_HEALTH = new VanillaLikePlayerHealthElementBuilder();
    public static final VanillaLikePlayerFoodElementBuilder VANILLA_LIKE_PLAYER_FOOD = new VanillaLikePlayerFoodElementBuilder();
    public static final VanillaLikePlayerArmorElementBuilder VANILLA_LIKE_PLAYER_ARMOR = new VanillaLikePlayerArmorElementBuilder();
    public static final VanillaLikePlayerAirElementBuilder VANILLA_LIKE_PLAYER_AIR = new VanillaLikePlayerAirElementBuilder();
    public static final VanillaLikeExperienceElementBuilder VANILLA_LIKE_EXPERIENCE = new VanillaLikeExperienceElementBuilder();
    public static final VanillaLikeJumpMeterElementBuilder VANILLA_LIKE_JUMP_METER = new VanillaLikeJumpMeterElementBuilder();
    public static final VanillaLikeMountHealthElementBuilder VANILLA_LIKE_MOUNT_HEALTH = new VanillaLikeMountHealthElementBuilder();
    public static final VanillaLikeEffectsElementBuilder VANILLA_LIKE_EFFECTS = new VanillaLikeEffectsElementBuilder();
    public static final VanillaLikeBossOverlayElementBuilder VANILLA_LIKE_BOSS_OVERLAY = new VanillaLikeBossOverlayElementBuilder();
    public static final VanillaLikeAttackIndicatorElementBuilder VANILLA_LIKE_ATTACK_INDICATOR = new VanillaLikeAttackIndicatorElementBuilder();

    public static final SlotElementBuilder INVENTORY_SLOT = new SlotElementBuilder();
    public static final PlayerElementBuilder PLAYER = new PlayerElementBuilder();
    public static final OverlayRemoverElementBuilder OVERLAY_REMOVER = new OverlayRemoverElementBuilder();
    public static final ChatCustomizerElementBuilder CHAT_CUSTOMIZER = new ChatCustomizerElementBuilder();
    public static final PlayerNbtHelperElementBuilder PLAYER_NBT_HELPER = new PlayerNbtHelperElementBuilder();
    public static final SingleLineTextElementBuilder SINGLE_LINE_TEXT = new SingleLineTextElementBuilder();
    public static final HudEraserElementBuilder HUD_ERASER = new HudEraserElementBuilder();

    public static void registerAll() {

        ElementRegistry.register(VANILLA_LIKE_HOTBAR);
        ElementRegistry.register(VANILLA_LIKE_SCOREBOARD);
        ElementRegistry.register(VANILLA_LIKE_PLAYER_HEALTH);
        ElementRegistry.register(VANILLA_LIKE_PLAYER_FOOD);
        ElementRegistry.register(VANILLA_LIKE_PLAYER_ARMOR);
        ElementRegistry.register(VANILLA_LIKE_PLAYER_AIR);
        ElementRegistry.register(VANILLA_LIKE_EXPERIENCE);
        ElementRegistry.register(VANILLA_LIKE_JUMP_METER);
        ElementRegistry.register(VANILLA_LIKE_MOUNT_HEALTH);
        ElementRegistry.register(VANILLA_LIKE_EFFECTS);
        ElementRegistry.register(VANILLA_LIKE_BOSS_OVERLAY);
        ElementRegistry.register(VANILLA_LIKE_ATTACK_INDICATOR);

        ElementRegistry.register(INVENTORY_SLOT);
        ElementRegistry.register(PLAYER);
        ElementRegistry.register(OVERLAY_REMOVER);
        ElementRegistry.register(CHAT_CUSTOMIZER);
        ElementRegistry.register(PLAYER_NBT_HELPER);
        ElementRegistry.register(SINGLE_LINE_TEXT);
        ElementRegistry.register(HUD_ERASER);

    }

}
