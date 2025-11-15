package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.cycle.CommonCycles;
import de.keksuccino.fancymenu.util.rendering.ui.screen.CellScreen;
import de.keksuccino.fancymenu.util.rendering.ui.screen.resource.ResourceChooserScreen;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.CycleButton;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.ExtendedButton;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.fancymenu.util.file.type.types.ImageFileType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class MarkerEditorScreen extends CellScreen {

    private final MarkerActionConfig config;
    private final Consumer<String> callback;
    private final boolean includeLookupField;

    private TextInputCell dotTextureCell;
    private TextInputCell needleTextureCell;

    public MarkerEditorScreen(@NotNull Component title, @NotNull MarkerActionConfig config, boolean includeLookupField, @NotNull Consumer<String> callback) {
        super(title);
        this.config = Objects.requireNonNull(config);
        this.includeLookupField = includeLookupField;
        this.callback = Objects.requireNonNull(callback);
    }

    @Override
    protected void initCells() {

        this.addStartEndSpacerCell();

        // Target element identifier (group)
        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.target_element"));
        TextInputCell targetElementCell = this.addTextInputCell(null, false, true)
                .setEditListener(s -> this.config.targetElementIdentifier = s.trim())
                .setText(this.config.targetElementIdentifier);
        targetElementCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.target_element.desc")));

        this.addCellGroupEndSpacerCell();

        if (this.includeLookupField) {
            this.addLabelCell(Component.translatable("spiffyhud.actions.marker.lookup_name"));
            TextInputCell lookupCell = this.addTextInputCell(null, false, true)
                    .setEditListener(s -> this.config.lookupMarkerName = s.trim())
                    .setText(this.config.getLookupName());
            lookupCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.lookup_name.desc")));
            this.addCellGroupEndSpacerCell();
        }

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.display_name"));
        TextInputCell displayNameCell = this.addTextInputCell(null, false, true)
                .setEditListener(s -> this.config.displayName = s.trim())
                .setText(this.config.displayName);
        displayNameCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.display_name.desc")));

        this.addCellGroupEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.color"));
        TextInputCell colorCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.colorHex = s.trim())
                .setText(this.config.colorHex);
        colorCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.color.desc")));

        this.addCellGroupEndSpacerCell();

        this.addTextureRow(TextureField.DOT);
        this.addTextureRow(TextureField.NEEDLE);

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.show_as_needle"));
        CycleButton<CommonCycles.CycleEnabledDisabled> showNeedleButton = new CycleButton<>(0, 0, 80, 20,
                CommonCycles.cycleEnabledDisabled("spiffyhud.actions.marker.show_as_needle.cycle", this.config.showAsNeedle),
                (value, button) -> this.config.showAsNeedle = value.getAsBoolean());
        showNeedleButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.show_as_needle.desc")));
        this.addWidgetCell(showNeedleButton, true);

        this.addCellGroupEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.position_x"));
        TextInputCell positionXCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.positionX = s.trim())
                .setText(this.config.positionX);
        positionXCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.position_x.desc")));

        this.addCellGroupEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.position_z"));
        TextInputCell positionZCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.positionZ = s.trim())
                .setText(this.config.positionZ);
        positionZCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.position_z.desc")));

        this.addStartEndSpacerCell();
    }

    @Override
    protected void onCancel() {
        this.callback.accept(null);
    }

    @Override
    protected void onDone() {
        this.config.displayName = PlaceholderParser.replacePlaceholders(this.config.displayName);
        this.config.normalize();
        this.callback.accept(this.config.serialize());
    }

    @Override
    public boolean allowDone() {
        if (!this.config.hasValidTarget() || !this.config.hasDisplayName()) {
            return false;
        }
        if (this.includeLookupField && this.config.getLookupName().isBlank()) {
            return false;
        }
        return true;
    }

    private void addTextureRow(@NotNull TextureField field) {
        boolean needle = field == TextureField.NEEDLE;
        String baseKey = needle ? "spiffyhud.actions.marker.needle_texture" : "spiffyhud.actions.marker.dot_texture";
        this.addLabelCell(Component.translatable(baseKey));
        TextInputCell cell = this.addTextInputCell(null, true, true);
        if (needle) {
            this.needleTextureCell = cell;
            cell.setEditListener(s -> this.config.needleTexture = s.trim());
            cell.setText(this.config.needleTexture == null ? "" : this.config.needleTexture);
        } else {
            this.dotTextureCell = cell;
            cell.setEditListener(s -> this.config.dotTexture = s.trim());
            cell.setText(this.config.dotTexture == null ? "" : this.config.dotTexture);
        }
        cell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines(baseKey + ".desc")));

        ExtendedButton pickButton = new ExtendedButton(0, 0, 20, 20, Component.translatable(baseKey + ".pick"), button -> this.openTexturePicker(field));
        pickButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines(baseKey + ".pick.desc")));
        this.addWidgetCell(pickButton, true);

        ExtendedButton clearButton = new ExtendedButton(0, 0, 20, 20, Component.translatable(baseKey + ".clear"), button -> this.clearTexture(field));
        clearButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines(baseKey + ".clear.desc")));
        this.addWidgetCell(clearButton, true);

        this.addCellGroupEndSpacerCell();
    }

    private void clearTexture(@NotNull TextureField field) {
        if (field == TextureField.NEEDLE) {
            this.config.needleTexture = "";
            if (this.needleTextureCell != null) {
                this.needleTextureCell.setText("");
            }
        } else {
            this.config.dotTexture = "";
            if (this.dotTextureCell != null) {
                this.dotTextureCell.setText("");
            }
        }
    }

    private void openTexturePicker(@NotNull TextureField field) {
        String stored = (field == TextureField.NEEDLE) ? this.config.needleTexture : this.config.dotTexture;
        String source = stored == null ? "" : stored;
        ResourceChooserScreen<ITexture, ImageFileType> chooser = ResourceChooserScreen.image(null, selection -> {
            if (selection != null) {
                if (field == TextureField.NEEDLE) {
                    this.config.needleTexture = selection;
                    if (this.needleTextureCell != null) {
                        this.needleTextureCell.setText(selection);
                    }
                } else {
                    this.config.dotTexture = selection;
                    if (this.dotTextureCell != null) {
                        this.dotTextureCell.setText(selection);
                    }
                }
            }
            Minecraft.getInstance().setScreen(this);
        });
        chooser.setSource(source.isBlank() ? null : source, false);
        Minecraft.getInstance().setScreen(chooser);
    }

    private enum TextureField {
        DOT,
        NEEDLE
    }

}
