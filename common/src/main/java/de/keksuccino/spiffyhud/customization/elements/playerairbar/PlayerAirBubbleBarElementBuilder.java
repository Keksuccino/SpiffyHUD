package de.keksuccino.spiffyhud.customization.elements.playerairbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.resource.ResourceSupplier;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public class PlayerAirBubbleBarElementBuilder extends ElementBuilder<PlayerAirBubbleBarElement, PlayerAirBubbleBarEditorElement> {

    public PlayerAirBubbleBarElementBuilder() {
        super("spiffy_player_air_bubble_bar");
    }

    @Override
    public @NotNull PlayerAirBubbleBarElement buildDefaultInstance() {
        PlayerAirBubbleBarElement element = new PlayerAirBubbleBarElement(this);
        element.baseWidth = 100;
        element.baseHeight = 100;
        return element;
    }

    @Override
    public PlayerAirBubbleBarElement deserializeElement(@NotNull SerializedElement serialized) {
        PlayerAirBubbleBarElement element = this.buildDefaultInstance();

        element.bubblesPerRow = Math.max(1, deserializeNumber(Integer.class, element.bubblesPerRow, serialized.getValue("bubbles_per_row")));
        element.bubbleGap = Math.max(0, deserializeNumber(Integer.class, element.bubbleGap, serialized.getValue("bubble_gap")));
        element.scaleMultiplier = Objects.requireNonNullElse(serialized.getValue("scale_multiplier"), element.scaleMultiplier);
        element.blinkOnLoss = deserializeBoolean(element.blinkOnLoss, serialized.getValue("blink_on_loss"));
        element.lowAirShakeEnabled = deserializeBoolean(element.lowAirShakeEnabled, serialized.getValue("shake_enabled"));
        element.lowAirShakeThresholdBubbles = Math.max(0, deserializeNumber(Integer.class, element.lowAirShakeThresholdBubbles, serialized.getValue("shake_threshold_bubbles")));
        element.poppingDurationMs = Math.max(0, deserializeNumber(Integer.class, element.poppingDurationMs, serialized.getValue("pop_duration_ms")));

        String alignment = serialized.getValue("spiffy_alignment");
        if (alignment != null) {
            SpiffyAlignment parsed = SpiffyAlignment.getByName(alignment);
            if (parsed != null) {
                element.spiffyAlignment = parsed;
            }
        }

        for (PlayerAirBubbleBarElement.AirTextureKind kind : PlayerAirBubbleBarElement.AirTextureKind.values()) {
            String key = texturePropertyKey(kind);
            ResourceSupplier<ITexture> supplier = deserializeImageResourceSupplier(serialized.getValue(key));
            element.setCustomTexture(kind, supplier);
        }

        return element;
    }

    @Override
    public @Nullable PlayerAirBubbleBarElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        PlayerAirBubbleBarElement element = super.deserializeElementInternal(serialized);
        if (element != null) {
            element.stayOnScreen = this.deserializeBoolean(element.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return element;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull PlayerAirBubbleBarElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("bubbles_per_row", "" + element.bubblesPerRow);
        serializeTo.putProperty("bubble_gap", "" + element.bubbleGap);
        serializeTo.putProperty("scale_multiplier", element.scaleMultiplier);
        serializeTo.putProperty("blink_on_loss", "" + element.blinkOnLoss);
        serializeTo.putProperty("shake_enabled", "" + element.lowAirShakeEnabled);
        serializeTo.putProperty("shake_threshold_bubbles", "" + element.lowAirShakeThresholdBubbles);
        serializeTo.putProperty("pop_duration_ms", "" + element.poppingDurationMs);
        serializeTo.putProperty("spiffy_alignment", element.spiffyAlignment.getName());

        for (PlayerAirBubbleBarElement.AirTextureKind kind : PlayerAirBubbleBarElement.AirTextureKind.values()) {
            ResourceSupplier<ITexture> supplier = element.getCustomTexture(kind);
            if (supplier != null) {
                serializeTo.putProperty(texturePropertyKey(kind), supplier.getSourceWithPrefix());
            }
        }

        return serializeTo;
    }

    private static String texturePropertyKey(@NotNull PlayerAirBubbleBarElement.AirTextureKind kind) {
        return "texture_" + kind.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public @NotNull PlayerAirBubbleBarEditorElement wrapIntoEditorElement(@NotNull PlayerAirBubbleBarElement element, @NotNull LayoutEditorScreen editor) {
        return new PlayerAirBubbleBarEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.player_air_bubble_bar");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_air_bubble_bar.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }
}
