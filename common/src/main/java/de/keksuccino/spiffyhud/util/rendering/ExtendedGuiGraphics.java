package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.ResourceLocation;

public interface ExtendedGuiGraphics {

    /**
     * Blits a texture with horizontally mirrored UV coordinates.
     * This effectively flips the texture horizontally.
     */
    void blitMirrored_Spiffy(RenderPipeline renderType, ResourceLocation texture, int x, int y, int width, int height, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, int color);

    /**
     * Alternative approach using matrix transformation for mirroring.
     * This scales the X axis by -1 to achieve horizontal mirroring.
     */
    void blitMirroredMatrix_Spiffy(RenderPipeline renderType, ResourceLocation texture, int x, int y, int width, int height, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, int color);

}
