package de.keksuccino.spiffyhud.customization.elements.playermounthealthbar;

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

public class PlayerMountHealthBarElementBuilder extends ElementBuilder<PlayerMountHealthBarElement, PlayerMountHealthBarEditorElement> {

    public PlayerMountHealthBarElementBuilder() {
        super("spiffy_player_mount_health_bar");
    }

    @Override
    public @NotNull PlayerMountHealthBarElement buildDefaultInstance() {
        PlayerMountHealthBarElement element = new PlayerMountHealthBarElement(this);
        element.baseWidth = 100;
        element.baseHeight = 100;
        return element;
    }

    @Override
    public PlayerMountHealthBarElement deserializeElement(@NotNull SerializedElement serialized) {
        PlayerMountHealthBarElement element = this.buildDefaultInstance();

        element.heartsPerRow = Math.max(1, deserializeNumber(Integer.class, element.heartsPerRow, serialized.getValue("hearts_per_row")));
        element.heartGap = Math.max(0, deserializeNumber(Integer.class, element.heartGap, serialized.getValue("heart_gap")));
        element.scaleMultiplier = Objects.requireNonNullElse(serialized.getValue("scale_multiplier"), element.scaleMultiplier);
        element.blinkOnLoss = deserializeBoolean(element.blinkOnLoss, serialized.getValue("blink_on_loss"));
        element.lowHealthShakeEnabled = deserializeBoolean(element.lowHealthShakeEnabled, serialized.getValue("shake_enabled"));
        element.lowHealthShakeThresholdHearts = Math.max(0, deserializeNumber(Integer.class, element.lowHealthShakeThresholdHearts, serialized.getValue("shake_threshold_hearts")));

        String alignment = serialized.getValue("spiffy_alignment");
        if (alignment != null) {
            SpiffyAlignment parsed = SpiffyAlignment.getByName(alignment);
            if (parsed != null) {
                element.spiffyAlignment = parsed;
            }
        }

        for (PlayerMountHealthBarElement.HeartTextureKind kind : PlayerMountHealthBarElement.HeartTextureKind.values()) {
            String key = texturePropertyKey(kind);
            ResourceSupplier<ITexture> supplier = deserializeImageResourceSupplier(serialized.getValue(key));
            element.setCustomTexture(kind, supplier);
        }

        return element;
    }

    @Override
    public @Nullable PlayerMountHealthBarElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        PlayerMountHealthBarElement element = super.deserializeElementInternal(serialized);
        if (element != null) {
            element.stayOnScreen = this.deserializeBoolean(element.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return element;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull PlayerMountHealthBarElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("hearts_per_row", "" + element.heartsPerRow);
        serializeTo.putProperty("heart_gap", "" + element.heartGap);
        serializeTo.putProperty("scale_multiplier", element.scaleMultiplier);
        serializeTo.putProperty("blink_on_loss", "" + element.blinkOnLoss);
        serializeTo.putProperty("shake_enabled", "" + element.lowHealthShakeEnabled);
        serializeTo.putProperty("shake_threshold_hearts", "" + element.lowHealthShakeThresholdHearts);
        serializeTo.putProperty("spiffy_alignment", element.spiffyAlignment.getName());

        for (PlayerMountHealthBarElement.HeartTextureKind kind : PlayerMountHealthBarElement.HeartTextureKind.values()) {
            ResourceSupplier<ITexture> supplier = element.getCustomTexture(kind);
            if (supplier != null) {
                serializeTo.putProperty(texturePropertyKey(kind), supplier.getSourceWithPrefix());
            }
        }

        return serializeTo;
    }

    private static String texturePropertyKey(@NotNull PlayerMountHealthBarElement.HeartTextureKind kind) {
        return "texture_" + kind.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public @NotNull PlayerMountHealthBarEditorElement wrapIntoEditorElement(@NotNull PlayerMountHealthBarElement element, @NotNull LayoutEditorScreen editor) {
        return new PlayerMountHealthBarEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.player_mount_health_bar");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_mount_health_bar.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }
}
