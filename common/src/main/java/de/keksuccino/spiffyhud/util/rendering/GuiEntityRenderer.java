package de.keksuccino.spiffyhud.util.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GuiEntityRenderer {

    private static final float DEFAULT_FACING = 180.0f; // Default inventory facing
    private static final float MAX_BODY_DELTA = 30.0f;    // Body rotation cap relative to default
    private static final float MAX_HEAD_DELTA = 30.0f;    // Head rotation cap relative to rendered body

    // Instance fields to track rendered rotations.
    // For body rotation:
    private float lastOriginalBodyRotation = Float.NaN;
    private float renderedBodyRotation = DEFAULT_FACING;
    // For head rotation:
    private float lastOriginalHeadRotation = Float.NaN;
    private float renderedHeadRotation = DEFAULT_FACING;

    /**
     * Renders a living entity into a GUI box.
     * <p>
     * The entity is scaled using its bounding dimensions from Pose.STANDING so that its bounding box fits
     * entirely within the target box defined by (posX, posY, boxWidth, boxHeight) and is centered.
     * <p>
     * The rendered body rotation is updated by applying only the incremental difference (delta)
     * from the previous original body rotation value, then clamped to lie within ±30° of DEFAULT_FACING.
     * The head rotation is similarly updated based on the incremental change, but then clamped to within ±30°
     * relative to the rendered body rotation. This prevents either from drifting too far, even if the original
     * rotations continue to increase.
     * After rendering, the entity's original rotations are restored.
     *
     * @param graphics  the GUI graphics context used for rendering
     * @param posX      the x-coordinate of the top-left corner of the target box
     * @param posY      the y-coordinate of the top-left corner of the target box
     * @param boxWidth  the width of the target box (in pixels)
     * @param boxHeight the height of the target box (in pixels)
     * @param opacity   the opacity of the rendered entity (0.0 = fully transparent, 1.0 = fully opaque)
     * @param entity    the LivingEntity to render
     */
    public void renderEntity(GuiGraphics graphics, int posX, int posY, int boxWidth, int boxHeight, float opacity, LivingEntity entity) {

        // Obtain the entity's bounding dimensions using Pose.STANDING.
        EntityDimensions dimensions = entity.getDimensions(Pose.STANDING);
        // Compute scale factors so that the entity's bounding box fits entirely within the target box.
        float scaleFromWidth = (float) boxWidth / dimensions.width();
        float scaleFromHeight = (float) boxHeight / dimensions.height();
        float uniformScale = Math.min(scaleFromWidth, scaleFromHeight);

        // Save original rotation values for later restoration.
        float origYBodyRot = entity.yBodyRot;
        float origYRot = entity.getYRot();
        float origXRot = entity.getXRot();
        float origYHeadRot = entity.yHeadRot;
        float origYHeadRotO = entity.yHeadRotO;

        // ----- Update Body Rotation Using Delta and Clamp -----
        float currentOriginalBody = origYBodyRot;
        if (Float.isNaN(lastOriginalBodyRotation)) {
            lastOriginalBodyRotation = currentOriginalBody;
            renderedBodyRotation = currentOriginalBody;
        }
        // Compute the incremental difference (wrapped properly).
        float bodyDelta = Mth.wrapDegrees(currentOriginalBody - lastOriginalBodyRotation);
        renderedBodyRotation += bodyDelta;
        // Clamp the rendered body rotation to within DEFAULT_FACING ± MAX_BODY_DELTA.
        renderedBodyRotation = Mth.clamp(renderedBodyRotation, DEFAULT_FACING - MAX_BODY_DELTA, DEFAULT_FACING + MAX_BODY_DELTA);
        lastOriginalBodyRotation = currentOriginalBody;
        // Override the entity's body rotation for rendering.
        entity.yBodyRot = renderedBodyRotation;

        // ----- Update Head Rotation Using Delta and Clamp Relative to Body -----
        float currentOriginalHead = origYHeadRot;
        if (Float.isNaN(lastOriginalHeadRotation)) {
            lastOriginalHeadRotation = currentOriginalHead;
            renderedHeadRotation = currentOriginalHead;
        }
        float headDelta = Mth.wrapDegrees(currentOriginalHead - lastOriginalHeadRotation);
        float newRenderedHead = renderedHeadRotation + headDelta;
        // Clamp the head rotation so it stays within ±MAX_HEAD_DELTA of the rendered body rotation.
        newRenderedHead = Mth.clamp(newRenderedHead, renderedBodyRotation - MAX_HEAD_DELTA, renderedBodyRotation + MAX_HEAD_DELTA);
        lastOriginalHeadRotation = currentOriginalHead;
        renderedHeadRotation = newRenderedHead;
        // Override the entity's head rotation.
        entity.yHeadRot = newRenderedHead;
        entity.yHeadRotO = newRenderedHead;
        entity.setYRot(newRenderedHead);

        // ----- Transformation and Rendering (Updated for 1.21.6) -----
        // Create render state for the entity
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0f);
        
        // Set opacity for rendering
        EntityRenderingUtils.submitLivingEntityOpacity(renderState, opacity);
        
        // Create rotation quaternion (180° about Z-axis for facing)
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        
        // Create translation vector - entity pivot is at feet, so we need to center it
        Vector3f translation = new Vector3f(0.0f, entity.getBbHeight() / 2.0f, 0.0f);

        // This fixes the entity being cut off at the edges
        posX -= 10000;
        posY -= 10000;
        boxWidth += 20000;
        boxHeight += 20000;

        // Submit the entity render state using the new 1.21.6 method
        graphics.submitEntityRenderState(
            renderState,
            uniformScale,
            translation,
            rotation,
            null, // No override camera angle
            posX,
            posY,
            posX + boxWidth,
            posY + boxHeight
        );

        // Restore the entity's original rotation values.
        entity.yBodyRot = origYBodyRot;
        entity.setYRot(origYRot);
        entity.setXRot(origXRot);
        entity.yHeadRot = origYHeadRot;
        entity.yHeadRotO = origYHeadRotO;

    }

}
