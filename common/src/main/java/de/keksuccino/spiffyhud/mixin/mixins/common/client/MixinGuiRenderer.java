package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * Mixin to fix an issue in Minecraft's GuiRenderer where all GuiEntityRenderState instances
 * are rendered using the scale of the last instance processed in a frame.
 *
 * <p><b>The Problem:</b></p>
 * Minecraft's {@link GuiRenderer} uses a single, shared instance of a Picture-in-Picture (PIP) renderer
 * (specifically {@link GuiEntityRenderer}) for all entities drawn in the GUI within one frame.
 * This shared renderer uses an internal off-screen texture (a framebuffer). When multiple entities with different scales are queued
 * for rendering (e.g., several {@code PlayerElement} instances), this single PIP renderer processes them sequentially.
 * It renders the first entity to its internal texture with the correct scale, then immediately <b>overwrites that same texture</b>
 * with the next entity at its scale. Consequently, all final draw calls for these entities reference the same texture, which
 * contains the image of the last-rendered entity. This results in all elements appearing with the same scale.
 *
 * <p><b>The Solution:</b></p>
 * This Mixin intercepts the process of fetching the PIP renderer. Instead of allowing the game to use the single shared instance,
 * it provides a unique renderer instance based on the entity's scale.
 * <ul>
 *   <li>A cache ({@code Map<Float, GuiEntityRenderer>}) is introduced to store renderer instances, keyed by their unique scale.</li>
 *   <li>{@link #wrap_List_get_in_preparePictureInPictureState_Spiffy(Map, Object, Operation, PictureInPictureRenderState)} to
 *       intercept the {@code Map.get()} call in {@code GuiRenderer#preparePictureInPictureState}.</li>
 *   <li>If the render state is for an entity, it uses the entity's scale to look up a renderer in our custom cache. If a
 *       renderer for that specific scale doesn't exist, a new one is created on-demand and cached for reuse within the frame.</li>
 *   <li>This ensures that entities with different scales use different renderer instances, each with its own separate internal
 *       texture, thus preventing the overwriting issue.</li>
 *   <li>{@link #before_close_Spiffy(CallbackInfo)} injects into the {@code close()} method to properly dispose of all cached
 *       renderers when the game shuts down or reloads resources, preventing VRAM and memory leaks.</li>
 * </ul>
 */
@Mixin(GuiRenderer.class)
public class MixinGuiRenderer {

    /**
     * Cache of PictureInPictureRenderers for entities, keyed by their scale.
     * This is the core of the fix. Instead of using one shared renderer,
     * we maintain a pool of them, one for each unique scale required in a frame.
     */
    @Unique private final Map<Float, GuiEntityRenderer> pipEntityRenderers_Spiffy = new HashMap<>();

    /**
     * Intercepts the call to `Map.get()` where GuiRenderer fetches the renderer for a Picture-in-Picture state.
     * If the state is a GuiEntityRenderState, we hijack the process. Instead of returning the single shared
     * renderer, we use our own cache keyed by the entity's scale.
     */
    @WrapOperation(method = "preparePictureInPictureState", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private <T extends PictureInPictureRenderState> Object wrap_List_get_in_preparePictureInPictureState_Spiffy(Map<Class<?>, PictureInPictureRenderer<?>> instance, Object key, Operation<Object> original, @Local T pictureInPictureRenderState) {
        // Only act on GuiEntityRenderState, which is what our PlayerElement uses.
        if ((key == GuiEntityRenderState.class) && (pictureInPictureRenderState instanceof GuiEntityRenderState entityRenderState)) {
            // Use the entity's scale as the unique key for our cache.
            float scale = entityRenderState.scale();
            // Check if we already have a renderer for this scale. If not, create and cache it.
            return this.pipEntityRenderers_Spiffy.computeIfAbsent(scale, s -> {
                // We need to create a new instance of the renderer.
                var mc = Minecraft.getInstance();
                return new GuiEntityRenderer(mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher());
            });
        }
        // For all other types of render states, let the original call proceed.
        return original.call(instance, key);
    }

    /**
     * The renderers we create are AutoCloseable and hold GPU resources.
     * We must inject into GuiRenderer's close() method to clean up our cached renderers,
     * preventing resource leaks when the game is closed or resources are reloaded.
     */
    @Inject(method = "close", at = @At("HEAD"))
    private void before_close_Spiffy(CallbackInfo ci) {
        this.pipEntityRenderers_Spiffy.values().forEach(net.minecraft.client.gui.render.pip.GuiEntityRenderer::close);
        this.pipEntityRenderers_Spiffy.clear();
    }

}