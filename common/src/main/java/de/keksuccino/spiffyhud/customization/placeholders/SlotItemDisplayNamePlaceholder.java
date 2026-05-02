package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.SerializationHelper;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinSpectatorGui;
import de.keksuccino.spiffyhud.util.ComponentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;

public class SlotItemDisplayNamePlaceholder extends Placeholder {

    public SlotItemDisplayNamePlaceholder() {
        super("slot_item_display_name");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        String slot = dps.values.get("slot");
        boolean ignoreSpectator = SerializationHelper.INSTANCE.deserializeBoolean(false, dps.values.get("ignore_spectator"));
        if ((slot != null) && MathUtils.isInteger(slot) && (Minecraft.getInstance().player != null)) {
            int slotInt = Integer.parseInt(slot);
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(slotInt);
            if (Minecraft.getInstance().player.isSpectator() && (slotInt >= 0) && (slotInt <= 8) && !ignoreSpectator) { // If slot is a hotbar slot and player is Spectator, return Spectator GUI slot names
                IMixinSpectatorGui accessor = (IMixinSpectatorGui) Minecraft.getInstance().gui.getSpectatorGui();
                SpectatorMenu menu = accessor.get_menu_Spiffy();
                if (menu != null) {
                    SpectatorMenuItem spectatorMenuItem = menu.getSelectedItem();
                    MutableComponent mutableComponent = (MutableComponent) ((spectatorMenuItem == SpectatorMenu.EMPTY_SLOT) ? menu.getSelectedCategory().getPrompt() : spectatorMenuItem.getName());
                    return ComponentUtils.toJson(mutableComponent);
                }
            } else if (!stack.isEmpty()) {
                MutableComponent mutableComponent = Component.empty().append(stack.getHoverName()).withStyle(stack.getRarity().color());
                return ComponentUtils.toJson(mutableComponent);
            }
        }
        return "";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of("slot", "ignore_spectator");
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.slot_item_display_name");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.slot_item_display_name.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("slot", "slot_number");
        values.put("ignore_spectator", "false");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
