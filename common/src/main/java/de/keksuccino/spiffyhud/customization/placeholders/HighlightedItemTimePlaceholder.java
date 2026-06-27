package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinHud;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinSpectatorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class HighlightedItemTimePlaceholder extends Placeholder {

    public HighlightedItemTimePlaceholder() {
        super("highlighted_item_time");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        int time = ((IMixinHud)Minecraft.getInstance().gui.hud).get_toolHighlightTimer_Spiffy();
        if ((Minecraft.getInstance().player) != null && (Minecraft.getInstance().player.isSpectator())) {
            if (((IMixinSpectatorGui)Minecraft.getInstance().gui.hud.getSpectatorGui()).invoke_getHotbarAlpha_Spiffy() > 0) {
                time = (int) (40.0 * Minecraft.getInstance().options.notificationDisplayTime().get());
            } else {
                time = 0;
            }
        }
        return "" + time;
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return null;
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.highlighted_item_time");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.highlighted_item_time.desc"));
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
