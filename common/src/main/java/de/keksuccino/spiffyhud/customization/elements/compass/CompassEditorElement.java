package de.keksuccino.spiffyhud.customization.elements.compass;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.fancymenu.util.rendering.ui.screen.ConfirmationScreen;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.spiffyhud.customization.marker.MarkerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class CompassEditorElement extends AbstractEditorElement {

    public CompassEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setAutoSizingAllowed(false);
    }

    @Override
    public void init() {
        super.init();

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "background_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().backgroundEnabled,
                        (editorElement, value) -> editorElement.getElement().backgroundEnabled = value,
                        "spiffyhud.elements.player_compass.background_enabled")
                .setStackable(false);

        this.addColorInput("background_color", Component.translatable("spiffyhud.elements.player_compass.color.background"), CompassElement.DEFAULT_BACKGROUND_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.background.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_after_background");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "bar_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().barEnabled,
                        (editorElement, value) -> editorElement.getElement().barEnabled = value,
                        "spiffyhud.elements.player_compass.bar_enabled")
                .setStackable(false);

        this.addColorInput("bar_color", Component.translatable("spiffyhud.elements.player_compass.color.bar"), CompassElement.DEFAULT_BAR_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.bar.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "bar_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().barTexture,
                        (editorElement, supplier) -> editorElement.getElement().barTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.bar"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.bar.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_bar");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "needle_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().needleEnabled,
                        (editorElement, value) -> editorElement.getElement().needleEnabled = value,
                        "spiffyhud.elements.player_compass.needle_enabled")
                .setStackable(false);

        this.addColorInput("needle_color", Component.translatable("spiffyhud.elements.player_compass.color.needle"), CompassElement.DEFAULT_NEEDLE_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.needle.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "needle_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().needleYOffset,
                        (editorElement, value) -> editorElement.getElement().needleYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.needle.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.needle.y_offset.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "needle_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().needleTexture,
                        (editorElement, supplier) -> editorElement.getElement().needleTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.needle"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.needle.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_needle");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "cardinal_ticks_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalTicksEnabled,
                        (editorElement, value) -> editorElement.getElement().cardinalTicksEnabled = value,
                        "spiffyhud.elements.player_compass.cardinal_ticks_enabled")
                .setStackable(false);

        this.addColorInput("cardinal_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.cardinal_tick"), CompassElement.DEFAULT_CARDINAL_TICK_COLOR_STRING);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "cardinal_tick_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalTickYOffset,
                        (editorElement, value) -> editorElement.getElement().cardinalTickYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TICK_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.cardinal_ticks.y_offset"),
                        true, CompassElement.DEFAULT_TICK_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.cardinal_ticks.y_offset.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "cardinal_tick_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().cardinalTickTexture,
                        (editorElement, supplier) -> editorElement.getElement().cardinalTickTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.cardinal_tick"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.cardinal_tick.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_cardinal_tick");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "degree_ticks_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeTicksEnabled,
                        (editorElement, value) -> editorElement.getElement().degreeTicksEnabled = value,
                        "spiffyhud.elements.player_compass.degree_ticks_enabled")
                .setStackable(false);

        this.addColorInput("degree_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.degree_tick"), CompassElement.DEFAULT_DEGREE_TICK_COLOR_STRING);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "degree_tick_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeTickYOffset,
                        (editorElement, value) -> editorElement.getElement().degreeTickYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TICK_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.degree_ticks.y_offset"),
                        true, CompassElement.DEFAULT_TICK_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.degree_ticks.y_offset.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "degree_tick_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().degreeTickTexture,
                        (editorElement, supplier) -> editorElement.getElement().degreeTickTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.degree_tick"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.degree_tick.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_degree_tick");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "minor_ticks_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().minorTicksEnabled,
                        (editorElement, value) -> editorElement.getElement().minorTicksEnabled = value,
                        "spiffyhud.elements.player_compass.minor_ticks_enabled")
                .setStackable(false);

        this.addColorInput("minor_tick_color", Component.translatable("spiffyhud.elements.player_compass.color.minor_tick"), CompassElement.DEFAULT_MINOR_TICK_COLOR_STRING);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "minor_tick_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().minorTickYOffset,
                        (editorElement, value) -> editorElement.getElement().minorTickYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TICK_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.minor_ticks.y_offset"),
                        true, CompassElement.DEFAULT_TICK_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.minor_ticks.y_offset.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "minor_tick_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().minorTickTexture,
                        (editorElement, supplier) -> editorElement.getElement().minorTickTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.minor_tick"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.minor_tick.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_minor_tick");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "cardinal_text_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalTextEnabled,
                        (editorElement, value) -> editorElement.getElement().cardinalTextEnabled = value,
                        "spiffyhud.elements.player_compass.cardinal_text_enabled")
                .setStackable(false);

        this.addColorInput("cardinal_text_color", Component.translatable("spiffyhud.elements.player_compass.color.cardinal_text"), CompassElement.DEFAULT_CARDINAL_TEXT_COLOR_STRING);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "cardinal_text_scale",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalTextScale,
                        (editorElement, value) -> editorElement.getElement().cardinalTextScale = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.cardinal_text.scale"),
                        true, CompassElement.DEFAULT_TEXT_SCALE_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.cardinal_text.scale.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "cardinal_text_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalTextYOffset,
                        (editorElement, value) -> editorElement.getElement().cardinalTextYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.cardinal_text.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.cardinal_text.y_offset.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "cardinal_texture_north",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().northCardinalTexture,
                        (editorElement, supplier) -> editorElement.getElement().northCardinalTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.cardinal.north"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.cardinal.north.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "cardinal_texture_east",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().eastCardinalTexture,
                        (editorElement, supplier) -> editorElement.getElement().eastCardinalTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.cardinal.east"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.cardinal.east.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "cardinal_texture_south",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().southCardinalTexture,
                        (editorElement, supplier) -> editorElement.getElement().southCardinalTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.cardinal.south"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.cardinal.south.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "cardinal_texture_west",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().westCardinalTexture,
                        (editorElement, supplier) -> editorElement.getElement().westCardinalTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.cardinal.west"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.cardinal.west.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "cardinal_outline",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().cardinalOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().cardinalOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.cardinal_outline")
                .setStackable(false);

        this.rightClickMenu.addSeparatorEntry("separator_after_cardinal");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "degree_numbers_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeNumbersEnabled,
                        (editorElement, value) -> editorElement.getElement().degreeNumbersEnabled = value,
                        "spiffyhud.elements.player_compass.degree_numbers_enabled")
                .setStackable(false);

        this.addColorInput("degree_text_color", Component.translatable("spiffyhud.elements.player_compass.color.number_text"), CompassElement.DEFAULT_NUMBER_TEXT_COLOR_STRING);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "degree_text_scale",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeTextScale,
                        (editorElement, value) -> editorElement.getElement().degreeTextScale = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.degree_text.scale"),
                        true, CompassElement.DEFAULT_TEXT_SCALE_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.degree_text.scale.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "degree_text_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeTextYOffset,
                        (editorElement, value) -> editorElement.getElement().degreeTextYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.degree_text.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.degree_text.y_offset.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "degree_outline",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().degreeOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().degreeOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.degree_outline")
                .setStackable(false);

        this.rightClickMenu.addSeparatorEntry("separator_after_degree");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "death_pointer_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().deathPointerEnabled,
                        (editorElement, value) -> editorElement.getElement().deathPointerEnabled = value,
                        "spiffyhud.elements.player_compass.death_pointer")
                .setStackable(false);

        this.addColorInput("death_pointer_color", Component.translatable("spiffyhud.elements.player_compass.color.death_pointer"), CompassElement.DEFAULT_DEATH_POINTER_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.death_pointer.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "death_pointer_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().deathPointerYOffset,
                        (editorElement, value) -> editorElement.getElement().deathPointerYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.death_pointer.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.death_pointer.y_offset.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "death_pointer_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().deathPointerTexture,
                        (editorElement, supplier) -> editorElement.getElement().deathPointerTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.death_pointer"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.death_pointer.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_death_pointer");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "world_markers_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().worldMarkersEnabled,
                        (editorElement, value) -> editorElement.getElement().worldMarkersEnabled = value,
                        "spiffyhud.elements.player_compass.world_markers")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.world_markers.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_dots_scale",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerDotScale,
                        (editorElement, value) -> editorElement.getElement().markerDotScale = (value == null || value.isBlank()) ? CompassElement.DEFAULT_DOT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker_dots.scale"),
                        true, CompassElement.DEFAULT_DOT_SCALE_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker_dots.scale.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_dot_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerDotYOffset,
                        (editorElement, value) -> editorElement.getElement().markerDotYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker.dot_y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.dot_y_offset.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_needle_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerNeedleYOffset,
                        (editorElement, value) -> editorElement.getElement().markerNeedleYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker.needle_y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.needle_y_offset.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "marker_labels_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerLabelsEnabled,
                        (editorElement, value) -> editorElement.getElement().markerLabelsEnabled = value,
                        "spiffyhud.elements.player_compass.marker.labels")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.labels.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "marker_label_outline_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerLabelOutlineEnabled,
                        (editorElement, value) -> editorElement.getElement().markerLabelOutlineEnabled = value,
                        "spiffyhud.elements.player_compass.marker.labels.outline")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.labels.outline.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_dot_label_x_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerDotLabelXOffset,
                        (editorElement, value) -> editorElement.getElement().markerDotLabelXOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker.dot_label.x_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.dot_label.x_offset.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_dot_label_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerDotLabelYOffset,
                        (editorElement, value) -> editorElement.getElement().markerDotLabelYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker.dot_label.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.dot_label.y_offset.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_needle_label_x_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerNeedleLabelXOffset,
                        (editorElement, value) -> editorElement.getElement().markerNeedleLabelXOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker.needle_label.x_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.needle_label.x_offset.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "marker_needle_label_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().markerNeedleLabelYOffset,
                        (editorElement, value) -> editorElement.getElement().markerNeedleLabelYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.marker.needle_label.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.marker.needle_label.y_offset.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_before_world_markers_clear");

        this.rightClickMenu.addClickableEntry("world_markers_clear", Component.translatable("spiffyhud.elements.player_compass.world_markers.clear"), (menu, entry) -> {
                    this.rightClickMenu.closeMenu();
                    ConfirmationScreen confirmation = ConfirmationScreen.critical(confirmed -> {
                        if (confirmed) {
                            this.clearMarkers();
                        }
                        Minecraft.getInstance().setScreen(this.editor);
                    }, Component.translatable("spiffyhud.elements.player_compass.world_markers.clear.confirm"));
                    Minecraft.getInstance().setScreen(confirmation);
                }).setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.world_markers.clear.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_before_mob_dots_move_up_down");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "mob_dots_move_up_down",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().mobDotsMoveUpDown,
                        (editorElement, value) -> editorElement.getElement().mobDotsMoveUpDown = value,
                        "spiffyhud.elements.player_compass.mob_dots.move")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.mob_dots.move.desc")));

        this.rightClickMenu.addSeparatorEntry("separator_before_hostile_dots_enabled");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "hostile_dots_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().hostileDotsEnabled,
                        (editorElement, value) -> editorElement.getElement().hostileDotsEnabled = value,
                        "spiffyhud.elements.player_compass.hostile_dots")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.hostile_dots.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "hostile_dots_heads_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().hostileDotsShowHeads,
                        (editorElement, value) -> editorElement.getElement().hostileDotsShowHeads = value,
                        "spiffyhud.elements.player_compass.hostile_dots.heads")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.hostile_dots.heads.desc")));

        this.addColorInput("hostile_dots_color", Component.translatable("spiffyhud.elements.player_compass.color.hostile_dots"), CompassElement.DEFAULT_HOSTILE_DOT_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.hostile_dots.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "hostile_dots_scale",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().hostileDotScale,
                        (editorElement, value) -> editorElement.getElement().hostileDotScale = (value == null || value.isBlank()) ? CompassElement.DEFAULT_DOT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.hostile_dots.scale"),
                        true, CompassElement.DEFAULT_DOT_SCALE_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.hostile_dots.scale.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "hostile_dots_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().hostileDotsYOffset,
                        (editorElement, value) -> editorElement.getElement().hostileDotsYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.hostile_dots.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.hostile_dots.y_offset.desc")));

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "hostile_dots_range",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().hostileDotsRange,
                        (editorElement, value) -> editorElement.getElement().hostileDotsRange = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_compass.hostile_dots.range"),
                        true, 200, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.hostile_dots.range.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "hostile_dots_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().hostileDotTexture,
                        (editorElement, supplier) -> editorElement.getElement().hostileDotTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.hostile_dots"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.hostile_dots.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

        this.rightClickMenu.addSeparatorEntry("separator_after_hostile_dots");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "passive_dots_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().passiveDotsEnabled,
                        (editorElement, value) -> editorElement.getElement().passiveDotsEnabled = value,
                        "spiffyhud.elements.player_compass.passive_dots")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.passive_dots.desc")));

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "passive_dots_heads_enabled",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().passiveDotsShowHeads,
                        (editorElement, value) -> editorElement.getElement().passiveDotsShowHeads = value,
                        "spiffyhud.elements.player_compass.passive_dots.heads")
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.passive_dots.heads.desc")));

        this.addColorInput("passive_dots_color", Component.translatable("spiffyhud.elements.player_compass.color.passive_dots"), CompassElement.DEFAULT_PASSIVE_DOT_COLOR_STRING)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.color.passive_dots.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "passive_dots_scale",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().passiveDotScale,
                        (editorElement, value) -> editorElement.getElement().passiveDotScale = (value == null || value.isBlank()) ? CompassElement.DEFAULT_DOT_SCALE_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.passive_dots.scale"),
                        true, CompassElement.DEFAULT_DOT_SCALE_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.passive_dots.scale.desc")));

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "passive_dots_y_offset",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().passiveDotsYOffset,
                        (editorElement, value) -> editorElement.getElement().passiveDotsYOffset = (value == null || value.isBlank()) ? CompassElement.DEFAULT_TEXT_OFFSET_STRING : value,
                        null, false, true,
                        Component.translatable("spiffyhud.elements.player_compass.passive_dots.y_offset"),
                        true, CompassElement.DEFAULT_TEXT_OFFSET_STRING, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.passive_dots.y_offset.desc")));

        this.addIntegerInputContextMenuEntryTo(this.rightClickMenu, "passive_dots_range",
                        CompassEditorElement.class,
                        consumes -> consumes.getElement().passiveDotsRange,
                        (editorElement, value) -> editorElement.getElement().passiveDotsRange = Math.max(0, value),
                        Component.translatable("spiffyhud.elements.player_compass.passive_dots.range"),
                        true, 200, null, null)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.passive_dots.range.desc")));

        this.addImageResourceChooserContextMenuEntryTo(this.rightClickMenu,
                        "passive_dots_texture",
                        CompassEditorElement.class,
                        null,
                        consumes -> consumes.getElement().passiveDotTexture,
                        (editorElement, supplier) -> editorElement.getElement().passiveDotTexture = supplier,
                        Component.translatable("spiffyhud.elements.player_compass.texture.passive_dots"),
                        true, null, true, true, true)
                .setStackable(false)
                .setTooltipSupplier((menu, entry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.texture.passive_dots.desc")))
                .setIcon(ContextMenu.IconFactory.getIcon("image"));

    }

    private ContextMenu.ClickableContextMenuEntry<?> addColorInput(String id, Component label, String defaultValue) {
        return this.addStringInputContextMenuEntryTo(this.rightClickMenu, id, CompassEditorElement.class,
                        consumes -> getElementValue(consumes, id),
                        (editorElement, value) -> setElementValue(editorElement, id, (value == null || value.isBlank()) ? defaultValue : value),
                        null, false, true, label, true, defaultValue, null, null)
                .setStackable(false);
    }

    private String getElementValue(CompassEditorElement consumes, String key) {
        CompassElement element = consumes.getElement();
        return switch (key) {
            case "background_color" -> element.backgroundColor;
            case "bar_color" -> element.barColor;
            case "cardinal_tick_color" -> element.cardinalTickColor;
            case "degree_tick_color" -> element.degreeTickColor;
            case "minor_tick_color" -> element.minorTickColor;
            case "cardinal_text_color" -> element.cardinalTextColor;
            case "number_text_color" -> element.numberTextColor;
            case "needle_color" -> element.needleColor;
            case "death_pointer_color" -> element.deathPointerColor;
            case "hostile_dots_color" -> element.hostileDotsColor;
            case "passive_dots_color" -> element.passiveDotsColor;
            default -> null;
        };
    }

    private void setElementValue(CompassEditorElement consumes, String key, String value) {
        CompassElement element = consumes.getElement();
        switch (key) {
            case "background_color" -> element.backgroundColor = value;
            case "bar_color" -> element.barColor = value;
            case "cardinal_tick_color" -> element.cardinalTickColor = value;
            case "degree_tick_color" -> element.degreeTickColor = value;
            case "minor_tick_color" -> element.minorTickColor = value;
            case "cardinal_text_color" -> element.cardinalTextColor = value;
            case "number_text_color" -> element.numberTextColor = value;
            case "needle_color" -> element.needleColor = value;
            case "death_pointer_color" -> element.deathPointerColor = value;
            case "hostile_dots_color" -> element.hostileDotsColor = value;
            case "passive_dots_color" -> element.passiveDotsColor = value;
            default -> {
            }
        }
    }

    public CompassElement getElement() {
        return (CompassElement) this.element;
    }

    public @NotNull List<MarkerData> getMarkers() {
        return this.getElement().getMarkers();
    }

    public boolean addMarker(@NotNull MarkerData marker) {
        return this.getElement().addMarker(marker);
    }

    public boolean editMarker(@NotNull String markerName, @NotNull Consumer<MarkerData> editor) {
        return this.getElement().editMarker(markerName, editor);
    }

    public boolean removeMarker(@NotNull String markerName) {
        return this.getElement().removeMarker(markerName);
    }

    public void clearMarkers() {
        this.getElement().clearMarkers();
    }

    public @NotNull String getMarkerGroupKey() {
        return this.getElement().getMarkerGroupKey();
    }

}
