package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinHud;
import de.keksuccino.spiffyhud.util.ComponentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ActionBarMessagePlaceholder extends Placeholder {

    public ActionBarMessagePlaceholder() {
        super("action_bar_message");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        Component message = ((IMixinHud)Minecraft.getInstance().gui.hud).get_overlayMessageString_Spiffy();
        if (message != null) {
            return ComponentUtils.toJson(message);
        }
        return "";
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return null;
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.action_bar_message");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.action_bar_message.desc"));
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
