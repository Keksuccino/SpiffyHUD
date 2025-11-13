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
        this.settings.setParallaxAllowed(false);
        this.settings.setAutoSizingAllowed(false);
    }

    @Override
    public void init() {
        super.init();

        this.addColorInput("background_color", Component.translatable("spiffyhud.elements.player_compass.color.background"), CompassElement.DEFAULT_BACKGROUND_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.background.desc")));

        this.addColorInput("bar_color", Component.translatable("spiffyhud.elements.player_compass.color.bar"), CompassElement.DEFAULT_BAR_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.bar.desc")));

        this.addColorInput("major_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.major_tick"), CompassElement.DEFAULT_MAJOR_TICK_COLOR_STRING);

        this.addColorInput("minor_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.minor_tick"), CompassElement.DEFAULT_MINOR_TICK_COLOR_STRING);

        this.addColorInput("cardinal_text_color", Component.translatable("spiffyhud.elements.player_compass.color.cardinal_text"), CompassElement.DEFAULT_CARDINAL_TEXT_COLOR_STRING);

        this.addColorInput("number_text_color", Component.translatable("spiffyhud.elements.player_compass.color.number_text"), CompassElement.DEFAULT_NUMBER_TEXT_COLOR_STRING);

        this.addColorInput("needle_color", Component.translatable("spiffyhud.elements.player_compass.color.needle"), CompassElement.DEFAULT_NEEDLE_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.needle.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "cardinal_outline",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().cardinalOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.cardinal_outline")
                .setStackable(true);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "degree_outline",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().degreeOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.degree_outline")
                .setStackable(true);
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
            default -> {
            }
        }
    }

    public CompassElement getElement() {
        return (CompassElement) this.element;
    }
}
