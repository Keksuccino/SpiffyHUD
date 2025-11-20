package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface IMixinGuiGraphics {

    @Accessor("guiSprites") TextureAtlas get_guiSprites_Spiffy();

    @Invoker("blitSprite") void invoke_private_blitSprite_Spiffy(
            RenderPipeline renderTypeGetter,
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
