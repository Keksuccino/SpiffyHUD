package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.customization.action.Action;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClearMarkersAction extends Action {

    private static final Logger LOGGER = LogManager.getLogger();

    public ClearMarkersAction() {
        super("spiffyhud_clear_markers");
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public void execute(@Nullable String value) {
        if (value == null) {
            LOGGER.error("[SPIFFYHUD] ClearMarkersAction requires a target element identifier.");
            return;
        }
        String target = value.trim();
        if (target.isEmpty()) {
            LOGGER.error("[SPIFFYHUD] ClearMarkersAction received a blank target element identifier.");
            return;
        }
        try {
            MarkerStorage.clearGroup(target);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("[SPIFFYHUD] ClearMarkersAction failed for identifier '{}'.", target, ex);
        }
    }

    @Override
    public @NotNull Component getActionDisplayName() {
        return Component.translatable("spiffyhud.actions.clear_markers");
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.actions.clear_markers.desc");
    }

    @Override
    public Component getValueDisplayName() {
        return Component.translatable("spiffyhud.actions.marker.target_element");
    }

    @Override
    public String getValueExample() {
        return "example.element.identifier";
    }
}
