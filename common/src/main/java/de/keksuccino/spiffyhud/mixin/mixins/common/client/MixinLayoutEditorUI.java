package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorUI;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(LayoutEditorUI.class)
public class MixinLayoutEditorUI {

    /**
     * @reason Remove unsupported right-click menu options for Spiffy layouts.
     */
    @Inject(method = "buildRightClickContextMenu", at = @At("RETURN"), remap = false)
    private void after_buildRightClickContextMenu_Spiffy(CallbackInfoReturnable<ContextMenu> info) {

        ContextMenu menu = info.getReturnValue();
        if (menu == null) return;

        if (isSpiffyEditorOpen_Spiffy()) {
            menu.removeEntry("menu_background_settings");
            menu.removeEntry("keep_background_aspect_ratio");
            menu.removeEntry("edit_menu_title");
            menu.removeEntry("scroll_list_customizations");
            menu.removeEntry("render_custom_elements_behind_vanilla");
            menu.removeEntry("auto_scaling");
            menu.removeEntry("forced_gui_scale");
            menu.removeEntry("open_audio");
            menu.removeEntry("close_audio");
            menu.removeEntry("manage_open_screen_actions");
            menu.removeEntry("manage_close_screen_actions");
            menu.removeEntry("render_custom_elements_behind_vanilla");
        }

    }

    /**
     * @reason Remove unsupported element types from Spiffy layouts.
     */
    @WrapOperation(method = "buildElementContextMenu", at = @At(value = "INVOKE", target = "Lde/keksuccino/fancymenu/customization/element/ElementRegistry;getBuilders()Ljava/util/List;", remap = false), remap = false)
    private static @NotNull List<ElementBuilder<?, ?>> wrap_getBuilders_in_buildElementContextMenu_Spiffy(Operation<List<ElementBuilder<?, ?>>> original) {

        List<ElementBuilder<?,?>> builders = original.call();

        if (isSpiffyEditorOpen_Spiffy()) {
            builders.removeIf(elementBuilder -> elementBuilder.getIdentifier().equals("audio_v2"));
            builders.removeIf(elementBuilder -> elementBuilder.getIdentifier().equals("music_controller"));
        }

        return builders;

    }

    @Unique
    private static boolean isSpiffyEditorOpen_Spiffy() {
        if (Minecraft.getInstance().screen instanceof LayoutEditorScreen e) {
            return e.layoutTargetScreen instanceof SpiffyOverlayScreen;
        }
        return false;
    }

}
