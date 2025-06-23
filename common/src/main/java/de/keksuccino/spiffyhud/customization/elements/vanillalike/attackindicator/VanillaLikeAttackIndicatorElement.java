package de.keksuccino.spiffyhud.customization.elements.vanillalike.attackindicator;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeAttackIndicatorElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    // Sprite resources for attack indicators in 1.21.1
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_full");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_background");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_progress");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_background");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_progress");

    private final Minecraft minecraft = Minecraft.getInstance();

    public boolean isHotbar = false;

    public VanillaLikeAttackIndicatorElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Renders the experience bar within this element's bounds.
     *
     * @param graphics The graphics context.
     * @param mouseX   The current mouse X-coordinate.
     * @param mouseY   The current mouse Y-coordinate.
     * @param partial  Partial ticks.
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        // Do nothing if the player or level is missing.
        if (this.minecraft.player == null || this.minecraft.level == null) {
            return;
        }

        // Get the absolute position and dimensions of this element.
        int elementX = this.getAbsoluteX();
        int elementY = this.getAbsoluteY();
        int elementWidth = this.getAbsoluteWidth();
        int elementHeight = this.getAbsoluteHeight();

        // Render the experience bar using the element's own bounds.
        this.renderAttackIndicator(graphics, this.isHotbar, elementX, elementY, elementWidth, elementHeight);

    }

    /**
     * Renders the attack indicator in either crosshair or hotbar mode, scaled to the specified dimensions.
     *
     * @param graphics the GuiGraphics object used for rendering
     * @param isHotbar whether to render the hotbar version (true) or crosshair version (false)
     * @param x the x position to render at
     * @param y the y position to render at
     * @param width the width to render the indicator at
     * @param height the height to render the indicator at
     */
    public void renderAttackIndicator(GuiGraphics graphics, boolean isHotbar, int x, int y, int width, int height) {

        float attackStrength = this.minecraft.player.getAttackStrengthScale(0.0f);
        if (isEditor()) attackStrength = 0.5f;

        if (isHotbar) {
            // Render hotbar version (only shown when not fully charged)
            if (attackStrength < 1.0f) {
                // First render the background
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, x, y, width, height, ARGB.white(this.opacity));

                // Then render the foreground based on attack strength
                int fillHeight = (int)(height * attackStrength);
                if (fillHeight > 0) {
                    // For hotbar indicator, the original sprite is 18x18 and fills from bottom
                    SpiffyRenderUtils.blitSprite(graphics, HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE,
                            width, height,  // Full widget size
                            0, height - fillHeight,  // Start from the bottom
                            x, y + height - fillHeight,  // Position at the bottom
                            width, fillHeight,  // Fill portion from bottom
                            ARGB.white(this.opacity)
                    );
                }
            }
        } else {
            // Render crosshair version
            boolean readyToAttack = false;
            if (this.minecraft.crosshairPickEntity != null &&
                    this.minecraft.crosshairPickEntity instanceof LivingEntity &&
                    attackStrength >= 1.0f) {
                readyToAttack = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0f;
                readyToAttack &= this.minecraft.crosshairPickEntity.isAlive();
            }

            if (readyToAttack) {
                // Render the "ready to attack" indicator
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, x, y, width, height, ARGB.white(this.opacity));
            } else if (attackStrength < 1.0f) {
                // Determine a proper height for the progress bar (maintaining aspect ratio)
                int barHeight = height;
                if (width / height > 4) { // Original aspect ratio is 16:4 or 4:1
                    barHeight = Math.max(1, width / 4);
                }

                // First render the background bar
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, x, y, width, barHeight, ARGB.white(this.opacity));

                // Then render the foreground based on attack strength
                int fillWidth = (int)(width * attackStrength);
                if (fillWidth > 0) {
                    // For crosshair indicator, the original sprite is 16x4 and fills from left
                    SpiffyRenderUtils.blitSprite(graphics, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE,
                            width, barHeight,  // Full widget size
                            0, 0,  // Start from top-left
                            x, y,  // Position at top-left
                            fillWidth, barHeight,  // Fill portion from left
                            ARGB.white(this.opacity)
                    );
                }
            }
        }

    }

}
