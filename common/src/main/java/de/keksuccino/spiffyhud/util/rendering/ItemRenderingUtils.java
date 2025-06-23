package de.keksuccino.spiffyhud.util.rendering;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemRenderingUtils {

    private static final Map<ItemStackRenderState, Float> ITEM_OPACITIES = new HashMap<>();

    private static float itemOpacity = 1.0F;

    public static void setItemOpacity(float opacity) {
        itemOpacity = opacity;
    }

    public static float getItemOpacity() {
        return itemOpacity;
    }

    public static void resetItemOpacity() {
        setItemOpacity(1.0F);
    }

    public static void submitCurrentItemOpacityFor(@NotNull ItemStackRenderState renderState) {
        ITEM_OPACITIES.put(renderState, itemOpacity);
    }

    public static float getOpacityFor(@NotNull ItemStackRenderState renderState) {
        return ITEM_OPACITIES.getOrDefault(renderState, 1.0F);
    }

    public static void resetSubmittedOpacities() {
        ITEM_OPACITIES.clear();
    }

}
