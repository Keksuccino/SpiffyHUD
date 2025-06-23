package de.keksuccino.spiffyhud.customization.elements.vanillalike.contextualbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaLikeContextualBarElementBuilder extends ElementBuilder<VanillaLikeContextualBarElement, VanillaLikeContextualBarEditorElement> {

    public VanillaLikeContextualBarElementBuilder() {
        super("spiffy_vanillalike_player_exp_bar");
    }

    @Override
    public @NotNull VanillaLikeContextualBarElement buildDefaultInstance() {
        VanillaLikeContextualBarElement e = new VanillaLikeContextualBarElement(this);
        e.stickyAnchor = true;
        e.stayOnScreen = false;
        return e;
    }

    @Override
    public VanillaLikeContextualBarElement deserializeElement(@NotNull SerializedElement serialized) {

        VanillaLikeContextualBarElement element = this.buildDefaultInstance();
        
        element.alwaysShowLocatorBar = this.deserializeBoolean(element.alwaysShowLocatorBar, serialized.getValue("always_show_locator_bar"));
        element.alwaysShowExperienceBar = this.deserializeBoolean(element.alwaysShowExperienceBar, serialized.getValue("always_show_experience_bar"));

        return element;

    }

    @Override
    public @Nullable VanillaLikeContextualBarElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        VanillaLikeContextualBarElement e = super.deserializeElementInternal(serialized);
        if (e != null) {
            // Fix "Stay on Screen" resetting itself for element types that have it disabled by default
            e.stayOnScreen = this.deserializeBoolean(e.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return e;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull VanillaLikeContextualBarElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("always_show_locator_bar", String.valueOf(element.alwaysShowLocatorBar));
        serializeTo.putProperty("always_show_experience_bar", String.valueOf(element.alwaysShowExperienceBar));

        return serializeTo;
        
    }

    @Override
    public @NotNull VanillaLikeContextualBarEditorElement wrapIntoEditorElement(@NotNull VanillaLikeContextualBarElement element, @NotNull LayoutEditorScreen editor) {
        return new VanillaLikeContextualBarEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.vanillalike.player_experience");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return null;
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
