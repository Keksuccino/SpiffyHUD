package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.fancymenu.customization.overlay.CustomizationOverlayUI;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.fancymenu.util.rendering.ui.menubar.v2.MenuBar;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomizationOverlayUI.class)
public class MixinCustomizationOverlayUI {

    //Disable the "Customizations" toggle in the SpiffyOverlayScreen, so customizations are always enabled for it
    @Inject(method = "buildMenuBar", at = @At("RETURN"), remap = false)
    private static void onReturnBuildMenuBar_Spiffy(boolean expanded, CallbackInfoReturnable<MenuBar> info) {
        MenuBar bar = info.getReturnValue();
        if (bar.getEntry("screen") instanceof MenuBar.ContextMenuBarEntry screenTab) {
            ContextMenu contextMenu = screenTab.getContextMenu();
            if (contextMenu.getEntry("toggle_current_customization") instanceof ContextMenu.ValueCycleContextMenuEntry<?> toggleCustomizationEntry) {
                toggleCustomizationEntry.addIsActiveSupplier((menu, entry) -> !(Minecraft.getInstance().gui.screen() instanceof SpiffyOverlayScreen));
            }
        }
    }

}
