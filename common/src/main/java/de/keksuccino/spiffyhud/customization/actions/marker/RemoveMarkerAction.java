package de.keksuccino.spiffyhud.customization.actions.marker;

import de.keksuccino.fancymenu.customization.action.Action;
import de.keksuccino.fancymenu.customization.action.ActionInstance;
import de.keksuccino.fancymenu.util.rendering.ui.pipwindow.PiPWindow;
import de.keksuccino.fancymenu.util.rendering.ui.pipwindow.PiPWindowHandler;
import de.keksuccino.fancymenu.util.rendering.ui.screen.texteditor.TextEditorWindowBody;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
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
        boolean success = MarkerStorage.removeMarker(config.targetElementIdentifier, config.uniqueMarkerName);
        if (!success) {
            LOGGER.error("[SPIFFYHUD] Failed to remove marker '{}' from group '{}'.", config.uniqueMarkerName, config.targetElementIdentifier);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("spiffyhud.actions.remove_marker");
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("spiffyhud.actions.remove_marker.desc");
    }

    @Override
    public Component getValueDisplayName() {
        return Component.empty();
    }

    @Override
    public String getValuePreset() {
        return MarkerRemovalConfig.defaultConfig().serialize();
    }

    @Override
    public void editValue(@NotNull ActionInstance instance, @NotNull ActionEditingCompletedFeedback onEditingCompleted, @NotNull ActionEditingCanceledFeedback onEditingCanceled) {
        String oldValue = instance.value;
        boolean[] handled = {false};
        MarkerRemovalConfig config = MarkerRemovalConfig.parse(instance.value);
        if (config == null) {
            config = MarkerRemovalConfig.defaultConfig();
        }
        final PiPWindow[] windowHolder = new PiPWindow[1];
        MarkerRemovalScreen screen = new MarkerRemovalScreen(
                Component.translatable("spiffyhud.actions.remove_marker.editor"),
                config,
                serialized -> {
                    if (handled[0]) {
                        return;
                    }
                    handled[0] = true;
                    if (serialized != null) {
                        instance.value = serialized;
                        onEditingCompleted.accept(instance, oldValue, serialized);
                    } else {
                        onEditingCanceled.accept(instance);
                    }
                    PiPWindow window = windowHolder[0];
                    if (window != null) {
                        window.close();
                    }
                }
        );
        PiPWindow window = new PiPWindow(screen.getTitle())
                .setScreen(screen)
                .setForceFancyMenuUiScale(true)
                .setAlwaysOnTop(true)
                .setBlockMinecraftScreenInputs(true)
                .setForceFocus(true)
                .setMinSize(TextEditorWindowBody.PIP_WINDOW_WIDTH, TextEditorWindowBody.PIP_WINDOW_HEIGHT)
                .setSize(TextEditorWindowBody.PIP_WINDOW_WIDTH, TextEditorWindowBody.PIP_WINDOW_HEIGHT);
        windowHolder[0] = window;
        PiPWindowHandler.INSTANCE.openWindowCentered(window, null);
        window.addCloseCallback(() -> {
            if (handled[0]) {
                return;
            }
            handled[0] = true;
            onEditingCanceled.accept(instance);
        });
    }

}
