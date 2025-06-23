package de.keksuccino.spiffyhud.customization.elements.vanillalike.effects;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.util.SizeAndPositionRecorder;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class VanillaLikeEffectsElement extends AbstractElement {

    // Sprite resources for effect backgrounds in 1.21.6
    private static final ResourceLocation EFFECT_BACKGROUND_AMBIENT_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background_ambient");
    private static final ResourceLocation EFFECT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background");

    private final Minecraft minecraft = Minecraft.getInstance();
    protected int tickCount;

    // These variables record the overall dimensions of the effects bar.
    // They will be updated based on the currently active effects.
    private int barHeight = 60; // default height (will be updated)
    private int barWidth = 100;  // default width (will be updated)

    // When true, the background (and icon inset) is rendered.
    private boolean shouldRenderBar = false;

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;

    public VanillaLikeEffectsElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Gets the sprite ResourceLocation for a mob effect.
     * This follows the same pattern as vanilla 1.21.6.
     */
    public static ResourceLocation getMobEffectSprite(Holder<MobEffect> holder) {
        return holder.unwrapKey()
                .map(ResourceKey::location)
                .map(resourceLocation -> resourceLocation.withPrefix("mob_effect/"))
                .orElseGet(MissingTextureAtlasSprite::getLocation);
    }

    /**
     * Renders the element.
     * <p>
     * This method first calls renderEffects(...) with a dummy offset to update the bar dimensions.
     * Then it calculates the base (top-left) coordinates using the element's absolute position.
     * Vertical alignment is corrected based on the element's height versus the bar's height.
     * Finally, it calls renderEffects(...) with the computed base so that each icon is drawn directly at its absolute position.
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        this.tickCount = SpiffyUtils.getGuiAccessor().getTickCount_Spiffy();

        if (this.minecraft.player == null) return;
        if (this.minecraft.level == null) return;

        // First call renderEffects with (0,0) offset to update the bar dimensions.
        this.shouldRenderBar = false;
        this.renderEffects(graphics, 0, 0);

        // Get the element's absolute position and dimensions.
        int elementAbsX = this.getAbsoluteX();
        int elementAbsY = this.getAbsoluteY();
        int elementHeight = this.getAbsoluteHeight();

        // Adjust vertical base based on alignment.
        // For TOP_* alignments, no vertical offset; for MID_* center the bar vertically;
        // for BOTTOM_* alignments, position the bar at the bottom.
        int baseY = elementAbsY;
        if (this.spiffyAlignment == SpiffyAlignment.MID_LEFT ||
                this.spiffyAlignment == SpiffyAlignment.MID_CENTERED ||
                this.spiffyAlignment == SpiffyAlignment.MID_RIGHT) {
            baseY += (elementHeight - this.barHeight) / 2;
        } else if (this.spiffyAlignment == SpiffyAlignment.BOTTOM_LEFT ||
                this.spiffyAlignment == SpiffyAlignment.BOTTOM_CENTERED ||
                this.spiffyAlignment == SpiffyAlignment.BOTTOM_RIGHT) {
            baseY += elementHeight - this.barHeight;
        }
        // For horizontal base, we simply use the element's absolute X.
        int baseX = elementAbsX;

        // Render effects directly at (baseX, baseY).
        this.shouldRenderBar = true;
        this.renderEffects(graphics, baseX, baseY);
    }

    /**
     * Renders the effect icons directly at absolute positions.
     *
     * <p>This method computes each effect's final absolute position by using a base (baseX, baseY) provided by the render() method
     * and then adding per-icon offsets that depend on the effect's type (beneficial or harmful) and the chosen alignment.
     * Horizontal positions are computed using a dynamically determined element width based on the number of icons.
     * Vertical positions are determined by a base row offset (with harmful effects rendered 26 pixels below beneficial ones).</p>
     *
     * @param graphics the graphics context
     * @param baseX the absolute X coordinate of the element's top-left corner
     * @param baseY the absolute Y coordinate (adjusted for vertical alignment)
     */
    protected void renderEffects(GuiGraphics graphics, int baseX, int baseY) {

        // Retrieve active effects.
        Collection<MobEffectInstance> activeEffects = Objects.requireNonNull(this.minecraft.player).getActiveEffects();

        // In editor mode, simulate some effects.
        if (isEditor()) {
            activeEffects = List.of(
                    new MobEffectInstance(MobEffects.LUCK, 300),
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300),
                    new MobEffectInstance(MobEffects.BAD_OMEN, 300)
            );
        }

        // Do not render if there are no effects
        if (activeEffects.isEmpty()) {
            return;
        }

        // Separate effects into beneficial and harmful lists (sorted in reverse order).
        List<MobEffectInstance> beneficialEffects = new ArrayList<>();
        List<MobEffectInstance> harmfulEffects = new ArrayList<>();
        for (MobEffectInstance effectInstance : Ordering.natural().reverse().sortedCopy(activeEffects)) {
            if (!effectInstance.showIcon()) continue;
            if (effectInstance.getEffect().value().isBeneficial()) {
                beneficialEffects.add(effectInstance);
            } else {
                harmfulEffects.add(effectInstance);
            }
        }

        // Prepare a list of tasks to render each effect icon.
        ArrayList<Runnable> renderTasks = Lists.newArrayListWithExpectedSize(activeEffects.size());

        // Recorder to capture overall bar dimensions.
        SizeAndPositionRecorder recorder = new SizeAndPositionRecorder();
        recorder.setWidthOffset(24);
        recorder.setHeightOffset(24);

        // Determine horizontal alignment.
        boolean isLeftAligned = (this.spiffyAlignment == SpiffyAlignment.TOP_LEFT ||
                this.spiffyAlignment == SpiffyAlignment.MID_LEFT ||
                this.spiffyAlignment == SpiffyAlignment.BOTTOM_LEFT);
        boolean isRightAligned = (this.spiffyAlignment == SpiffyAlignment.TOP_RIGHT ||
                this.spiffyAlignment == SpiffyAlignment.MID_RIGHT ||
                this.spiffyAlignment == SpiffyAlignment.BOTTOM_RIGHT);

        // Compute vertical base for the icon rows.
        int baseRowOffset = 1;
        if (this.minecraft.isDemo()) {
            baseRowOffset += 15;
        }
        int beneficialRowY = baseY + baseRowOffset;
        int harmfulRowY = baseY + baseRowOffset + 26;

        // Compute required width based on number of icons.
        int beneficialIconCount = beneficialEffects.size();
        int harmfulIconCount = harmfulEffects.size();
        int requiredWidth = Math.max(beneficialIconCount, harmfulIconCount) * 25;

        // Use the computed width for horizontal layout.
        int elementWidth = requiredWidth;

        // Calculate starting X positions for beneficial effects.
        int beneficialStartX;
        if (isLeftAligned) {
            beneficialStartX = 0;
        } else if (isRightAligned) {
            beneficialStartX = elementWidth - beneficialIconCount * 25;
        } else { // center-aligned
            beneficialStartX = (elementWidth - beneficialIconCount * 25) / 2;
        }

        // Calculate starting X positions for harmful effects.
        int harmfulStartX;
        if (isLeftAligned) {
            harmfulStartX = 0;
        } else if (isRightAligned) {
            harmfulStartX = elementWidth - harmfulIconCount * 25;
        } else { // center-aligned
            harmfulStartX = (elementWidth - harmfulIconCount * 25) / 2;
        }

        // Process beneficial effects.
        for (int i = 0; i < beneficialIconCount; i++) {
            MobEffectInstance effectInstance = beneficialEffects.get(i);
            int relativeX = beneficialStartX + 25 * i;
            int finalIconX = baseX + relativeX;
            float iconAlpha = 1.0f;
            
            if (this.shouldRenderBar) {
                // Render background for the effect icon.
                if (effectInstance.isAmbient()) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_AMBIENT_SPRITE, finalIconX, beneficialRowY, 24, 24, ARGB.white(this.opacity));
                } else {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SPRITE, finalIconX, beneficialRowY, 24, 24, ARGB.white(this.opacity));
                    if (effectInstance.endsWithin(200)) {
                        int duration = effectInstance.getDuration();
                        int fadeFactor = 10 - duration / 20;
                        iconAlpha = Mth.clamp((float) duration / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f)
                                + Mth.cos((float) duration * (float) Math.PI / 5.0f)
                                * Mth.clamp((float) fadeFactor / 10.0f * 0.25f, 0.0f, 0.25f);
                        iconAlpha = Mth.clamp(iconAlpha, 0.0f, 1.0f);
                    }
                }
            }
            
            Holder<MobEffect> effectHolder = effectInstance.getEffect();
            ResourceLocation effectSprite = getMobEffectSprite(effectHolder);
            recorder.updateX(finalIconX);
            recorder.updateY(beneficialRowY);
            
            final int iconX = finalIconX;
            final int iconY = beneficialRowY;
            final float finalIconAlpha = iconAlpha;
            
            if (this.shouldRenderBar) {
                renderTasks.add(() -> {
                    // Render the effect icon with proper alpha
                    graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        effectSprite,
                        iconX + 3,
                        iconY + 3,
                        18,
                        18,
                        ARGB.white(finalIconAlpha * this.opacity)
                    );
                });
            }
        }

        // Process harmful effects.
        for (int i = 0; i < harmfulIconCount; i++) {
            MobEffectInstance effectInstance = harmfulEffects.get(i);
            int relativeX = harmfulStartX + 25 * i;
            int finalIconX = baseX + relativeX;
            float iconAlpha = 1.0f;
            
            if (this.shouldRenderBar) {
                if (effectInstance.isAmbient()) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_AMBIENT_SPRITE, finalIconX, harmfulRowY, 24, 24, ARGB.white(this.opacity));
                } else {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SPRITE, finalIconX, harmfulRowY, 24, 24, ARGB.white(this.opacity));
                    if (effectInstance.endsWithin(200)) {
                        int duration = effectInstance.getDuration();
                        int fadeFactor = 10 - duration / 20;
                        iconAlpha = Mth.clamp((float) duration / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f)
                                + Mth.cos((float) duration * (float) Math.PI / 5.0f)
                                * Mth.clamp((float) fadeFactor / 10.0f * 0.25f, 0.0f, 0.25f);
                        iconAlpha = Mth.clamp(iconAlpha, 0.0f, 1.0f);
                    }
                }
            }
            
            Holder<MobEffect> effectHolder = effectInstance.getEffect();
            ResourceLocation effectSprite = getMobEffectSprite(effectHolder);
            recorder.updateX(finalIconX);
            recorder.updateY(harmfulRowY);
            
            final int iconX = finalIconX;
            final int iconY = harmfulRowY;
            final float finalIconAlpha = iconAlpha;
            
            if (this.shouldRenderBar) {
                renderTasks.add(() -> {
                    // Render the effect icon with proper alpha
                    graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        effectSprite,
                        iconX + 3,
                        iconY + 3,
                        18,
                        18,
                        ARGB.white(finalIconAlpha * this.opacity)
                    );
                });
            }
        }

        // Update overall bar dimensions from the recorder and our computed width.
        this.barHeight = recorder.getHeight();
        this.barWidth = requiredWidth;

        // Execute all rendering tasks.
        renderTasks.forEach(Runnable::run);
    }

    @Override
    public int getAbsoluteWidth() {
        return this.barWidth;
    }

    @Override
    public int getAbsoluteHeight() {
        return this.barHeight;
    }
}
