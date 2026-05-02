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
        if (!config.hasValidTarget() || !config.hasValidMarkerName()) {
            LOGGER.error("[SPIFFYHUD] AddMarkerAction requires a target element identifier and display name.");
            return;
        }
        boolean success = MarkerStorage.addMarker(config.targetElementIdentifier, config.toMarkerData());
        if (!success) {
            LOGGER.error("[SPIFFYHUD] Failed to add marker '{}' to group '{}'.", config.uniqueMarkerName, config.targetElementIdentifier);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("spiffyhud.actions.add_marker");
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("spiffyhud.actions.add_marker.desc");
    }

    @Override
    public Component getValueDisplayName() {
        return Component.empty();
    }

    @Override
    public String getValuePreset() {
        return MarkerActionConfig.defaultConfig().serialize();
    }

    @Override
    public void editValue(@NotNull ActionInstance instance, @NotNull ActionEditingCompletedFeedback onEditingCompleted, @NotNull ActionEditingCanceledFeedback onEditingCanceled) {
        String oldValue = instance.value;
        boolean[] handled = {false};
        MarkerActionConfig config = MarkerActionConfig.parse(instance.value);
        if (config == null) {
            config = MarkerActionConfig.defaultConfig();
        }
        final PiPWindow[] windowHolder = new PiPWindow[1];
        MarkerEditorScreen screen = new MarkerEditorScreen(
                Component.translatable("spiffyhud.actions.add_marker.editor"),
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
