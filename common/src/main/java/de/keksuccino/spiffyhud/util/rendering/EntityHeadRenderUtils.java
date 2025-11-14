package de.keksuccino.spiffyhud.util.rendering;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for rendering a mob's head (or a centered bust) inside a GUI square.
 */
public final class EntityHeadRenderUtils {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final float BASE_DEPTH = 150.0F;
    private static final float HEAD_VERTICAL_FACTOR = 0.65F;
    private static final float HEAD_SCALE_BIAS = 0.7F;

    private EntityHeadRenderUtils() {
    }

    public static boolean renderMobHead(@NotNull GuiGraphics graphics, int left, int top, int size, @Nullable Mob mob, float opacity) {
        if (mob == null || mob.isRemoved()) {
            return false;
        }
        PoseStack pose = graphics.pose();
        graphics.enableScissor(left, top, left + size, top + size);
        pose.pushPose();
        pose.translate(left + size / 2.0F, top + size, BASE_DEPTH);
        float width = Math.max(mob.getBbWidth(), 0.001F);
        float height = Math.max(mob.getBbHeight(), 0.001F);
        float scale = (size / Math.max(width, height)) * HEAD_SCALE_BIAS;
        pose.scale(scale, scale, -scale);
        pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
        pose.translate(0.0F, -height * HEAD_VERTICAL_FACTOR, 0.0F);

        float originalBody = mob.yBodyRot;
        float originalBodyO = mob.yBodyRotO;
        float originalYRot = mob.getYRot();
        float originalYRotO = mob.yRotO;
        float originalXRot = mob.getXRot();
        float originalXRotO = mob.xRotO;
        float originalHead = mob.yHeadRot;
        float originalHeadO = mob.yHeadRotO;

        mob.setYBodyRot(180.0F);
        mob.yBodyRotO = 180.0F;
        mob.setYRot(180.0F);
        mob.yRotO = 180.0F;
        mob.setXRot(0.0F);
        mob.xRotO = 0.0F;
        mob.setYHeadRot(180.0F);
        mob.yHeadRotO = 180.0F;

        Lighting.setupForEntityInInventory();
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, opacity);
        EntityRenderDispatcher dispatcher = MC.getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(mob, 0.0, 0.0, 0.0, 0.0F, 1.0F, pose, graphics.bufferSource(), 15728880));
        graphics.flush();
        dispatcher.setRenderShadow(true);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupFor3DItems();

        mob.setYBodyRot(originalBody);
        mob.yBodyRotO = originalBodyO;
        mob.setYRot(originalYRot);
        mob.yRotO = originalYRotO;
        mob.setXRot(originalXRot);
        mob.xRotO = originalXRotO;
        mob.setYHeadRot(originalHead);
        mob.yHeadRotO = originalHeadO;

        pose.popPose();
        graphics.disableScissor();
        return true;
    }
}
