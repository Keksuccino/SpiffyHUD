package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.util.level.EntityNbtUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;

public class PlayerNbtDataPlaceholder extends Placeholder {

    public PlayerNbtDataPlaceholder() {
        super("player_nbt_data");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        LocalPlayer e = Minecraft.getInstance().player;
        if (e != null) {
            String path = dps.values.get("path");
            if (path != null) {
                String value = EntityNbtUtils.getNbtString(e, path);
                if (value == null) return "§cINVALID NBT PATH!";
                return value;
            }
        } else {
            return "§cNOT IN A WORLD!";
        }
        return "§cERROR!";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of("path");
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.player_nbt_data");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.player_nbt_data.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("path", "path_to_nbt");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
