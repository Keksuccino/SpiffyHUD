package de.keksuccino.spiffyhud.customization.elements.player;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.air.VanillaLikePlayerAirElement;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerElementBuilder extends ElementBuilder<PlayerElement, PlayerEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public PlayerElementBuilder() {
        super("spiffy_player");
    }

    @Override
    public @NotNull PlayerElement buildDefaultInstance() {
        PlayerElement i = new PlayerElement(this);
        i.baseWidth = 80;
        i.baseHeight = 300;
        i.stayOnScreen = false;
        i.stickyAnchor = true;
        return i;
    }

    @Override
    public PlayerElement deserializeElement(@NotNull SerializedElement serialized) {

        PlayerElement element = this.buildDefaultInstance();

//        element.slot = Objects.requireNonNullElse(serialized.getValue("inventory_slot"), element.slot);
//        element.useSelectedSlot = SerializationHelper.INSTANCE.deserializeBoolean(element.useSelectedSlot, serialized.getValue("use_selected_slot"));

        return element;

    }

    @Override
    public @Nullable PlayerElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        PlayerElement e = super.deserializeElementInternal(serialized);
        if (e != null) {
            // Fix "Stay on Screen" resetting itself for element types that have it disabled by default
            e.stayOnScreen = this.deserializeBoolean(e.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return e;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull PlayerElement element, @NotNull SerializedElement serializeTo) {

//        serializeTo.putProperty("inventory_slot", element.slot);
//        serializeTo.putProperty("use_selected_slot", "" + element.useSelectedSlot);

        return serializeTo;

    }

    @Override
    public @NotNull PlayerEditorElement wrapIntoEditorElement(@NotNull PlayerElement element, @NotNull LayoutEditorScreen editor) {
        return new PlayerEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.player");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
