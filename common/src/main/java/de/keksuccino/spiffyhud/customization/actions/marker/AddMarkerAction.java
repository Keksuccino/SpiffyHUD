package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.customization.action.Action;
import de.keksuccino.fancymenu.customization.action.ActionInstance;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.marker.MarkerData;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddMarkerAction extends Action {

    private static final Logger LOGGER = LogManager.getLogger();

    public AddMarkerAction() {
        super("spiffyhud_add_marker");
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public void execute(@Nullable String value) {
        MarkerActionConfig config = MarkerActionConfig.parse(value);
        if (config == null) {
            LOGGER.error("[SPIFFYHUD] AddMarkerAction received malformed configuration.");
            return;
        }
        if (!config.hasValidTarget() || !config.hasDisplayName()) {
            LOGGER.warn("[SPIFFYHUD] AddMarkerAction requires a target element identifier and display name.");
            return;
        }
        MarkerData marker = config.toMarkerData();
        boolean success = MarkerStorage.addMarker(config.targetElementIdentifier, marker);
        if (!success) {
            LOGGER.warn("[SPIFFYHUD] Failed to add marker '{}' to group '{}'.", marker.getName(), config.targetElementIdentifier);
        }
    }

    @Override
    public @NotNull Component getActionDisplayName() {
        return Component.translatable("spiffyhud.actions.add_marker");
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.actions.add_marker.desc");
    }

    @Override
    public Component getValueDisplayName() {
        return Component.empty();
    }

    @Override
    public String getValueExample() {
        return MarkerActionConfig.defaultConfig().serialize();
    }

    @Override
    public void editValue(@NotNull Screen parentScreen, @NotNull ActionInstance instance) {
        MarkerActionConfig config = MarkerActionConfig.parse(instance.value);
        if (config == null) {
            config = MarkerActionConfig.defaultConfig();
        }
        MarkerEditorScreen screen = new MarkerEditorScreen(
                Component.translatable("spiffyhud.actions.add_marker.editor"),
                config,
                false,
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
