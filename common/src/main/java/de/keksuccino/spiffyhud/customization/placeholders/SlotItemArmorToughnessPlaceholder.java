package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;

public class SlotItemArmorToughnessPlaceholder extends Placeholder {

    public SlotItemArmorToughnessPlaceholder() {
        super("slot_item_armor_toughness");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        String slot = dps.values.get("slot");
        
        if ((slot != null) && MathUtils.isInteger(slot) && (Minecraft.getInstance().player != null)) {
            int slotInt = Integer.parseInt(slot);
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(slotInt);
            
            if (!stack.isEmpty()) {
                double armorToughness = 0.0;
                
                // Get armor toughness from attribute modifiers
                stack
                var modifiers = stack.getAttributeModifiers();
                if (modifiers.containsKey(Attributes.ARMOR_TOUGHNESS)) {
                    for (AttributeModifier modifier : modifiers.get(Attributes.ARMOR_TOUGHNESS)) {
                        armorToughness += modifier.amount();
                    }
                }
                
                // Return the value, removing decimal if it's a whole number
                if (armorToughness % 1 == 0) {
                    return String.valueOf((int) armorToughness);
                } else {
                    return String.valueOf(armorToughness);
                }
            }
        }
        return "0";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of("slot");
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.slot_item_armor_toughness");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.slot_item_armor_toughness.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("slot", "slot_number");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
