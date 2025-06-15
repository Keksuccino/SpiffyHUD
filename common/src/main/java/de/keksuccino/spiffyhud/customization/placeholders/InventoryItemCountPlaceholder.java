package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class InventoryItemCountPlaceholder extends Placeholder {

    public InventoryItemCountPlaceholder() {
        super("inventory_item_count");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        String itemKey = dps.values.get("item");
        
        if ((itemKey != null) && (Minecraft.getInstance().player != null)) {
            try {
                ResourceLocation itemId = ResourceLocation.parse(itemKey);
                Optional<Holder.Reference<Item>> optional = BuiltInRegistries.ITEM.get(itemId);
                Item targetItem = ((optional != null) && optional.isPresent()) ? optional.get().value() : null;
                
                if (targetItem != null) {
                    int totalCount = 0;
                    
                    // Count items in main inventory (0-35)
                    for (int i = 0; i < 36; i++) {
                        ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
                        if (!stack.isEmpty() && stack.getItem() == targetItem) {
                            totalCount += stack.getCount();
                        }
                    }
                    
                    // Count items in armor slots (36-39)
                    for (int i = 36; i < 40; i++) {
                        ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
                        if (!stack.isEmpty() && stack.getItem() == targetItem) {
                            totalCount += stack.getCount();
                        }
                    }
                    
                    // Count items in offhand slot (40)
                    ItemStack offhandStack = Minecraft.getInstance().player.getInventory().getItem(40);
                    if (!offhandStack.isEmpty() && offhandStack.getItem() == targetItem) {
                        totalCount += offhandStack.getCount();
                    }
                    
                    return String.valueOf(totalCount);
                }
            } catch (Exception e) {
                // Invalid item key format
                return "0";
            }
        }
        return "0";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of("item");
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.inventory_item_count");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.inventory_item_count.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("item", "minecraft:diamond");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
