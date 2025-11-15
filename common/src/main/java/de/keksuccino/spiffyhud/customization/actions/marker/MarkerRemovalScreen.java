package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.screen.CellScreen;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.function.Consumer;

public class MarkerRemovalScreen extends CellScreen {

    private final MarkerRemovalConfig config;
    private final Consumer<String> callback;

    public MarkerRemovalScreen(@NotNull Component title, @NotNull MarkerRemovalConfig config, @NotNull Consumer<String> callback) {
        super(title);
        this.config = Objects.requireNonNull(config);
        this.callback = Objects.requireNonNull(callback);
    }

    @Override
    protected void initCells() {
        this.addStartEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.target_element"));
        TextInputCell targetCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.targetElementIdentifier = s.trim())
                .setText(this.config.targetElementIdentifier);
        targetCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.target_element.desc")));

        this.addCellGroupEndSpacerCell();

        this.addLabelCell(Component.translatable("spiffyhud.actions.marker.remove_name"));
        TextInputCell markerCell = this.addTextInputCell(null, true, true)
                .setEditListener(s -> this.config.markerName = s.trim())
                .setText(this.config.markerName);
        markerCell.editBox.setTooltip(() -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.actions.marker.remove_name.desc")));

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
        return this.config.isValid();
    }
}
