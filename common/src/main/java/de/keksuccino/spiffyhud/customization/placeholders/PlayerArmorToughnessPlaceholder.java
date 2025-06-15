package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class PlayerArmorToughnessPlaceholder extends Placeholder {

    public PlayerArmorToughnessPlaceholder() {
        super("player_armor_toughness");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        if (Minecraft.getInstance().player != null) {
            float armorToughness = (float) Minecraft.getInstance().player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
            
            // Return the value, removing decimal if it's a whole number
            if (armorToughness % 1 == 0) {
                return String.valueOf((int) armorToughness);
            } else {
                return String.valueOf(armorToughness);
            }
        }
        return "0";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return null;
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.player_armor_toughness");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.player_armor_toughness.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        return new DeserializedPlaceholderString(this.getIdentifier(), null, "");
    }

}
