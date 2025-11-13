package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.authlib.GameProfile;
import de.keksuccino.spiffyhud.util.death.DeathPointStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps track of the local client's death animation to persist the last death position.
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {

    protected MixinLocalPlayer(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    /**
     * @reason Persist the death point the first tick the death animation runs.
     */
    @Inject(method = "tickDeath", at = @At("HEAD"))
    private void head_tickDeath_captureDeathPoint_Spiffy(CallbackInfo ci) {
        if (this.deathTime != 0) {
            return;
        }
        Level level = this.level();
        if ((level == null) || (Minecraft.getInstance().player != (Object) this)) {
            return;
        }
        DeathPointStorage.recordDeath(level, this.getX(), this.getY(), this.getZ());
    }
    
}
