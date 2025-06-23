package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class SpiffyRenderUtils {

    /**
     * Draws a textured quad with the U texture coordinates swapped so that the image appears mirrored horizontally.
     * In 1.21.6, this uses a matrix transformation approach for mirroring.
     *
     * @param graphics             The graphics context.
     * @param atlasLocation        The texture atlas.
     * @param x                    The screen X coordinate.
     * @param y                    The screen Y coordinate.
     * @param u                    The source U coordinate (left edge) of the texture.
     * @param v                    The source V coordinate (top edge) of the texture.
     * @param width                The width of the quad.
     * @param height               The height of the quad.
     * @param textureWidth         The width of the texture.
     * @param textureHeight        The height of the texture.
     * @param color                The color to apply to the texture.
     */
    public static void blitMirrored(
            GuiGraphics graphics,
            ResourceLocation atlasLocation,
            int x,
            int y,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            int color) {
        
        RenderSystem.assertOnRenderThread();
        
        // Use matrix transformation for mirroring
        graphics.pose().pushMatrix();
        
        // Translate to the right edge of where we want to draw, then scale X by -1
        graphics.pose().translate(x + width, y, 0);
        graphics.pose().scale(-1.0f, 1.0f, 1.0f);
        
        // Draw at origin (0,0) since we've already translated
        graphics.blit(RenderType.guiTextured(atlasLocation), 0, 0, u, v, width, height, textureWidth, textureHeight, color);
        
        // Restore matrix state
        graphics.pose().popMatrix();
    }

    /**
     * Draws a textured quad with the U texture coordinates swapped so that the image appears mirrored horizontally.
     * Uses white color.
     */
    public static void blitMirrored(
            GuiGraphics graphics,
            ResourceLocation atlasLocation,
            int x,
            int y,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight) {

        blitMirrored(
            graphics,
            atlasLocation,
            x,
            y,
            u,
            v,
            width,
            height,
            textureWidth,
            textureHeight,
            -1  // White (fully opaque)
        );
    }

    /**
     * Draws a sprite horizontally mirrored (flipped along the vertical axis).
     * This method is specifically designed for sprite resources and handles sprite texture coordinates.
     *
     * @param graphics         The graphics context.
     * @param sprite           The sprite resource location.
     * @param x                The screen X coordinate.
     * @param y                The screen Y coordinate.
     * @param width            The width of the sprite.
     * @param height           The height of the sprite.
     * @param color            The color to apply to the sprite.
     */
    public static void blitSpriteMirrored(
            GuiGraphics graphics,
            ResourceLocation sprite,
            int x,
            int y,
            int width,
            int height,
            int color) {

        RenderSystem.assertOnRenderThread();
        
        // Use matrix transformation for mirroring
        graphics.pose().pushMatrix();
        
        // Translate to the right edge of where we want to draw, then scale X by -1
        graphics.pose().translate(x + width, y, 0);
        graphics.pose().scale(-1.0f, 1.0f, 1.0f);
        
        // Draw the sprite at origin (0,0) since we've already translated
        graphics.blitSprite(RenderType.guiTextured, sprite, 0, 0, width, height, color);
        
        // Restore matrix state
        graphics.pose().popMatrix();
    }

    /**
     * Draws a sprite horizontally mirrored (flipped along the vertical axis).
     * Uses white color.
     */
    public static void blitSpriteMirrored(
            GuiGraphics graphics,
            ResourceLocation sprite,
            int x,
            int y,
            int width,
            int height) {

        blitSpriteMirrored(
            graphics,
            sprite,
            x,
            y,
            width,
            height,
            -1  // White (fully opaque)
        );
    }

    /**
     * Blits a sprite with specific UV coordinates.
     * In 1.21.6, we use the built-in blitSprite method with appropriate parameters.
     */
    public static void blitSprite(
            GuiGraphics graphics,
            ResourceLocation sprite,
            int textureWidth,
            int textureHeight,
            int uPosition,
            int vPosition,
            int x,
            int y,
            int uWidth,
            int vHeight,
            int color
    ) {
        // Use the built-in blitSprite method with UV parameters
        graphics.blitSprite(RenderType.guiTextured, sprite, textureWidth, textureHeight, 
                           uPosition, vPosition, x, y, uWidth, vHeight, color);
    }

    /**
     * Returns the given color with the given alpha applied.
     *
     * @param color The original color value (ARGB format)
     * @param alpha The alpha value to apply (0.0f to 1.0f)
     *
     * @return The color with the new alpha value applied
     */
    public static int colorWithAlpha(int color, float alpha) {
        int alphaComponent = Math.round(alpha * 255.0f);
        if (alphaComponent > 255) alphaComponent = 255;
        if (alphaComponent < 0) alphaComponent = 0;
        // Extract color components
        int red = ARGB.red(color);
        int green = ARGB.green(color);
        int blue = ARGB.blue(color);
        // Create new color with the specified alpha
        return ARGB.color(alphaComponent, red, green, blue);
    }

}
