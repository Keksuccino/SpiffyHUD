package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.customization.action.Action;
import de.keksuccino.fancymenu.customization.action.ActionInstance;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import de.keksuccino.konkrete.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditMarkerAction extends Action {

    private static final Logger LOGGER = LogManager.getLogger();

    public EditMarkerAction() {
        super("spiffyhud_edit_marker");
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public void execute(@Nullable String value) {
        MarkerActionConfig config = MarkerActionConfig.parse(value);
        if (config == null) {
            LOGGER.error("[SPIFFYHUD] EditMarkerAction received malformed configuration.");
            return;
        }
        String lookupName = config.getLookupName();
        if (!config.hasValidTarget() || lookupName.isBlank() || !config.hasDisplayName()) {
            LOGGER.warn("[SPIFFYHUD] EditMarkerAction is missing required data.");
            return;
        }
        if (!MathUtils.isDouble(config.positionX) || !MathUtils.isDouble(config.positionZ)) {
            LOGGER.warn("[SPIFFYHUD] EditMarkerAction requires numeric coordinates but received '{}', '{}'.", config.positionX, config.positionZ);
            return;
        }
        double parsedX = Double.parseDouble(config.positionX.trim());
        double parsedZ = Double.parseDouble(config.positionZ.trim());
        boolean success = MarkerStorage.editMarker(config.targetElementIdentifier, lookupName, marker -> {
            marker.setName(config.displayName);
            marker.setColor(config.colorHex);
            marker.setTexture(config.texture);
            marker.setShowAsNeedle(config.showAsNeedle);
            marker.setMarkerPosX(parsedX);
            marker.setMarkerPosZ(parsedZ);
        });
        if (!success) {
            LOGGER.warn("[SPIFFYHUD] Failed to edit marker '{}' in group '{}'.", lookupName, config.targetElementIdentifier);
        }
    }

    @Override
    public @NotNull Component getActionDisplayName() {
        return Component.translatable("spiffyhud.actions.edit_marker");
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.actions.edit_marker.desc");
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
                Component.translatable("spiffyhud.actions.edit_marker.editor"),
                config,
                true,
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
