package de.keksuccino.spiffyhud.customization.elements.playerfoodbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PlayerFoodBarEditorElement extends AbstractEditorElement {

    public PlayerFoodBarEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setStretchable(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setResizeable(false);
        this.settings.setParallaxAllowed(false);
        this.settings.setAutoSizingAllowed(false);
    }

    @Override
    public void init() {
        super.init();

        this.rightClickMenu.addValueCycleEntry("body_alignment", SpiffyAlignment.TOP_LEFT.cycle(this.getElement().spiffyAlignment)
                        .addCycleListener(alignment -> {
                            editor.history.saveSnapshot();
                            this.getElement().spiffyAlignment = alignment;
                        }))
                .setStackable(false);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "scale_multiplier",
                        PlayerFoodBarEditorElement.class,
                        consumes -> consumes.getElement().scaleMultiplier,
                        (editorElement, value) -> editorElement.getElement().scaleMultiplier = (value == null || value.isBlank()) ? PlayerFoodBarElement.DEFAULT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_food_bar.scale"),
                        true, PlayerFoodBarElement.DEFAULT_SCALE_STRING, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_food_bar.scale.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_general");

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "icons_per_row",
                        PlayerFoodBarEditorElement.class,
                        consumes -> consumes.getElement().iconsPerRow,
                        (editorElement, value) -> editorElement.getElement().iconsPerRow = Math.max(1, value),
                        Component.translatable("spiffyhud.elements.player_food_bar.icons_per_row"),
                        true, 10, null, null)
                .setStackable(true);

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "icon_gap",
                        PlayerFoodBarEditorElement.class,
                        consumes -> consumes.getElement().iconGap,
                        (editorElement, value) -> editorElement.getElement().iconGap = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_food_bar.icon_gap"),
                        true, 1, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_food_bar.icon_gap.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_icon");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "blink_on_loss",
                        PlayerFoodBarEditorElement.class,
                        consumes -> consumes.getElement().blinkOnLoss,
                        (editorElement, value) -> editorElement.getElement().blinkOnLoss = value,
                        "spiffyhud.elements.player_food_bar.blink")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_blink");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "shake_enabled",
                        PlayerFoodBarEditorElement.class,
                        consumes -> consumes.getElement().lowFoodShakeEnabled,
                        (editorElement, value) -> editorElement.getElement().lowFoodShakeEnabled = value,
                        "spiffyhud.elements.player_food_bar.shake")
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_food_bar.shake.desc")));

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "shake_threshold",
                        PlayerFoodBarEditorElement.class,
                        consumes -> consumes.getElement().lowFoodShakeThresholdIcons,
                        (editorElement, value) -> editorElement.getElement().lowFoodShakeThresholdIcons = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_food_bar.shake_threshold"),
                        true, 4, null, null)
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_shake");

        ContextMenu texturesMenu = new ContextMenu();
        this.rightClickMenu.addSubMenuEntry("food_textures",
                        Component.translatable("spiffyhud.elements.player_food_bar.textures"),
                        texturesMenu)
                .setStackable(true)
                .setIcon(ContextMenu.IconFactory.getIcon("image"))
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_food_bar.textures.desc")));

        for (PlayerFoodBarElement.FoodTextureKind kind : PlayerFoodBarElement.FoodTextureKind.values()) {
            this.addImageResourceChooserContextMenuEntryTo(texturesMenu,
                            "texture_" + kind.name().toLowerCase(),
                            PlayerFoodBarEditorElement.class,
                            null,
                            consumes -> consumes.getElement().getCustomTexture(kind),
                            (editorElement, supplier) -> editorElement.getElement().setCustomTexture(kind, supplier),
                            Component.translatable(kind.getTranslationKey()),
                            true, null, true, true, true)
                    .setStackable(true);
        }

    }

    public PlayerFoodBarElement getElement() {
        return (PlayerFoodBarElement) this.element;
    }

}
