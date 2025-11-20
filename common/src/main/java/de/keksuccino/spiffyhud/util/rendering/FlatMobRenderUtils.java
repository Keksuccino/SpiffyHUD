package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility for rendering a mob's flat front view inside a GUI square.
 */
public class FlatMobRenderUtils {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final float VIEWPORT_FILL_RATIO = 0.92F; // keep a little padding so tall mobs don't clip
    private static final float HALF = 0.5F;
    private static final Map<Mob, Mob> RENDER_CLONES = new WeakHashMap<>();

    private FlatMobRenderUtils() {
    }

    public static boolean renderFlatMob(@NotNull GuiGraphics graphics, int left, int top, int size, @Nullable Mob mob, float opacity) {
        Mob renderMob = prepareRenderMob(mob);
        if (renderMob == null) {
            return false;
        }
        graphics.enableScissor(left, top, left + size, top + size);
        MobBounds bounds = captureBounds(renderMob);
        float scale = computeScale(bounds, size);
        Vector3f offset = new Vector3f(0.0F, bounds.height * HALF, 0.0F);
        Quaternionf baseRotation = Axis.ZP.rotationDegrees(180.0F);

        float originalBody = renderMob.yBodyRot;
        float originalBodyO = renderMob.yBodyRotO;
        float originalYRot = renderMob.getYRot();
        float originalYRotO = renderMob.yRotO;
        float originalXRot = renderMob.getXRot();
        float originalXRotO = renderMob.xRotO;
        float originalHead = renderMob.yHeadRot;
        float originalHeadO = renderMob.yHeadRotO;

        renderMob.setYBodyRot(180.0F);
        renderMob.yBodyRotO = 180.0F;
        renderMob.setYRot(180.0F);
        renderMob.yRotO = 180.0F;
        renderMob.setXRot(0.0F);
        renderMob.xRotO = 0.0F;
        renderMob.setYHeadRot(180.0F);
        renderMob.yHeadRotO = 180.0F;

        renderEntity(graphics, left, top, size, scale, offset, baseRotation, renderMob);

        renderMob.setYBodyRot(originalBody);
        renderMob.yBodyRotO = originalBodyO;
        renderMob.setYRot(originalYRot);
        renderMob.yRotO = originalYRotO;
        renderMob.setXRot(originalXRot);
        renderMob.xRotO = originalXRotO;
        renderMob.setYHeadRot(originalHead);
        renderMob.yHeadRotO = originalHeadO;

        graphics.disableScissor();
        return true;
    }

    private static void renderEntity(
            @NotNull GuiGraphics graphics,
            int left,
            int top,
            int size,
            float scale,
            @NotNull Vector3f offset,
            @NotNull Quaternionf modelRotation,
            @NotNull Mob mob
    ) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super Mob, ?> renderer = dispatcher.getRenderer(mob);
        EntityRenderState renderState = renderer.createRenderState(mob, 1.0F);
        renderState.lightCoords = 15728880;
        renderState.hitboxesRenderState = null;
        renderState.serverHitboxesRenderState = null;
        renderState.shadowPieces.clear();
        renderState.outlineColor = EntityRenderState.NO_OUTLINE;

        graphics.submitEntityRenderState(
                renderState,
                scale,
                offset,
                modelRotation,
                null,
                left,
                top,
                left + size,
                top + size
        );
    }

    @NotNull
    private static MobBounds captureBounds(@NotNull Mob mob) {
        AABB boundingBox = mob.getBoundingBox();
        double width = boundingBox.maxX - boundingBox.minX;
        double height = boundingBox.maxY - boundingBox.minY;
        double depth = boundingBox.maxZ - boundingBox.minZ;
        float widthF = (float) Math.max(width, 0.001F);
        float heightF = (float) Math.max(height, 0.001F);
        float depthF = (float) Math.max(depth, 0.001F);
        return new MobBounds(heightF, Math.max(widthF, depthF));
    }

    private static float computeScale(@NotNull MobBounds bounds, int size) {
        float dominant = Math.max(bounds.height, bounds.horizontal);
        float available = Math.max(size * VIEWPORT_FILL_RATIO, 1.0F);
        return available / dominant;
    }

    @Nullable
    private static Mob prepareRenderMob(@Nullable Mob source) {
        if (!isRenderableSource(source)) {
            evict(source);
            return null;
        }
        Level level = source.level();
        if (level == null) {
            evict(source);
            return null;
        }
        Mob clone = RENDER_CLONES.get(source);
        if (clone == null || clone.isRemoved() || clone.level() != level) {
            clone = createClone(source);
            if (clone == null) {
                evict(source);
                return null;
            }
            RENDER_CLONES.put(source, clone);
        }
        return clone;
    }

    private static boolean isRenderableSource(@Nullable Mob source) {
        return source != null && source.isAlive() && !source.isRemoved();
    }

    private static void evict(@Nullable Mob source) {
        if (source != null) {
            RENDER_CLONES.remove(source);
        }
    }

    @Nullable
    private static Mob createClone(@NotNull Mob source) {
        Level level = source.level();
        if (level == null) {
            level = MC.level;
        }
        if (level == null) {
            return null;
        }
        Mob copy = (Mob) source.getType().create(level, EntitySpawnReason.LOAD);
        if (copy == null) {
            return null;
        }
        copy.setNoGravity(true);
        copy.setNoAi(true);
        copy.noPhysics = true;
        copy.setSilent(true);
        copyMobData(source, copy);
        return copy;
    }

    private static void copyMobData(@NotNull Mob source, @NotNull Mob target) {
        TagValueOutput tagOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        source.saveWithoutId(tagOutput);
        CompoundTag tag = tagOutput.buildResult();
        tag.putBoolean("PersistenceRequired", false);
        target.load(TagValueInput.create(ProblemReporter.DISCARDING, target.registryAccess(), tag));
        target.setNoGravity(true);
        target.setNoAi(true);
        target.noPhysics = true;
        target.setSilent(true);
    }

    private static final class MobBounds {
        private final float height;
        private final float horizontal;

        private MobBounds(float height, float horizontal) {
            this.height = height;
            this.horizontal = horizontal;
        }
    }

}
