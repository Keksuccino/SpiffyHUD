package de.keksuccino.spiffyhud.util.rendering.exclusion;

/**
 * Interface to access the exclusion area functionality added to GuiGraphics via mixin.
 * Cast GuiGraphics to this interface to access the exclusion area methods.
 */
public interface IGuiGraphicsExclusionArea {
    
    /**
     * Push a new exclusion area onto the stack.
     * Nothing will be rendered within this area until it's popped.
     * 
     * @param x1 First x coordinate
     * @param y1 First y coordinate
     * @param x2 Second x coordinate
     * @param y2 Second y coordinate
     */
    void spiffyHud$pushExclusionArea(int x1, int y1, int x2, int y2);
    
    /**
     * Pop the most recent exclusion area from the stack.
     * 
     * @throws IllegalStateException if the stack is empty
     */
    void spiffyHud$popExclusionArea();
    
    /**
     * Clear all exclusion areas.
     */
    void spiffyHud$clearExclusionAreas();
    
    /**
     * Check if exclusion areas are currently active.
     * 
     * @return true if there are active exclusion areas
     */
    boolean spiffyHud$hasExclusionAreas();
}
