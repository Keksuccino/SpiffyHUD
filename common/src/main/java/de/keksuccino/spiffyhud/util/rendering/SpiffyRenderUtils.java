package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.spiffyhud.mixin.mixins.common.client.IMixinGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

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
     * In 1.21.6, this uses the mixin implementation for proper UV coordinate mirroring.
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
        
        // Cast to our extended interface and use the mixin method
        ExtendedGuiGraphics extended = (ExtendedGuiGraphics) graphics;
        extended.blitMirrored_Spiffy(
            RenderPipelines.GUI_TEXTURED,
            atlasLocation,
            x, y,
            width, height,
            (float) u, (float) v,
            (float) width, (float) height,
            textureWidth, textureHeight,
            color
        );
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
        
        TextureAtlasSprite atlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(sprite);
        ResourceLocation atlasLocation = atlasSprite.atlasLocation();
        
        // Get the sprite's UV coordinates
        float u0 = atlasSprite.getU0();
        float v0 = atlasSprite.getV0();
        float u1 = atlasSprite.getU1();
        float v1 = atlasSprite.getV1();
        
        // Calculate the texture dimensions from the UV coordinates
        // Since UV coordinates are normalized (0-1), we need to denormalize them
        int atlasWidth = 256; // Default atlas size, adjust if needed
        int atlasHeight = 256;
        
        // Calculate the actual pixel coordinates in the atlas
        float uPixel = u0 * atlasWidth;
        float vPixel = v0 * atlasHeight;
        float uWidth = (u1 - u0) * atlasWidth;
        float vHeight = (v1 - v0) * atlasHeight;
        
        // Use the mixin method with the sprite's texture coordinates
        ExtendedGuiGraphics extended = (ExtendedGuiGraphics) graphics;
        extended.blitMirrored_Spiffy(
            RenderPipelines.GUI_TEXTURED,
            atlasLocation,
            x, y,
            width, height,
            uPixel, vPixel,
            uWidth, vHeight,
            atlasWidth, atlasHeight,
            color
        );
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
     * In 1.21.6, we use mixins to access the private blitSprite method for proper sprite rendering.
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
        IMixinGuiGraphics mixinGraphics = (IMixinGuiGraphics) graphics;
        TextureAtlasSprite textureAtlasSprite = mixinGraphics.get_sprites_Spiffy().getSprite(sprite);
        GuiSpriteScaling guiSpriteScaling = mixinGraphics.get_sprites_Spiffy().getSpriteScaling(textureAtlasSprite);
        
        if (guiSpriteScaling instanceof GuiSpriteScaling.Stretch) {
            mixinGraphics.invoke_private_blitSprite_Spiffy(
                RenderPipelines.GUI_TEXTURED,
                textureAtlasSprite,
                textureWidth,
                textureHeight,
                uPosition,
                vPosition,
                x,
                y,
                uWidth,
                vHeight,
                color
            );
        } else {
            graphics.enableScissor(x, y, x + uWidth, y + vHeight);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x - uPosition, y - vPosition, textureWidth, textureHeight, color);
            graphics.disableScissor();
        }
    }

    /**
     * Draws a textured quad with the U texture coordinates swapped using matrix transformation.
     * This is an alternative mirroring approach using matrix scaling.
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
    public static void blitMirroredMatrix(
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
        
        // Cast to our extended interface and use the matrix-based mixin method
        ExtendedGuiGraphics extended = (ExtendedGuiGraphics) graphics;
        extended.blitMirroredMatrix_Spiffy(
            RenderPipelines.GUI_TEXTURED,
            atlasLocation,
            x, y,
            width, height,
            (float) u, (float) v,
            (float) width, (float) height,
            textureWidth, textureHeight,
            color
        );
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
