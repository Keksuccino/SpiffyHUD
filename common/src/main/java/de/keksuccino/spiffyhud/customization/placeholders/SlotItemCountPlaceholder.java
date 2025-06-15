package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;

public class SlotItemCountPlaceholder extends Placeholder {

    public SlotItemCountPlaceholder() {
        super("slot_item_count");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        String slot = dps.values.get("slot");
        
        if ((slot != null) && MathUtils.isInteger(slot) && (Minecraft.getInstance().player != null)) {
            int slotInt = Integer.parseInt(slot);
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(slotInt);
            
            if (!stack.isEmpty()) {
                return String.valueOf(stack.getCount());
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
        return I18n.get("spiffyhud.placeholders.slot_item_count");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.slot_item_count.desc"));
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
