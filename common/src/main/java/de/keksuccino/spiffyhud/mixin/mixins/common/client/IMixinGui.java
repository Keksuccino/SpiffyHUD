package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(Gui.class)
public interface IMixinGui {

    @Accessor("screen") @Nullable Screen get_screen_Spiffy();

    @Accessor("screen") void set_screen_Spiffy(@Nullable Screen screen);

}
