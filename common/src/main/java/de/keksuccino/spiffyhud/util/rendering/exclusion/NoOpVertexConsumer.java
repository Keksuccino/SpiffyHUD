package de.keksuccino.spiffyhud.util.rendering.exclusion;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * A no-operation VertexConsumer that discards all vertex data.
 * Useful for completely disabling rendering.
 */
public class NoOpVertexConsumer implements VertexConsumer {
    
    public static final NoOpVertexConsumer INSTANCE = new NoOpVertexConsumer();
    
    private NoOpVertexConsumer() {}
    
    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return this;
    }
    
    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        return this;
    }
    
    @Override
    public VertexConsumer setUv(float u, float v) {
        return this;
    }
    
    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }
    
    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this;
    }
    
    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        return this;
    }
    
    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, 
                         int overlay, int light, float normalX, float normalY, float normalZ) {
        // Do nothing
    }
}
