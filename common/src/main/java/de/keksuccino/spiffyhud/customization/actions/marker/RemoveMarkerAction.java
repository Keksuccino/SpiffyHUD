package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.customization.action.Action;
import de.keksuccino.fancymenu.customization.action.ActionInstance;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoveMarkerAction extends Action {

    private static final Logger LOGGER = LogManager.getLogger();

    public RemoveMarkerAction() {
        super("spiffyhud_remove_marker");
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public void execute(@Nullable String value) {
        MarkerRemovalConfig config = MarkerRemovalConfig.parse(value);
        if (config == null) {
            LOGGER.error("[SPIFFYHUD] RemoveMarkerAction received malformed configuration.");
            return;
        }
        if (!config.isValid()) {
            LOGGER.error("[SPIFFYHUD] RemoveMarkerAction is missing required data.");
            return;
        }
        boolean success = MarkerStorage.removeMarker(config.targetElementIdentifier, config.markerName);
        if (!success) {
            LOGGER.error("[SPIFFYHUD] Failed to remove marker '{}' from group '{}'.", config.markerName, config.targetElementIdentifier);
        }
    }

    @Override
    public @NotNull Component getActionDisplayName() {
        return Component.translatable("spiffyhud.actions.remove_marker");
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.actions.remove_marker.desc");
    }

    @Override
    public Component getValueDisplayName() {
        return Component.empty();
    }

    @Override
    public String getValueExample() {
        return MarkerRemovalConfig.defaultConfig().serialize();
    }

    @Override
    public void editValue(@NotNull Screen parentScreen, @NotNull ActionInstance instance) {
        MarkerRemovalConfig config = MarkerRemovalConfig.parse(instance.value);
        if (config == null) {
            config = MarkerRemovalConfig.defaultConfig();
        }
        MarkerRemovalScreen screen = new MarkerRemovalScreen(
                Component.translatable("spiffyhud.actions.remove_marker.editor"),
                config,
                serialized -> {
                    if (serialized != null) {
                        instance.value = serialized;
                    }
                    Minecraft.getInstance().setScreen(parentScreen);
                }
        );
        Minecraft.getInstance().setScreen(screen);
    }

}
