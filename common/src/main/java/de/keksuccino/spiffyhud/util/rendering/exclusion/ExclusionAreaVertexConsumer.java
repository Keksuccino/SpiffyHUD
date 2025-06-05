package de.keksuccino.spiffyhud.util.rendering.exclusion;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;

public class ExclusionAreaVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final ExclusionAreaStack exclusionStack;
    private final VertexFormat.Mode mode;
    
    // Vertex accumulator for current primitive
    private final List<VertexData> currentPrimitive = new ArrayList<>();
    private final int verticesPerPrimitive;
    private int currentVertexIndex = 0;
    
    // Track if we're in the middle of a primitive that should be excluded
    private boolean currentPrimitiveExcluded = false;
    
    public ExclusionAreaVertexConsumer(VertexConsumer delegate, ExclusionAreaStack exclusionStack, VertexFormat.Mode mode) {
        this.delegate = delegate;
        this.exclusionStack = exclusionStack;
        this.mode = mode;
        this.verticesPerPrimitive = getVerticesPerPrimitive(mode);
        
        // Pre-allocate vertex data
        for (int i = 0; i < Math.max(verticesPerPrimitive, 4); i++) {
            currentPrimitive.add(new VertexData());
        }
    }
    
    private static int getVerticesPerPrimitive(VertexFormat.Mode mode) {
        return switch (mode) {
            case LINES -> 2;
            case TRIANGLES -> 3;
            case QUADS -> 4;
            case LINE_STRIP, TRIANGLE_STRIP, TRIANGLE_FAN -> 3; // Keep buffer of 3 for strips
            default -> 1;
        };
    }
    
    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        // For safety, always pass through vertices if we're at risk of incomplete primitives
        if (mode == VertexFormat.Mode.QUADS || mode == VertexFormat.Mode.TRIANGLES || mode == VertexFormat.Mode.LINES) {
            if (currentVertexIndex >= currentPrimitive.size()) {
                currentPrimitive.add(new VertexData());
            }
            
            VertexData vertex = currentPrimitive.get(currentVertexIndex);
            vertex.x = x;
            vertex.y = y;
            vertex.z = z;
            vertex.hasPosition = true;
            
            currentVertexIndex++;
            
            // Check if we've completed a primitive
            if (currentVertexIndex >= verticesPerPrimitive) {
                // Check exclusion for the complete primitive
                if (shouldExcludePrimitive()) {
                    // Don't forward the vertices
                    currentVertexIndex = 0;
                } else {
                    // Forward all vertices of this primitive
                    for (int i = 0; i < verticesPerPrimitive; i++) {
                        forwardVertex(currentPrimitive.get(i));
                    }
                    currentVertexIndex = 0;
                }
            }
        } else {
            // For strips, fans, and other modes, always forward immediately
            // to avoid incomplete primitive issues
            delegate.addVertex(x, y, z);
        }
        
        return this;
    }
    
    private boolean shouldExcludePrimitive() {
        // Calculate primitive bounds
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        
        for (int i = 0; i < verticesPerPrimitive; i++) {
            VertexData v = currentPrimitive.get(i);
            if (v.hasPosition) {
                minX = Math.min(minX, v.x);
                minY = Math.min(minY, v.y);
                maxX = Math.max(maxX, v.x);
                maxY = Math.max(maxY, v.y);
            }
        }
        
        return exclusionStack.doesRectangleIntersectExclusion(minX, minY, maxX, maxY);
    }
    
    private void forwardVertex(VertexData v) {
        if (v.hasPosition) {
            delegate.addVertex(v.x, v.y, v.z);
            if (v.hasColor) delegate.setColor(v.colorR, v.colorG, v.colorB, v.colorA);
            if (v.hasUv) delegate.setUv(v.u, v.v);
            if (v.hasOverlay) delegate.setOverlay(v.overlay);
            if (v.hasLight) delegate.setLight(v.light);
            if (v.hasNormal) delegate.setNormal(v.nx, v.ny, v.nz);
        }
    }
    
    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        if (mode == VertexFormat.Mode.QUADS || mode == VertexFormat.Mode.TRIANGLES || mode == VertexFormat.Mode.LINES) {
            if (currentVertexIndex > 0 && currentVertexIndex <= currentPrimitive.size()) {
                VertexData vertex = currentPrimitive.get(currentVertexIndex - 1);
                vertex.colorR = r;
                vertex.colorG = g;
                vertex.colorB = b;
                vertex.colorA = a;
                vertex.hasColor = true;
            }
        } else {
            delegate.setColor(r, g, b, a);
        }
        return this;
    }
    
    @Override
    public VertexConsumer setUv(float u, float v) {
        if (mode == VertexFormat.Mode.QUADS || mode == VertexFormat.Mode.TRIANGLES || mode == VertexFormat.Mode.LINES) {
            if (currentVertexIndex > 0 && currentVertexIndex <= currentPrimitive.size()) {
                VertexData vertex = currentPrimitive.get(currentVertexIndex - 1);
                vertex.u = u;
                vertex.v = v;
                vertex.hasUv = true;
            }
        } else {
            delegate.setUv(u, v);
        }
        return this;
    }
    
    @Override
    public VertexConsumer setUv1(int u, int v) {
        return setOverlay((u & 0xFFFF) | ((v & 0xFFFF) << 16));
    }
    
    @Override
    public VertexConsumer setUv2(int u, int v) {
        return setLight((u & 0xFFFF) | ((v & 0xFFFF) << 16));
    }
    
    @Override
    public VertexConsumer setOverlay(int overlay) {
        if (mode == VertexFormat.Mode.QUADS || mode == VertexFormat.Mode.TRIANGLES || mode == VertexFormat.Mode.LINES) {
            if (currentVertexIndex > 0 && currentVertexIndex <= currentPrimitive.size()) {
                VertexData vertex = currentPrimitive.get(currentVertexIndex - 1);
                vertex.overlay = overlay;
                vertex.hasOverlay = true;
            }
        } else {
            delegate.setOverlay(overlay);
        }
        return this;
    }
    
    @Override
    public VertexConsumer setLight(int light) {
        if (mode == VertexFormat.Mode.QUADS || mode == VertexFormat.Mode.TRIANGLES || mode == VertexFormat.Mode.LINES) {
            if (currentVertexIndex > 0 && currentVertexIndex <= currentPrimitive.size()) {
                VertexData vertex = currentPrimitive.get(currentVertexIndex - 1);
                vertex.light = light;
                vertex.hasLight = true;
            }
        } else {
            delegate.setLight(light);
        }
        return this;
    }
    
    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        if (mode == VertexFormat.Mode.QUADS || mode == VertexFormat.Mode.TRIANGLES || mode == VertexFormat.Mode.LINES) {
            if (currentVertexIndex > 0 && currentVertexIndex <= currentPrimitive.size()) {
                VertexData vertex = currentPrimitive.get(currentVertexIndex - 1);
                vertex.nx = x;
                vertex.ny = y;
                vertex.nz = z;
                vertex.hasNormal = true;
            }
        } else {
            delegate.setNormal(x, y, z);
        }
        return this;
    }
    
    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, 
                         int overlay, int light, float nx, float ny, float nz) {
        this.addVertex(x, y, z);
        this.setColor(color);
        this.setUv(u, v);
        this.setOverlay(overlay);
        this.setLight(light);
        this.setNormal(nx, ny, nz);
    }
    
    // Vertex data holder
    private static class VertexData {
        float x, y, z;
        int colorR, colorG, colorB, colorA;
        float u, v;
        int overlay;
        int light;
        float nx, ny, nz;
        
        boolean hasPosition, hasColor, hasUv, hasOverlay, hasLight, hasNormal;
        
        public VertexData copy() {
            VertexData copy = new VertexData();
            copy.x = this.x;
            copy.y = this.y;
            copy.z = this.z;
            copy.colorR = this.colorR;
            copy.colorG = this.colorG;
            copy.colorB = this.colorB;
            copy.colorA = this.colorA;
            copy.u = this.u;
            copy.v = this.v;
            copy.overlay = this.overlay;
            copy.light = this.light;
            copy.nx = this.nx;
            copy.ny = this.ny;
            copy.nz = this.nz;
            copy.hasPosition = this.hasPosition;
            copy.hasColor = this.hasColor;
            copy.hasUv = this.hasUv;
            copy.hasOverlay = this.hasOverlay;
            copy.hasLight = this.hasLight;
            copy.hasNormal = this.hasNormal;
            return copy;
        }
    }
}
