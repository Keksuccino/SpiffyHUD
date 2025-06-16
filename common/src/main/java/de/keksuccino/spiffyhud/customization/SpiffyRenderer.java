package de.keksuccino.spiffyhud.customization;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.spiffyhud.customization.elements.huderaser.HudEraserElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SpiffyRenderer {

    private static final Logger LOGGER = LogManager.getLogger();
    
    // Our capture buffer to store the pre-HUD render
    private static RenderTarget worldCapture;

    public static void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (!Minecraft.getInstance().options.hideGui) {
            SpiffyGui.INSTANCE.render(graphics, -10000000, -10000000, deltaTracker.getGameTimeDeltaTicks());
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
    }

    /**
     * Initialize or resize the capture buffer if needed
     */
    private static void initializeCapture(Minecraft minecraft) {
        // Create the capture buffer if needed
        if (worldCapture == null) {
            worldCapture = new RenderTarget(false) {
                // We need to extend RenderTarget since it's abstract
                // but we don't need to override any methods
            };
        }
        
        // Make sure our capture buffer matches the current window size
        if (worldCapture.width != minecraft.getWindow().getWidth() || 
            worldCapture.height != minecraft.getWindow().getHeight()) {
            worldCapture.resize(
                minecraft.getWindow().getWidth(),
                minecraft.getWindow().getHeight(),
                Minecraft.ON_OSX
            );
        }
    }
    
    /**
     * Capture the world render before HUD elements are drawn
     */
    public static void captureWorldRender() {
        Minecraft minecraft = Minecraft.getInstance();
        
        // Check if we have any HudEraserElements before proceeding
        boolean hasHudEraserElements = false;
        ScreenCustomizationLayer layer = ScreenCustomizationLayerHandler.getLayerOfScreen(SpiffyOverlayScreen.DUMMY_INSTANCE);
        if (layer != null) {
            hasHudEraserElements = layer.allElements.stream().anyMatch(element -> element instanceof HudEraserElement);
        }
        
        // Skip capture if no eraser elements exist
        if (!hasHudEraserElements) {
            return;
        }
        
        // Make sure our buffer exists and is properly sized
        initializeCapture(minecraft);
        
        // Copy from main framebuffer to our capture buffer
        // Blit from main buffer to our capture buffer
        GlStateManager._glBindFramebuffer(36008, minecraft.getMainRenderTarget().frameBufferId); // GL_READ_FRAMEBUFFER = 36008
        GlStateManager._glBindFramebuffer(36009, worldCapture.frameBufferId); // GL_DRAW_FRAMEBUFFER = 36009
        GlStateManager._glBlitFrameBuffer(
            0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(),
            0, 0, worldCapture.width, worldCapture.height,
            16384, 9728 // GL_COLOR_BUFFER_BIT = 16384, GL_NEAREST = 9728
        );
        
        // Restore the main framebuffer
        minecraft.getMainRenderTarget().bindWrite(true);
    }
    
    /**
     * Apply the captured world render to the HUD eraser elements
     */
    public static void applyHoles(@NotNull GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        
        if (worldCapture == null) return;
        
        // Find any HudEraserElements and draw the captured world texture in those areas
        ScreenCustomizationLayer layer = ScreenCustomizationLayerHandler.getLayerOfScreen(SpiffyOverlayScreen.DUMMY_INSTANCE);
        if (layer != null) {
            // Save current blend state
            boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
            int srcBlend = GL11.glGetInteger(GL11.GL_BLEND_SRC);
            int dstBlend = GL11.glGetInteger(GL11.GL_BLEND_DST);
            
            // Configure blending to replace pixels completely
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);
            
            // Bind our captured texture
            worldCapture.bindRead();
            
            layer.allElements.forEach(abstractElement -> {
                if (abstractElement instanceof HudEraserElement e) {
                    int x = e.getAbsoluteX();
                    int y = e.getAbsoluteY();
                    int width = e.getAbsoluteWidth();
                    int height = e.getAbsoluteHeight();
                    
                    LOGGER.info("Applying hole at {},{} with size {}x{}", x, y, width, height);
                    
                    // Set the texture and shader
                    RenderSystem.setShaderTexture(0, worldCapture.getColorTextureId());
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);

                    // Use a consistent rendering approach like in GuiGraphics
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                    // Get the matrix
                    org.joml.Matrix4f matrix = graphics.pose().last().pose();

                    // Calculate UV coordinates
                    float u1 = (float)x / worldCapture.width;
                    float v1 = (float)y / worldCapture.height;
                    float u2 = (float)(x + width) / worldCapture.width;
                    float v2 = (float)(y + height) / worldCapture.height;

                    // Add vertices
                    bufferBuilder.addVertex(matrix, (float)x, (float)y, 0.0F).setUv(u1, v1);
                    bufferBuilder.addVertex(matrix, (float)x, (float)(y + height), 0.0F).setUv(u1, v2);
                    bufferBuilder.addVertex(matrix, (float)(x + width), (float)(y + height), 0.0F).setUv(u2, v2);
                    bufferBuilder.addVertex(matrix, (float)(x + width), (float)y, 0.0F).setUv(u2, v1);

                    // Draw
                    BufferUploader.draw(bufferBuilder.buildOrThrow());
                }
            });
            
            // Unbind the texture
            worldCapture.unbindRead();
            
            // Restore previous blend state
            if (!blendEnabled) {
                RenderSystem.disableBlend();
            } else {
                RenderSystem.blendFunc(srcBlend, dstBlend);
            }
        }
    }
    
    /**
     * Clean up resources when no longer needed
     */
    public static void cleanup() {
        if (worldCapture != null) {
            worldCapture.destroyBuffers();
            worldCapture = null;
        }
    }

}
