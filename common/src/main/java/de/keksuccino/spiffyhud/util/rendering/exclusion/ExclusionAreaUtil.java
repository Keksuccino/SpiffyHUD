package de.keksuccino.spiffyhud.util.rendering.exclusion;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Utility class for working with exclusion areas in GuiGraphicsExtractor.
 * Provides convenient methods and a try-with-resources helper.
 */
public class ExclusionAreaUtil {
    
    /**
     * Push an exclusion area onto the GuiGraphicsExtractor stack.
     */
    public static void pushExclusionArea(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            exclusion.spiffyHud$pushExclusionArea(x1, y1, x2, y2);
        }
    }
    
    /**
     * Pop an exclusion area from the GuiGraphicsExtractor stack.
     */
    public static void popExclusionArea(GuiGraphicsExtractor graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            exclusion.spiffyHud$popExclusionArea();
        }
    }
    
    /**
     * Clear all exclusion areas from the GuiGraphicsExtractor stack.
     */
    public static void clearExclusionAreas(GuiGraphicsExtractor graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            exclusion.spiffyHud$clearExclusionAreas();
        }
    }
    
    /**
     * Check if the GuiGraphicsExtractor has active exclusion areas.
     */
    public static boolean hasExclusionAreas(GuiGraphicsExtractor graphics) {
        if (graphics instanceof IGuiGraphicsExclusionArea exclusion) {
            return exclusion.spiffyHud$hasExclusionAreas();
        }
        return false;
    }
    
    /**
     * Pop all exclusion areas from the GuiGraphicsExtractor stack at once.
     * Returns the number of areas that were popped.
     */
    public static int popAllExclusionAreas(GuiGraphicsExtractor graphics) {
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
    public static ExclusionAreaContext withExclusionArea(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
        pushExclusionArea(graphics, x1, y1, x2, y2);
        return new ExclusionAreaContext(graphics);
    }
    
    /**
     * Auto-closeable context for exclusion areas.
     */
    public static class ExclusionAreaContext implements AutoCloseable {
        private final GuiGraphicsExtractor graphics;
        
        private ExclusionAreaContext(GuiGraphicsExtractor graphics) {
            this.graphics = graphics;
        }
        
        @Override
        public void close() {
            popExclusionArea(graphics);
        }
    }

}
