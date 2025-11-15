package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.cycle.CommonCycles;
import de.keksuccino.fancymenu.util.input.CharacterFilter;
import de.keksuccino.fancymenu.util.rendering.ui.screen.CellScreen;
import de.keksuccino.fancymenu.util.rendering.ui.screen.resource.ResourceChooserScreen;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.CycleButton;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.ExtendedButton;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.fancymenu.util.file.type.types.ImageFileType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class MarkerEditorScreen extends CellScreen {

    private final MarkerActionConfig config;
    private final Consumer<String> callback;
    private final boolean includeLookupField;

    private TextInputCell textureCell;

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
        TextInputCell targetElementCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.targetElementIdentifier = s.trim())
                .setText(this.config.targetElementIdentifier);
        targetElementCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.target_element.desc")));

        this.addCellGroupEndSpacerCell();

        if (this.includeLookupField) {
            this.addLabelCell(Component.translatable("spiffyhud.actions.marker.lookup_name"));
            TextInputCell lookupCell = this.addTextInputCell(null, true, true)
                    .setEditListener(s -> this.config.lookupMarkerName = s.trim())
                    .setText(this.config.getLookupName());
            lookupCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.lookup_name.desc")));
            this.addCellGroupEndSpacerCell();
        }

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.display_name"));
        TextInputCell displayNameCell = this.addTextInputCell(null, true, true)
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

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.texture"));
        this.textureCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.texture = s.trim())
                .setText(this.config.texture);
        if (this.textureCell != null) {
            this.textureCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.texture.desc")));
        }
        ExtendedButton pickButton = new ExtendedButton(0, 0, 20, 20, Component.translatable("spiffyhud.actions.marker.texture.pick"), button -> this.openTexturePicker());
        pickButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.texture.pick.desc")));
        this.addWidgetCell(pickButton, true);
        ExtendedButton clearButton = new ExtendedButton(0, 0, 20, 20, Component.translatable("spiffyhud.actions.marker.texture.clear"), button -> {
            this.config.texture = "";
            if (this.textureCell != null) {
                this.textureCell.setText("");
            }
        });
        clearButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.texture.clear.desc")));
        this.addWidgetCell(clearButton, true);

        this.addCellGroupEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.show_as_needle"));
        CycleButton<CommonCycles.CycleEnabledDisabled> showNeedleButton = new CycleButton<>(0, 0, 80, 20,
                CommonCycles.cycleEnabledDisabled("spiffyhud.actions.marker.show_as_needle.cycle", this.config.showAsNeedle),
                (value, button) -> this.config.showAsNeedle = value.getAsBoolean());
        showNeedleButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.show_as_needle.desc")));
        this.addWidgetCell(showNeedleButton, true);

        this.addCellGroupEndSpacerCell();

        CharacterFilter decimalFilter = CharacterFilter.buildDecimalFiler();
        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.position_x"));
        TextInputCell positionXCell = this.addTextInputCell(decimalFilter, true, true)
                .setEditListener(s -> this.config.positionX = parseDouble(s, this.config.positionX))
                .setText(String.valueOf(this.config.positionX));
        positionXCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.position_x.desc")));

        this.addCellGroupEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.position_z"));
        TextInputCell positionZCell = this.addTextInputCell(decimalFilter, true, true)
                .setEditListener(s -> this.config.positionZ = parseDouble(s, this.config.positionZ))
                .setText(String.valueOf(this.config.positionZ));
        positionZCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.position_z.desc")));

        this.addStartEndSpacerCell();
    }

    @Override
    protected void onCancel() {
        this.callback.accept(null);
    }

    @Override
    protected void onDone() {
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

    private void openTexturePicker() {
        ResourceChooserScreen<ITexture, ImageFileType> chooser = ResourceChooserScreen.image(null, source -> {
            if (source != null) {
                this.config.texture = source;
                if (this.textureCell != null) {
                    this.textureCell.setText(source);
                }
            }
            Minecraft.getInstance().setScreen(this);
        });
        chooser.setSource(this.config.texture.isBlank() ? null : this.config.texture, false);
        Minecraft.getInstance().setScreen(chooser);
    }

    private static double parseDouble(@Nullable String raw, double fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Mth.clamp(Double.parseDouble(raw.trim()), -30000000.0D, 30000000.0D);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
