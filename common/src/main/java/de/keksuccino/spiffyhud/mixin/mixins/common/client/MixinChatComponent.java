package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.keksuccino.spiffyhud.customization.elements.chatcustomizer.ChatCustomizerHandler;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public abstract class MixinChatComponent {

    // Store the current fade factor for proper color fading
    @Unique private final ThreadLocal<Float> currentFadeFactor_Spiffy = new ThreadLocal<>();

    @Shadow @Final Minecraft minecraft;

    @Shadow public boolean isChatFocused() { return false; }
    @Shadow private double getScale() { return 1.0; }
    @Shadow private int getWidth() { return 0; }

    /**
     * Capture the fade factor used in the render loop
     */
    @ModifyVariable(method = "forEachLine", at = @At("STORE"), ordinal = 0)
    private float captureFadeFactor_Spiffy(float factor) {
        currentFadeFactor_Spiffy.set(factor);
        return factor;
    }

    /**
     * Modify chat translation to position it correctly based on corner setting
     * This only handles LEFT vs RIGHT positioning
     * @reason Adjust the chat render pose to support right-aligned layouts.
     */
    @WrapOperation(
        method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;updatePose(Ljava/util/function/Consumer;)V")
    )
    private void wrap_updatePose_Spiffy(ChatComponent.ChatGraphicsAccess access, Consumer<Matrix3x2f> consumer, Operation<Void> original) {
        if (!ChatCustomizerHandler.isChatRightAligned()) {
            original.call(access, consumer);
            return;
        }

        Consumer<Matrix3x2f> spiffyConsumer = (Matrix3x2f matrix) -> {
            float scale = (float)this.getScale();
            matrix.scale(scale, scale);
            float chatWidth = (float)this.getWidth() / scale;
            float newX = this.minecraft.getWindow().getGuiScaledWidth() - chatWidth - 8.0F;
            matrix.translate(newX, 0.0F);
        };
        original.call(access, spiffyConsumer);
    }

    /**
     * Wrap line spacing option value when accessed in render method
     * @reason Apply the custom line spacing override when rendering chat.
     */
    @WrapOperation(
        method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;")
    )
    private Object wrap_get_render_Spiffy(OptionInstance<?> instance, Operation<Object> original) {
        if (instance == minecraft.options.chatLineSpacing() && ChatCustomizerHandler.lineSpacing != null) {
            return ChatCustomizerHandler.lineSpacing;
        }
        return original.call(instance);
    }

    /**
     * Wrap line spacing in getLineHeight method
     * @reason Apply the custom line spacing override when calculating line height.
     */
    @WrapOperation(method = "getLineHeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object wrap_getLineHeight_Spiffy(OptionInstance<?> instance, Operation<Object> original) {
        if (instance == minecraft.options.chatLineSpacing() && ChatCustomizerHandler.lineSpacing != null) {
            return ChatCustomizerHandler.lineSpacing;
        }
        return original.call(instance);
    }

    /**
     * Customize chat background fill color
     * @reason Replace the line background fill color with the custom value (including fade).
     */
    @ModifyVariable(
        method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ChatComponent.ChatGraphicsAccess wrap_renderChatGraphicsAccess_Spiffy(ChatComponent.ChatGraphicsAccess access) {
        return new ChatGraphicsAccessWrapper_Spiffy(access, this.currentFadeFactor_Spiffy, this::isChatFocused);
    }

    private static final class ChatGraphicsAccessWrapper_Spiffy implements ChatComponent.ChatGraphicsAccess {

        private final ChatComponent.ChatGraphicsAccess delegate_Spiffy;
        private final ThreadLocal<Float> fadeFactor_Spiffy;
        private final BooleanSupplier focusedSupplier_Spiffy;

        private ChatGraphicsAccessWrapper_Spiffy(
            ChatComponent.ChatGraphicsAccess delegate,
            ThreadLocal<Float> fadeFactor,
            BooleanSupplier focusedSupplier
        ) {
            this.delegate_Spiffy = delegate;
            this.fadeFactor_Spiffy = fadeFactor;
            this.focusedSupplier_Spiffy = focusedSupplier;
        }

        @Override
        public void updatePose(Consumer<Matrix3x2f> consumer) {
            this.delegate_Spiffy.updatePose(consumer);
        }

        @Override
        public void fill(int minX, int minY, int maxX, int maxY, int color) {
            if (ChatCustomizerHandler.chatBackgroundColor != null && minX == -4) {
                int customColor = ChatCustomizerHandler.chatBackgroundColor.getColorInt();
                Float fadeFactor = this.focusedSupplier_Spiffy.getAsBoolean() ? 1.0F : this.fadeFactor_Spiffy.get();
                if (fadeFactor != null && fadeFactor < 1.0f) {
                    int customAlpha = (customColor >> 24) & 0xFF;
                    int fadedAlpha = (int)(customAlpha * fadeFactor);
                    customColor = (customColor & 0x00FFFFFF) | (fadedAlpha << 24);
                }
                this.delegate_Spiffy.fill(minX, minY, maxX, maxY, customColor);
                return;
            }

            this.delegate_Spiffy.fill(minX, minY, maxX, maxY, color);
        }

        @Override
        public boolean handleMessage(int i, float f, FormattedCharSequence formattedCharSequence) {
            return this.delegate_Spiffy.handleMessage(i, f, formattedCharSequence);
        }

        @Override
        public void handleTag(int i, int j, int k, int l, float f, GuiMessageTag guiMessageTag) {
            this.delegate_Spiffy.handleTag(i, j, k, l, f, guiMessageTag);
        }

        @Override
        public void handleTagIcon(int i, int j, boolean bl, GuiMessageTag guiMessageTag, GuiMessageTag.Icon icon) {
            this.delegate_Spiffy.handleTagIcon(i, j, bl, guiMessageTag, icon);
        }

    }

}
