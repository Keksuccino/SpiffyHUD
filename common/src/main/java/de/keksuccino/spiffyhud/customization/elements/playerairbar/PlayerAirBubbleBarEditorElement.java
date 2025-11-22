package de.keksuccino.spiffyhud.customization.elements.playerairbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PlayerAirBubbleBarEditorElement extends AbstractEditorElement {

    public PlayerAirBubbleBarEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
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
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().scaleMultiplier,
                        (editorElement, value) -> editorElement.getElement().scaleMultiplier = (value == null || value.isBlank()) ? PlayerAirBubbleBarElement.DEFAULT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_air_bubble_bar.scale"),
                        true, PlayerAirBubbleBarElement.DEFAULT_SCALE_STRING, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_air_bubble_bar.scale.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_general");

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "bubbles_per_row",
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().bubblesPerRow,
                        (editorElement, value) -> editorElement.getElement().bubblesPerRow = Math.max(1, value),
                        Component.translatable("spiffyhud.elements.player_air_bubble_bar.bubbles_per_row"),
                        true, 10, null, null)
                .setStackable(true);

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "bubble_gap",
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().bubbleGap,
                        (editorElement, value) -> editorElement.getElement().bubbleGap = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_air_bubble_bar.bubble_gap"),
                        true, 1, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_air_bubble_bar.bubble_gap.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_bubble_general");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "pop_animation",
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().blinkOnLoss,
                        (editorElement, value) -> editorElement.getElement().blinkOnLoss = value,
                        "spiffyhud.elements.player_air_bubble_bar.pop_animation")
                .setStackable(true);

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "pop_duration_ms",
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().poppingDurationMs,
                        (editorElement, value) -> editorElement.getElement().poppingDurationMs = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_air_bubble_bar.pop_duration"),
                        true, PlayerAirBubbleBarElement.DEFAULT_POPPING_DURATION_MS, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_air_bubble_bar.pop_duration.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_bubble_pop");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "shake_enabled",
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().lowAirShakeEnabled,
                        (editorElement, value) -> editorElement.getElement().lowAirShakeEnabled = value,
                        "spiffyhud.elements.player_air_bubble_bar.shake")
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_air_bubble_bar.shake.desc")));

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "shake_threshold",
                        PlayerAirBubbleBarEditorElement.class,
                        consumes -> consumes.getElement().lowAirShakeThresholdBubbles,
                        (editorElement, value) -> editorElement.getElement().lowAirShakeThresholdBubbles = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_air_bubble_bar.shake_threshold"),
                        true, 4, null, null)
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_before_textures");

        ContextMenu texturesMenu = new ContextMenu();
        this.rightClickMenu.addSubMenuEntry("air_textures",
                        Component.translatable("spiffyhud.elements.player_air_bubble_bar.textures"),
                        texturesMenu)
                .setStackable(true)
                .setIcon(ContextMenu.IconFactory.getIcon("image"))
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_air_bubble_bar.textures.desc")));

        for (PlayerAirBubbleBarElement.AirTextureKind kind : PlayerAirBubbleBarElement.AirTextureKind.values()) {
            this.addImageResourceChooserContextMenuEntryTo(texturesMenu,
                            "texture_" + kind.name().toLowerCase(),
                            PlayerAirBubbleBarEditorElement.class,
                            null,
                            consumes -> consumes.getElement().getCustomTexture(kind),
                            (editorElement, supplier) -> editorElement.getElement().setCustomTexture(kind, supplier),
                            Component.translatable(kind.getTranslationKey()),
                            true, null, true, true, true)
                    .setStackable(true);
        }

    }

    public PlayerAirBubbleBarElement getElement() {
        return (PlayerAirBubbleBarElement) this.element;
    }

}
