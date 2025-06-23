package de.keksuccino.spiffyhud.util.rendering;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class EntityRenderingUtils {

    private static final Map<EntityRenderState, Float> LIVING_ENTITY_OPACITIES = new HashMap<>();

    public static void submitLivingEntityOpacity(@NotNull EntityRenderState entityRenderState, float opacity) {
        LIVING_ENTITY_OPACITIES.put(entityRenderState, opacity);
    }

    public static float getLivingEntityOpacity(@NotNull EntityRenderState entityRenderState) {
        return LIVING_ENTITY_OPACITIES.getOrDefault(entityRenderState, 1.0F);
    }

    public static void resetLivingEntityOpacities() {
        LIVING_ENTITY_OPACITIES.clear();
    }

}
