package de.keksuccino.spiffyhud.util.rendering.exclusion;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import java.util.ArrayDeque;
import java.util.Deque;

public class ExclusionAreaStack {
    private final Deque<ScreenRectangle> stack = new ArrayDeque<>();
    
    public void push(ScreenRectangle area) {
        stack.addLast(area);
    }
    
    public void pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Exclusion area stack underflow");
        }
        stack.removeLast();
    }
    
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    public void clear() {
        stack.clear();
    }
    
    public boolean isPointExcluded(float x, float y) {
        for (ScreenRectangle area : stack) {
            if (area.containsPoint((int)x, (int)y)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isRectangleFullyExcluded(float minX, float minY, float maxX, float maxY) {
        for (ScreenRectangle area : stack) {
            if (area.left() <= minX && area.right() >= maxX && 
                area.top() <= minY && area.bottom() >= maxY) {
                return true;
            }
        }
        return false;
    }
    
    public boolean doesRectangleIntersectExclusion(float minX, float minY, float maxX, float maxY) {
        for (ScreenRectangle area : stack) {
            if (!(maxX < area.left() || minX > area.right() || 
                  maxY < area.top() || minY > area.bottom())) {
                return true;
            }
        }
        return false;
    }
}
