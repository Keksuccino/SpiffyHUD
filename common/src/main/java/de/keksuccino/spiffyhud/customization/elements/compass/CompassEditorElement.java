package de.keksuccino.spiffyhud.customization.elements.compass;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CompassEditorElement extends AbstractEditorElement {

    public CompassEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setAutoSizingAllowed(false);
    }

    @Override
    public void init() {
        super.init();

        this.addColorInput("background_color", Component.translatable("spiffyhud.elements.player_compass.color.background"), CompassElement.DEFAULT_BACKGROUND_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.background.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "background_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().backgroundEnabled,
                        (editorElement, value) -> editorElement.getElement().backgroundEnabled = value,
                        "spiffyhud.elements.player_compass.background_enabled")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_background");

        this.addColorInput("bar_color", Component.translatable("spiffyhud.elements.player_compass.color.bar"), CompassElement.DEFAULT_BAR_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.bar.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "bar_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().barEnabled,
                        (editorElement, value) -> editorElement.getElement().barEnabled = value,
                        "spiffyhud.elements.player_compass.bar_enabled")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_bar");

        this.addColorInput("needle_color", Component.translatable("spiffyhud.elements.player_compass.color.needle"), CompassElement.DEFAULT_NEEDLE_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.needle.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "needle_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().needleTexture,
                        (editorElement, supplier) -> editorElement.getElement().needleTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.needle"),
                        true, null, true, true, true)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.needle.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "death_pointer_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().deathPointerTexture,
                        (editorElement, supplier) -> editorElement.getElement().deathPointerTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.death_pointer"),
                        true, null, true, true, true)
                .setStackable(true)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.death_pointer.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "needle_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().needleEnabled,
                        (editorElement, value) -> editorElement.getElement().needleEnabled = value,
                        "spiffyhud.elements.player_compass.needle_enabled")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_needle");

        this.addColorInput("major_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.major_tick"), CompassElement.DEFAULT_MAJOR_TICK_COLOR_STRING);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "major_ticks_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().majorTicksEnabled,
                        (editorElement, value) -> editorElement.getElement().majorTicksEnabled = value,
                        "spiffyhud.elements.player_compass.major_ticks_enabled")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_major_tick");

        this.addColorInput("minor_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.minor_tick"), CompassElement.DEFAULT_MINOR_TICK_COLOR_STRING);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "minor_ticks_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().minorTicksEnabled,
                        (editorElement, value) -> editorElement.getElement().minorTicksEnabled = value,
                        "spiffyhud.elements.player_compass.minor_ticks_enabled")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_minor_tick");

        this.addColorInput("cardinal_text_color", Component.translatable("spiffyhud.elements.player_compass.color.cardinal_text"), CompassElement.DEFAULT_CARDINAL_TEXT_COLOR_STRING);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "cardinal_text_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalTextEnabled,
                        (editorElement, value) -> editorElement.getElement().cardinalTextEnabled = value,
                        "spiffyhud.elements.player_compass.cardinal_text_enabled")
                .setStackable(true);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "cardinal_outline",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().cardinalOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.cardinal_outline")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_cardinal");

        this.addColorInput("degree_text_color", Component.translatable("spiffyhud.elements.player_compass.color.number_text"), CompassElement.DEFAULT_NUMBER_TEXT_COLOR_STRING);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "degree_numbers_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeNumbersEnabled,
                        (editorElement, value) -> editorElement.getElement().degreeNumbersEnabled = value,
                        "spiffyhud.elements.player_compass.degree_numbers_enabled")
                .setStackable(true);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "degree_outline",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().degreeOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.degree_outline")
                .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_after_degree");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "death_pointer_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().deathPointerEnabled,
                        (editorElement, value) -> editorElement.getElement().deathPointerEnabled = value,
                        "spiffyhud.elements.player_compass.death_pointer")
                .setStackable(true);

        this.addColorInput("death_pointer_color", Component.translatable("spiffyhud.elements.player_compass.color.death_pointer"), CompassElement.DEFAULT_DEATH_POINTER_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.death_pointer.desc")));

    }

    private ContextMenu.ClickableContextMenuEntry<?> addColorInput(String id, Component label, String defaultValue) {
        return this.addStringInputContextMenuEntryTo(this.rightClickMenu, id, CompassEditorElement.class,
                consumes -> getElementValue(consumes, id),
                (editorElement, value) -> setElementValue(editorElement, id, (value == null || value.isBlank()) ? defaultValue : value),
                null, false, true, label, true, defaultValue, null, null)
                .setStackable(true);
    }

    private String getElementValue(CompassEditorElement consumes, String key) {
        CompassElement element = consumes.getElement();
        return switch (key) {
            case "background_color" -> element.backgroundColor;
            case "bar_color" -> element.barColor;
            case "major_tick_color" -> element.majorTickColor;
            case "minor_tick_color" -> element.minorTickColor;
            case "cardinal_text_color" -> element.cardinalTextColor;
            case "number_text_color" -> element.numberTextColor;
            case "needle_color" -> element.needleColor;
            case "death_pointer_color" -> element.deathPointerColor;
            default -> null;
        };
    }

    private void setElementValue(CompassEditorElement consumes, String key, String value) {
        CompassElement element = consumes.getElement();
        switch (key) {
            case "background_color" -> element.backgroundColor = value;
            case "bar_color" -> element.barColor = value;
            case "major_tick_color" -> element.majorTickColor = value;
            case "minor_tick_color" -> element.minorTickColor = value;
            case "cardinal_text_color" -> element.cardinalTextColor = value;
            case "number_text_color" -> element.numberTextColor = value;
            case "needle_color" -> element.needleColor = value;
            case "death_pointer_color" -> element.deathPointerColor = value;
            default -> {
            }
        }
    }

    public CompassElement getElement() {
        return (CompassElement) this.element;
    }
}
