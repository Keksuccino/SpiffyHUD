package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
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
    private static final int CLONE_REFRESH_INTERVAL_TICKS = 40;
    private static final Map<Mob, CachedMob> RENDER_CLONES = new WeakHashMap<>();

    private FlatMobRenderUtils() {
    }

    public static boolean renderFlatMob(@NotNull GuiGraphics graphics, int left, int top, int size, @Nullable Mob mob, float opacity) {
        Mob renderMob = prepareRenderMob(mob);
        if (renderMob == null) {
            return false;
        }
        graphics.enableScissor(left, top, left + size, top + size);
        float centerX = left + size / 2.0F;
        float centerY = top + size / 2.0F;
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

        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, opacity);
        graphics.pose().pushPose();
        renderEntity(graphics, centerX, centerY, scale, offset, baseRotation, renderMob);
        graphics.flush();
        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupFor3DItems();

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

    private static void renderEntity(@NotNull GuiGraphics graphics, float centerX, float centerY, float scale, @NotNull Vector3f offset,
                                     @NotNull Quaternionf modelRotation, @NotNull Mob mob) {
        graphics.pose().translate(centerX, centerY, 50.0);
        graphics.pose().scale(scale, scale, -scale);
        graphics.pose().translate(offset.x, offset.y, offset.z);
        graphics.pose().mulPose(modelRotation);
        Lighting.setupForEntityInInventory();
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(mob, 0.0, 0.0, 0.0, 0.0F, 1.0F, graphics.pose(), graphics.bufferSource(), 15728880));
        dispatcher.setRenderShadow(true);
        Lighting.setupFor3DItems();
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
        if (source == null || source.isRemoved()) {
            return null;
        }
        Level level = source.level();
        if (level == null) {
            return null;
        }
        CachedMob cached = RENDER_CLONES.get(source);
        if (cached == null || cached.clone.isRemoved() || cached.clone.level() != level) {
            Mob created = createClone(source);
            if (created == null) {
                return null;
            }
            cached = new CachedMob(created, source.tickCount);
            RENDER_CLONES.put(source, cached);
        } else if (shouldRefresh(source, cached)) {
            copyMobData(source, cached.clone);
            cached.lastSyncedTick = source.tickCount;
        }
        return cached.clone;
    }

    private static boolean shouldRefresh(@NotNull Mob source, @NotNull CachedMob cached) {
        return Math.abs(source.tickCount - cached.lastSyncedTick) >= CLONE_REFRESH_INTERVAL_TICKS;
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
        var created = source.getType().create(level);
        if (!(created instanceof Mob copy)) {
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
        CompoundTag tag = new CompoundTag();
        source.saveWithoutId(tag);
        tag.putBoolean("PersistenceRequired", false);
        target.load(tag);
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

    private static final class CachedMob {
        private final @NotNull Mob clone;
        private int lastSyncedTick;

        private CachedMob(@NotNull Mob clone, int lastSyncedTick) {
            this.clone = clone;
            this.lastSyncedTick = lastSyncedTick;
        }
    }

}
