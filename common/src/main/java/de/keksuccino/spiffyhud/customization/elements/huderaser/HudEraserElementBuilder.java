package de.keksuccino.spiffyhud.customization.elements.huderaser;

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

public class HudEraserElementBuilder extends ElementBuilder<HudEraserElement, HudEraserEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public HudEraserElementBuilder() {
        super("spiffy_hud_eraser");
    }

    @Override
    public @NotNull HudEraserElement buildDefaultInstance() {
        HudEraserElement i = new HudEraserElement(this);
        i.baseWidth = 100;
        i.baseHeight = 100;
        i.inEditorColor = DrawableColor.of(new Color(161, 79, 255, 100));
        return i;
    }

    @Override
    public HudEraserElement deserializeElement(@NotNull SerializedElement serialized) {

        return this.buildDefaultInstance();

    }

    @Override
    protected SerializedElement serializeElement(@NotNull HudEraserElement element, @NotNull SerializedElement serializeTo) {

        return serializeTo;

    }

    @Override
    public @NotNull HudEraserEditorElement wrapIntoEditorElement(@NotNull HudEraserElement element, @NotNull LayoutEditorScreen editor) {
        return new HudEraserEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.hud_eraser");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.hud_eraser.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
