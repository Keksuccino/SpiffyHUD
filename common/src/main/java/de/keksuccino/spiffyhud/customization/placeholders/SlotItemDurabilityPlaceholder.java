package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.SerializationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;

public class SlotItemDurabilityPlaceholder extends Placeholder {

    public SlotItemDurabilityPlaceholder() {
        super("slot_item_durability");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        String slot = dps.values.get("slot");
        String format = dps.values.get("format");
        
        if ((slot != null) && MathUtils.isInteger(slot) && (Minecraft.getInstance().player != null)) {
            int slotInt = Integer.parseInt(slot);
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(slotInt);
            
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                int maxDurability = stack.getMaxDamage();
                int currentDurability = maxDurability - stack.getDamageValue();
                
                if (format == null) {
                    format = "current";
                }
                
                switch (format.toLowerCase()) {
                    case "current":
                        return String.valueOf(currentDurability);
                    case "max":
                        return String.valueOf(maxDurability);
                    case "damage":
                        return String.valueOf(stack.getDamageValue());
                    case "current_max":
                        return currentDurability + "/" + maxDurability;
                    case "percentage":
                        if (maxDurability > 0) {
                            int percentage = (int) Math.round((currentDurability * 100.0) / maxDurability);
                            return String.valueOf(percentage);
                        }
                        return "0";
                    default:
                        return String.valueOf(currentDurability);
                }
            }
        }
        return "";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of("slot", "format");
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.slot_item_durability");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.slot_item_durability.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("slot", "slot_number");
        values.put("format", "current");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
