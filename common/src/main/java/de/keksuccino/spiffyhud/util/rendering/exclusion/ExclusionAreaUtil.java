package de.keksuccino.spiffyhud.util.rendering.exclusion;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Utility class for working with exclusion areas in GuiGraphics.
 * Provides convenient methods and a try-with-resources helper.
 */
public class ExclusionAreaUtil {
    
    /**
     * Push an exclusion area onto the GuiGraphics stack.
     */
    public static void pushExclusionArea(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            exclusion.spiffyHud$pushExclusionArea(x1, y1, x2, y2);
        }
    }
    
    /**
     * Pop an exclusion area from the GuiGraphics stack.
     */
    public static void popExclusionArea(GuiGraphics graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            exclusion.spiffyHud$popExclusionArea();
        }
    }
    
    /**
     * Clear all exclusion areas from the GuiGraphics stack.
     */
    public static void clearExclusionAreas(GuiGraphics graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            exclusion.spiffyHud$clearExclusionAreas();
        }
    }
    
    /**
     * Check if the GuiGraphics has active exclusion areas.
     */
    public static boolean hasExclusionAreas(GuiGraphics graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            return exclusion.spiffyHud$hasExclusionAreas();
        }
        return false;
    }
    
    /**
     * Pop all exclusion areas from the GuiGraphics stack at once.
     * Returns the number of areas that were popped.
     */
    public static int popAllExclusionAreas(GuiGraphics graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            return exclusion.spiffyHud$popAllExclusionAreas();
        }
        return 0;
    }
    
    /**
     * Create an auto-closeable exclusion area context.
     * Use with try-with-resources to automatically pop the exclusion area.
     * <p>
     * Example:
     * <pre>
     * try (var exclusion = ExclusionAreaUtil.withExclusionArea(graphics, 10, 10, 100, 100)) {
     *     // Render code here - nothing will render in the 10,10 to 100,100 area
     * }
     * </pre>
     */
    public static ExclusionAreaContext withExclusionArea(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        pushExclusionArea(graphics, x1, y1, x2, y2);
        return new ExclusionAreaContext(graphics);
    }
    
    /**
     * Auto-closeable context for exclusion areas.
     */
    public static class ExclusionAreaContext implements AutoCloseable {
        private final GuiGraphics graphics;
        
        private ExclusionAreaContext(GuiGraphics graphics) {
            this.graphics = graphics;
        }
        
        @Override
        public void close() {
            popExclusionArea(graphics);
        }
    }

}
