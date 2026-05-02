package de.keksuccino.spiffyhud.customization.elements.overlayremover;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.Objects;

public class OverlayRemoverElementBuilder extends ElementBuilder<OverlayRemoverElement, OverlayRemoverEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public OverlayRemoverElementBuilder() {
        super("spiffy_overlay_remover");
    }

    @Override
    public @NotNull OverlayRemoverElement buildDefaultInstance() {
        OverlayRemoverElement i = new OverlayRemoverElement(this);
        i.baseWidth = 100;
        i.baseHeight = 100;
        i.inEditorColor.setDefault(DrawableColor.of(new Color(137, 168, 50)).getHex()).set(DrawableColor.of(new Color(137, 168, 50)).getHex());
        return i;
    }

    @Override
    public OverlayRemoverElement deserializeElement(@NotNull SerializedElement serialized) {

        OverlayRemoverElement element = this.buildDefaultInstance();

        element.overlayType = Objects.requireNonNullElse(
                OverlayRemoverElement.OverlayType.getByName(Objects.requireNonNullElse(serialized.getValue("overlay_type"), OverlayRemoverElement.OverlayType.VIGNETTE.getName())),
                element.overlayType);

        return element;

    }

    @Override
    protected SerializedElement serializeElement(@NotNull OverlayRemoverElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("overlay_type", element.overlayType.getName());

        return serializeTo;

    }

    @Override
    public @NotNull OverlayRemoverEditorElement wrapIntoEditorElement(@NotNull OverlayRemoverElement element, @NotNull LayoutEditorScreen editor) {
        return new OverlayRemoverEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.overlay_remover");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.overlay_remover.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
