package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.Pair;
import de.keksuccino.fancymenu.util.SerializationUtils;
import de.keksuccino.spiffyhud.util.level.EntityNbtUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerNbtDataPlaceholder extends Placeholder {

    private static final Map<String, Pair<String, Long>> CACHE = new HashMap<>();

    public PlayerNbtDataPlaceholder() {
        super("player_nbt_data");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        long now = System.currentTimeMillis();
        LocalPlayer e = Minecraft.getInstance().player;
        if (e != null) {
            String path = dps.values.get("path");
            long cooldownMs = SerializationUtils.deserializeNumber(Long.class, 0L, dps.values.get("refresh_cooldown_ms"));
            if (path != null) {
                String identifier = path + ":" + cooldownMs;
                if ((cooldownMs > 0) && CACHE.containsKey(identifier) && ((CACHE.get(identifier).getValue() + cooldownMs) >= now)) {
                    return CACHE.get(identifier).getKey();
                } else {
                    String value = EntityNbtUtils.getNbtString(e, path);
                    if (value == null) return "§cINVALID NBT PATH!";
                    if (cooldownMs > 0) {
                        CACHE.put(identifier, Pair.of(value, now));
                    }
                    return value;
                }
            }
        } else {
            return "§cNOT IN A WORLD!";
        }
        return "§cERROR!";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of("path", "refresh_cooldown_ms");
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
        values.put("refresh_cooldown_ms", "100");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
