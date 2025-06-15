package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.spiffyhud.util.MouseListenerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Shadow private int fakeRightMouse;

    @Inject(method = "onPress", at = @At("HEAD"))
    private void before_onPress_SpiffyHUD(long windowPointer, int button, int action, int modifiers, CallbackInfo info) {

        boolean bl = action == 1;
        if (Minecraft.ON_OSX && button == 0) {
            if (bl) {
                if ((modifiers & 2) == 2) {
                    button = 1;
                }
            } else if (this.fakeRightMouse > 0) {
                button = 1;
            }
        }

        MouseListenerHandler.notifyListeners(button, action, modifiers);

    }

}
