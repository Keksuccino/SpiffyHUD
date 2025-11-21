package de.keksuccino.spiffyhud.customization.elements.playerarmorbar;

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

public class PlayerArmorBarElementBuilder extends ElementBuilder<PlayerArmorBarElement, PlayerArmorBarEditorElement> {

    public PlayerArmorBarElementBuilder() {
        super("spiffy_player_armor_bar");
    }

    @Override
    public @NotNull PlayerArmorBarElement buildDefaultInstance() {
        PlayerArmorBarElement element = new PlayerArmorBarElement(this);
        element.baseWidth = 100;
        element.baseHeight = 100;
        return element;
    }

    @Override
    public PlayerArmorBarElement deserializeElement(@NotNull SerializedElement serialized) {
        PlayerArmorBarElement element = this.buildDefaultInstance();

        element.iconsPerRow = Math.max(1, deserializeNumber(Integer.class, element.iconsPerRow, serialized.getValue("icons_per_row")));
        element.iconGap = Math.max(0, deserializeNumber(Integer.class, element.iconGap, serialized.getValue("icon_gap")));
        element.scaleMultiplier = Objects.requireNonNullElse(serialized.getValue("scale_multiplier"), element.scaleMultiplier);
        element.blinkOnLoss = deserializeBoolean(element.blinkOnLoss, serialized.getValue("blink_on_loss"));
        element.lowArmorShakeEnabled = deserializeBoolean(element.lowArmorShakeEnabled, serialized.getValue("shake_enabled"));
        element.lowArmorShakeThresholdIcons = Math.max(0, deserializeNumber(Integer.class, element.lowArmorShakeThresholdIcons, serialized.getValue("shake_threshold_icons")));

        String alignment = serialized.getValue("spiffy_alignment");
        if (alignment != null) {
            SpiffyAlignment parsed = SpiffyAlignment.getByName(alignment);
            if (parsed != null) {
                element.spiffyAlignment = parsed;
            }
        }

        for (PlayerArmorBarElement.ArmorTextureKind kind : PlayerArmorBarElement.ArmorTextureKind.values()) {
            String key = texturePropertyKey(kind);
            ResourceSupplier<ITexture> supplier = deserializeImageResourceSupplier(serialized.getValue(key));
            element.setCustomTexture(kind, supplier);
        }

        return element;
    }

    @Override
    public @Nullable PlayerArmorBarElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        PlayerArmorBarElement element = super.deserializeElementInternal(serialized);
        if (element != null) {
            element.stayOnScreen = this.deserializeBoolean(element.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return element;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull PlayerArmorBarElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("icons_per_row", "" + element.iconsPerRow);
        serializeTo.putProperty("icon_gap", "" + element.iconGap);
        serializeTo.putProperty("scale_multiplier", element.scaleMultiplier);
        serializeTo.putProperty("blink_on_loss", "" + element.blinkOnLoss);
        serializeTo.putProperty("shake_enabled", "" + element.lowArmorShakeEnabled);
        serializeTo.putProperty("shake_threshold_icons", "" + element.lowArmorShakeThresholdIcons);
        serializeTo.putProperty("spiffy_alignment", element.spiffyAlignment.getName());

        for (PlayerArmorBarElement.ArmorTextureKind kind : PlayerArmorBarElement.ArmorTextureKind.values()) {
            ResourceSupplier<ITexture> supplier = element.getCustomTexture(kind);
            if (supplier != null) {
                serializeTo.putProperty(texturePropertyKey(kind), supplier.getSourceWithPrefix());
            }
        }

        return serializeTo;
    }

    private static String texturePropertyKey(@NotNull PlayerArmorBarElement.ArmorTextureKind kind) {
        return "texture_" + kind.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public @NotNull PlayerArmorBarEditorElement wrapIntoEditorElement(@NotNull PlayerArmorBarElement element, @NotNull LayoutEditorScreen editor) {
        return new PlayerArmorBarEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.player_armor_bar");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_armor_bar.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }
}
