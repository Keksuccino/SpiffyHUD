package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SpiffyRenderUtils {

    private static boolean blendLocked = false;

    public static void lockBlend(boolean lock) {
        blendLocked = true;
    }

    public static boolean isBlendLocked() {
        return blendLocked;
    }

    /**
     * Draws a textured quad with the U texture coordinates swapped so that the image appears mirrored horizontally.
     *
     * @param graphics       The graphics context.
     * @param atlasLocation  The texture atlas.
     * @param x              The screen X coordinate.
     * @param y              The screen Y coordinate.
     * @param blitOffset     The z-level (blit offset).
     * @param u              The source U coordinate (left edge) of the texture.
     * @param v              The source V coordinate (top edge) of the texture.
     * @param width          The width of the quad.
     * @param height         The height of the quad.
     * @param textureWidth   The width of the texture.
     * @param textureHeight  The height of the texture.
     */
    public static void blitMirrored(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int blitOffset, int u, int v, int width, int height, int textureWidth, int textureHeight) {

        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.enableBlend();

        // Calculate texture coordinates.
        float minU = (u + width) / (float) textureWidth;
        float maxU = u / (float) textureWidth;
        float minV = v / (float) textureHeight;
        float maxV = (v + height) / (float) textureHeight;

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix, (float)x, (float)y, (float)blitOffset).setUv(minU, minV);
        bufferBuilder.addVertex(matrix, (float)x, (float)(y + height), (float)blitOffset).setUv(minU, maxV);
        bufferBuilder.addVertex(matrix, (float)(x + width), (float)(y + height), (float)blitOffset).setUv(maxU, maxV);
        bufferBuilder.addVertex(matrix, (float)(x + width), (float)y, (float)blitOffset).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.disableBlend();

    }
    
    /**
     * Draws a sprite horizontally mirrored (flipped along the vertical axis).
     * This method is specifically designed for sprite resources and handles sprite texture coordinates.
     *
     * @param graphics  The graphics context.
     * @param sprite    The sprite resource location.
     * @param x         The screen X coordinate.
     * @param y         The screen Y coordinate.
     * @param width     The width of the sprite.
     * @param height    The height of the sprite.
     */
    public static void blitSpriteMirrored(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        
        // Use the internal method for sprite rendering that matches what GuiGraphics does
        // but with horizontally mirrored texture coordinates
        TextureAtlasSprite atlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(sprite);
        
        RenderSystem.setShaderTexture(0, atlasSprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        
        // For horizontal mirroring, swap U coordinates
        float minU = atlasSprite.getU1();
        float maxU = atlasSprite.getU0();
        float minV = atlasSprite.getV0();
        float maxV = atlasSprite.getV1();
        
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix, (float)x, (float)y, 0.0f).setUv(minU, minV);
        bufferBuilder.addVertex(matrix, (float)x, (float)(y + height), 0.0f).setUv(minU, maxV);
        bufferBuilder.addVertex(matrix, (float)(x + width), (float)(y + height), 0.0f).setUv(maxU, maxV);
        bufferBuilder.addVertex(matrix, (float)(x + width), (float)y, 0.0f).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

    }

}
