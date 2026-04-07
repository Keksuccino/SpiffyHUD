package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.keksuccino.spiffyhud.customization.elements.chatcustomizer.ChatCustomizerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    /**
     * Apply custom input field background color
     */
    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void customizeInputBackgroundFill_Spiffy(GuiGraphicsExtractor graphics, int minX, int minY, int maxX, int maxY, int color, Operation<Void> original) {
        if (ChatCustomizerHandler.inputBackgroundColor != null) {
            // Use the custom color with its own alpha
            int customColor = ChatCustomizerHandler.inputBackgroundColor.getColorInt();
            original.call(graphics, minX, minY, maxX, maxY, customColor);
        } else {
            original.call(graphics, minX, minY, maxX, maxY, color);
        }
    }

}
