package de.keksuccino.spiffyhud.util.rendering.exclusion;

import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Example usage of the exclusion area system.
 * This class demonstrates different ways to use exclusion areas.
 */
public class ExclusionAreaExample {
    
    /**
     * Example 1: Basic usage with manual push/pop
     */
    public static void renderWithBasicExclusion(GuiGraphics graphics, Font font) {

        // Define an exclusion area from 100,100 to 300,200
        ExclusionAreaUtil.pushExclusionArea(graphics, 100, 100, 300, 200);
        
        try {
            // This rectangle partially overlaps the exclusion area - only the non-overlapping part will render
            graphics.fill(50, 50, 350, 250, DrawableColor.BLACK.getColorInt());
            
            // This text is fully within the exclusion area - it won't render
            graphics.drawString(font, "Hidden Text", 150, 150, -1);
            
            // This text is outside the exclusion area - it will render normally
            graphics.drawString(font, "Visible Text", 10, 10, -1);
        } finally {
            // Always pop the exclusion area
            ExclusionAreaUtil.popExclusionArea(graphics);
        }

    }
    
    /**
     * Example 2: Using try-with-resources for automatic cleanup
     */
    public static void renderWithAutoExclusion(GuiGraphics graphics, Font font) {
        // This automatically pops the exclusion area when the block exits
        try (var exclusion = ExclusionAreaUtil.withExclusionArea(graphics, 100, 100, 300, 200)) {
            graphics.fill(50, 50, 350, 250, 0xFF0000FF);
            graphics.drawString(font, "Test", 150, 150, 0xFFFFFF);
        }
    }
    
    /**
     * Example 3: Nested exclusion areas
     */
    public static void renderWithNestedExclusions(GuiGraphics graphics, Font font) {
        // First exclusion area
        try (var exclusion1 = ExclusionAreaUtil.withExclusionArea(graphics, 50, 50, 200, 200)) {
            // This won't render in the 50,50 to 200,200 area
            graphics.fill(0, 0, 300, 300, 0xFF0000FF);
            
            // Second exclusion area (nested)
            try (var exclusion2 = ExclusionAreaUtil.withExclusionArea(graphics, 300, 50, 450, 200)) {
                // This won't render in either exclusion area
                graphics.fill(0, 0, 500, 300, 0xFF00FF00);
            }
            
            // After popping exclusion2, only exclusion1 is active
            graphics.fill(350, 100, 400, 150, 0xFFFF0000); // This will render
        }
    }
    
    /**
     * Example 4: Dynamic exclusion areas (e.g., for UI windows)
     */
    public static void renderWithDynamicExclusion(GuiGraphics graphics, Font font, int windowX, int windowY, int windowWidth, int windowHeight) {
        // Exclude rendering behind a UI window
        try (var exclusion = ExclusionAreaUtil.withExclusionArea(graphics, 
                windowX, windowY, windowX + windowWidth, windowY + windowHeight)) {
            
            // Render background elements that should be hidden behind the window
            for (int i = 0; i < 10; i++) {
                graphics.drawString(font, "Background Text " + i, 10, 20 + i * 20, 0x808080);
            }
            
            // Draw some shapes that will be clipped by the window area
            graphics.fill(0, 0, 500, 500, 0x20FFFFFF);
        }
        
        // Draw the window itself (outside the exclusion area)
        graphics.fill(windowX, windowY, windowX + windowWidth, windowY + windowHeight, 0xFF333333);
        graphics.renderOutline(windowX, windowY, windowWidth, windowHeight, 0xFFFFFFFF);
        graphics.drawString(font, "Window Content", windowX + 10, windowY + 10, 0xFFFFFF);
    }
    
    /**
     * Example 5: Conditional exclusion
     */
    public static void renderWithConditionalExclusion(GuiGraphics graphics, Font font, boolean enableExclusion) {
        if (enableExclusion) {
            ExclusionAreaUtil.pushExclusionArea(graphics, 100, 100, 300, 300);
        }
        
        try {
            // This will be affected by exclusion only if enableExclusion is true
            graphics.fill(0, 0, 400, 400, 0x80FF0000);
            graphics.drawCenteredString(font, "Conditional Rendering", 200, 200, 0xFFFFFF);
        } finally {
            if (enableExclusion) {
                ExclusionAreaUtil.popExclusionArea(graphics);
            }
        }
    }
    
    /**
     * Example 6: Using popAll to clear all exclusion areas at once
     */
    public static void renderWithPopAll(GuiGraphics graphics, Font font) {
        // Push multiple exclusion areas
        ExclusionAreaUtil.pushExclusionArea(graphics, 50, 50, 150, 150);
        ExclusionAreaUtil.pushExclusionArea(graphics, 200, 50, 300, 150);
        ExclusionAreaUtil.pushExclusionArea(graphics, 350, 50, 450, 150);
        
        // Render something
        graphics.fill(0, 0, 500, 200, 0xFF0000FF);
        
        // Pop all exclusion areas at once
        int popped = ExclusionAreaUtil.popAllExclusionAreas(graphics);
        graphics.drawString(font, "Popped " + popped + " exclusion areas", 10, 210, 0xFFFFFF);
        
        // This will render without any exclusions
        graphics.fill(0, 220, 500, 300, 0xFF00FF00);
    }

}
