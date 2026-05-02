package de.keksuccino.spiffyhud.customization.elements.playernbthelper;

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

public class PlayerNbtHelperElementBuilder extends ElementBuilder<PlayerNbtHelperElement, PlayerNbtHelperEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public PlayerNbtHelperElementBuilder() {
        super("spiffy_player_nbt_helper");
    }

    @Override
    public @NotNull PlayerNbtHelperElement buildDefaultInstance() {
        PlayerNbtHelperElement i = new PlayerNbtHelperElement(this);
        i.baseWidth = 100;
        i.baseHeight = 100;
        i.inEditorColor.setDefault(DrawableColor.of(new Color(19, 59, 157)).getHex()).set(DrawableColor.of(new Color(19, 59, 157)).getHex());
        return i;
    }

    @Override
    public PlayerNbtHelperElement deserializeElement(@NotNull SerializedElement serialized) {

        PlayerNbtHelperElement element = this.buildDefaultInstance();

        return element;

    }

    @Override
    protected SerializedElement serializeElement(@NotNull PlayerNbtHelperElement element, @NotNull SerializedElement serializeTo) {

        return serializeTo;

    }

    @Override
    public @NotNull PlayerNbtHelperEditorElement wrapIntoEditorElement(@NotNull PlayerNbtHelperElement element, @NotNull LayoutEditorScreen editor) {
        return new PlayerNbtHelperEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.player_nbt_helper");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_nbt_helper.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
