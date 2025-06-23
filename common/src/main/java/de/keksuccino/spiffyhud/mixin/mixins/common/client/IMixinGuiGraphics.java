package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.function.Function;

@Mixin(GuiGraphics.class)
public interface IMixinGuiGraphics {

    @Accessor("sprites") GuiSpriteManager get_sprites_Spiffy();

    @Invoker("blitSprite") void invoke_private_blitSprite_Spiffy(
            Function<ResourceLocation, RenderType> renderTypeGetter,
            TextureAtlasSprite sprite,
            int textureWidth,
            int textureHeight,
            int uPosition,
            int vPosition,
            int x,
            int y,
            int uWidth,
            int vHeight,
            int color
    );

}
