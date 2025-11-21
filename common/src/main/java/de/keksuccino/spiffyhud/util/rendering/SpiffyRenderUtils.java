package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
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
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, (float)x, (float)y, (float)blitOffset).uv(minU, minV).endVertex();
        bufferBuilder.vertex(matrix, (float)x, (float)(y + height), (float)blitOffset).uv(minU, maxV).endVertex();
        bufferBuilder.vertex(matrix, (float)(x + width), (float)(y + height), (float)blitOffset).uv(maxU, maxV).endVertex();
        bufferBuilder.vertex(matrix, (float)(x + width), (float)y, (float)blitOffset).uv(maxU, minV).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

        RenderSystem.disableBlend();

    }

}
