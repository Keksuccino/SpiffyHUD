package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.keksuccino.spiffyhud.customization.elements.chatcustomizer.ChatCustomizerHandler;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.OptionInstance;

@Mixin(ChatComponent.class)
public abstract class MixinChatComponent {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private boolean isChatFocused() { return false; }
    @Shadow public double getScale() { return 1.0; }
    @Shadow public int getWidth() { return 0; }

    // Store the current fade factor for proper color fading
    @Unique private ThreadLocal<Float> currentFadeFactor_Spiffy = new ThreadLocal<>();

    /**
     * Capture the fade factor used in the render loop
     */
    @WrapOperation(method = "forEachLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;getTimeFactor(I)D"))
    private double wrap_getTimeFactor_Spiffy(int t, Operation<Double> original) {
        double factor = original.call(t);
        currentFadeFactor_Spiffy.set((float) factor);
        return factor;
    }

    /**
     * Modify chat translation to position it correctly based on corner setting
     * This only handles LEFT vs RIGHT positioning
     */
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;translate(FF)Lorg/joml/Matrix3x2f;", ordinal = 0))
    private Matrix3x2f modifyChatTranslation_Spiffy(Matrix3x2fStack instance, float x, float y, Operation<Matrix3x2f> original) {
        if (ChatCustomizerHandler.isChatRightAligned()) {
            // For right-aligned chat, adjust x position
            float chatWidth = (float) this.getWidth() / (float) this.getScale();
            float newX = minecraft.getWindow().getGuiScaledWidth() - chatWidth - 8;
            original.call(instance, newX, y);
        } else {
            // Default position
            original.call(instance, x, y);
        }
        return null;
    }

    /**
     * Wrap line spacing option value when accessed in render method
     */
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object wrap_OptionGet_Spiffy(OptionInstance<?> instance, Operation<Object> original) {
        if (instance == minecraft.options.chatLineSpacing() && ChatCustomizerHandler.lineSpacing != null) {
            return ChatCustomizerHandler.lineSpacing;
        }
        return original.call(instance);
    }

    /**
     * Wrap line spacing in getLineHeight method
     */
    @WrapOperation(method = "getLineHeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object wrap_LineSpacingGet_Spiffy(OptionInstance<?> instance, Operation<Object> original) {
        if (instance == minecraft.options.chatLineSpacing() && ChatCustomizerHandler.lineSpacing != null) {
            return ChatCustomizerHandler.lineSpacing;
        }
        return original.call(instance);
    }

    /**
     * Customize chat background fill color
     */
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0))
    private void customizeBackgroundFill_Spiffy(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int color, Operation<Void> original) {
        if (ChatCustomizerHandler.chatBackgroundColor != null) {
            int customColor = ChatCustomizerHandler.chatBackgroundColor.getColorInt();
            // Apply fade effect using the captured fade factor
            Float fadeFactor = this.isChatFocused() ? 1.0F : currentFadeFactor_Spiffy.get();
            if ((fadeFactor != null) && fadeFactor < 1.0f) {
                // Extract the custom alpha
                int customAlpha = (customColor >> 24) & 0xFF;
                // Apply fade factor to the alpha
                int fadedAlpha = (int)(customAlpha * fadeFactor);
                // Create a new color with the faded alpha
                customColor = (customColor & 0x00FFFFFF) | (fadedAlpha << 24);
            }
            original.call(graphics, minX, minY, maxX, maxY, customColor);
        } else {
            original.call(graphics, minX, minY, maxX, maxY, color);
        }
    }

}