package de.keksuccino.spiffyhud.customization.elements.playerheartbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PlayerHeartHealthBarEditorElement extends AbstractEditorElement {

    public PlayerHeartHealthBarEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
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
                        PlayerHeartHealthBarEditorElement.class,
                        consumes -> consumes.getElement().scaleMultiplier,
                        (editorElement, value) -> editorElement.getElement().scaleMultiplier = (value == null || value.isBlank()) ? PlayerHeartHealthBarElement.DEFAULT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_heart_health_bar.scale"),
                        true, PlayerHeartHealthBarElement.DEFAULT_SCALE_STRING, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_heart_health_bar.scale.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_general");

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "hearts_per_row",
                        PlayerHeartHealthBarEditorElement.class,
                        consumes -> consumes.getElement().heartsPerRow,
                        (editorElement, value) -> editorElement.getElement().heartsPerRow = Math.max(1, value),
                        Component.translatable("spiffyhud.elements.player_heart_health_bar.hearts_per_row"),
                        true, 10, null, null)
                .setStackable(true);

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "heart_gap",
                        PlayerHeartHealthBarEditorElement.class,
                        consumes -> consumes.getElement().heartGap,
                        (editorElement, value) -> editorElement.getElement().heartGap = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_heart_health_bar.heart_gap"),
                        true, 1, null, null)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_heart_health_bar.heart_gap.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_heart");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "blink_on_loss",
                        PlayerHeartHealthBarEditorElement.class,
                        consumes -> consumes.getElement().blinkOnLoss,
                        (editorElement, value) -> editorElement.getElement().blinkOnLoss = value,
                        "spiffyhud.elements.player_heart_health_bar.blink")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_blink");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "shake_enabled",
                        PlayerHeartHealthBarEditorElement.class,
                        consumes -> consumes.getElement().lowHealthShakeEnabled,
                        (editorElement, value) -> editorElement.getElement().lowHealthShakeEnabled = value,
                        "spiffyhud.elements.player_heart_health_bar.shake")
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_heart_health_bar.shake.desc")));

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "shake_threshold",
                        PlayerHeartHealthBarEditorElement.class,
                        consumes -> consumes.getElement().lowHealthShakeThresholdHearts,
                        (editorElement, value) -> editorElement.getElement().lowHealthShakeThresholdHearts = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_heart_health_bar.shake_threshold"),
                        true, 4, null, null)
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_shake");

        ContextMenu texturesMenu = new ContextMenu();
        this.rightClickMenu.addSubMenuEntry("heart_textures",
                        Component.translatable("spiffyhud.elements.player_heart_health_bar.textures"),
                        texturesMenu)
                .setStackable(true)
                .setIcon(ContextMenu.IconFactory.getIcon("image"))
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_heart_health_bar.textures.desc")));

        for (PlayerHeartHealthBarElement.HeartTextureKind kind : PlayerHeartHealthBarElement.HeartTextureKind.values()) {
            this.addImageResourceChooserContextMenuEntryTo(texturesMenu,
                            "texture_" + kind.name().toLowerCase(),
                            PlayerHeartHealthBarEditorElement.class,
                            null,
                            consumes -> consumes.getElement().getCustomTexture(kind),
                            (editorElement, supplier) -> editorElement.getElement().setCustomTexture(kind, supplier),
                            Component.translatable(kind.getTranslationKey()),
                            true, null, true, true, true)
                    .setStackable(true);
        }

    }

    public PlayerHeartHealthBarElement getElement() {
        return (PlayerHeartHealthBarElement) this.element;
    }

}
