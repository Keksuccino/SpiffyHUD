package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility for rendering a mob's flat front view inside a GUI square.
 */
public class FlatMobRenderUtils {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final float BASE_DEPTH = 150.0F;
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
        PoseStack pose = graphics.pose();
        graphics.enableScissor(left, top, left + size, top + size);
        pose.pushPose();
        float centerX = left + size / 2.0F;
        float centerY = top + size / 2.0F;
        pose.translate(centerX, centerY, BASE_DEPTH);
        MobBounds bounds = captureBounds(renderMob);
        float scale = computeScale(bounds, size);
        pose.scale(scale, scale, -scale);
        pose.translate(0.0F, bounds.height * HALF, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(180.0F));

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

        Lighting.setupForEntityInInventory();
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, opacity);
        EntityRenderDispatcher dispatcher = MC.getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(renderMob, 0.0, 0.0, 0.0, 0.0F, 1.0F, pose, graphics.bufferSource(), 15728880));
        graphics.flush();
        dispatcher.setRenderShadow(true);
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

        pose.popPose();
        graphics.disableScissor();
        return true;
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
