package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ActionBarMessageTimePlaceholder extends Placeholder {

    public ActionBarMessageTimePlaceholder() {
        super("action_bar_message_time");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        int time = ((IMixinHud)Minecraft.getInstance().gui.hud).get_overlayMessageTime_Spiffy();
        return "" + time;
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return null;
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.action_bar_message_time");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.action_bar_message_time.desc"));
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
