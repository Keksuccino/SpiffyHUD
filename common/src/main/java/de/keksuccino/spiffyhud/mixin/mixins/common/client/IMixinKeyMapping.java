package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface IMixinKeyMapping {

    @Accessor("key") InputConstants.Key get_key_Spiffy();

}
