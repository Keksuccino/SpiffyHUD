package de.keksuccino.spiffyhud;

import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.fancymenu.customization.screen.identifier.ScreenIdentifierHandler;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinHud;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpiffyUtils {

    public static final SpiffyOverlayScreen DUMMY_SPIFFY_OVERLAY_SCREEN = new SpiffyOverlayScreen(false);

    public static boolean isSpiffyIdentifier(@Nullable String identifier) {
        if (identifier == null) return false;
        return ScreenIdentifierHandler.isIdentifierOfScreen(identifier, DUMMY_SPIFFY_OVERLAY_SCREEN);
    }

    @NotNull
    public static IMixinHud getGuiAccessor() {
        return (IMixinHud) Minecraft.getInstance().gui.hud;
    }

}
