package de.keksuccino.spiffyhud.customization.elements.vanillalike.armor;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaLikePlayerArmorElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    // The sprite resources for armor in 1.21.5
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
    private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");

    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 9;

    private final Minecraft minecraft = Minecraft.getInstance();
    protected int tickCount;

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;

    public VanillaLikePlayerArmorElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        // Update the current tick (if needed for any time-dependent effects).
        this.tickCount = SpiffyUtils.getGuiAccessor().getTickCount_Spiffy();

        // Ensure that the player and level are available.
        if (this.minecraft.player == null || this.minecraft.level == null) {
            return;
        }

        // Retrieve the element's absolute position and size.
        int elementX = this.getAbsoluteX();
        int elementY = this.getAbsoluteY();
        int elementWidth = this.getAbsoluteWidth();
        int elementHeight = this.getAbsoluteHeight();

        // Calculate where the armor bar should be drawn inside the element based on alignment.
        Integer[] alignedPosition = SpiffyAlignment.calculateElementBodyPosition(
                this.spiffyAlignment,
                elementX,
                elementY,
                elementWidth,
                elementHeight,
                BAR_WIDTH,
                BAR_HEIGHT
        );
        int armorBarX = alignedPosition[0];
        int armorBarY = alignedPosition[1];

        // Render the armor bar at the computed aligned position.
        this.renderPlayerArmor(graphics, armorBarX, armorBarY);
    }

    /**
     * Renders the player's armor bar at a given offset.
     * Uses different rendering orders and texture blitting (mirrored or not)
     * based on whether the element alignment is right-based or not.
     *
     * @param graphics the graphics context for rendering
     * @param offsetX  the x-coordinate where the bar should start drawing
     * @param offsetY  the y-coordinate where the bar should start drawing
     */
    private void renderPlayerArmor(GuiGraphics graphics, int offsetX, int offsetY) {
        // Retrieve the current player; if unavailable, skip rendering.
        Player player = this.getCameraPlayer();
        if (player == null) {
            return;
        }

        // Get the player's armor value.
        int armorValue = player.getArmorValue();
        // For editor/demo mode, simulate an armor value.
        if (isEditor()) {
            armorValue = 9;
        }

        // Define constants for the number of icons, spacing between them, and icon size.
        final int iconCount = 10;
        final int iconSpacing = 8;
        final int iconSize = 9;

        // Determine if the current alignment is right-based.
        boolean isRightAligned = spiffyAlignment == SpiffyAlignment.TOP_RIGHT ||
                spiffyAlignment == SpiffyAlignment.MID_RIGHT ||
                spiffyAlignment == SpiffyAlignment.BOTTOM_RIGHT;

        // Calculate color with opacity
        int color = ARGB.color(Math.round(this.opacity * 255f), 255, 255, 255);

        // Loop over each armor slot.
        for (int slot = 0; slot < iconCount; slot++) {
            // Calculate the x-coordinate based on alignment:
            // - For right-based alignment, we render from right to left.
            // - For left or centered alignments, we render from left to right.
            int iconX;
            if (isRightAligned) {
                iconX = offsetX + (iconCount - 1 - slot) * iconSpacing;
            } else {
                iconX = offsetX + slot * iconSpacing;
            }
            int iconY = offsetY;

            // Each slot represents two armor points.
            // Compute the armor segment value that this slot corresponds to.
            int armorSegment = slot * 2 + 1;

            // Determine which icon to draw:
            // - Full armor icon if the segment value is less than the armor value.
            // - Half armor icon if the segment value exactly equals the armor value.
            // - Otherwise, draw the empty armor icon background.
            ResourceLocation armorSprite;
            if (armorSegment < armorValue) {
                // Full armor icon
                armorSprite = ARMOR_FULL_SPRITE;
            } else if (armorSegment == armorValue) {
                // Half armor icon
                armorSprite = ARMOR_HALF_SPRITE;
            } else {
                // Empty armor icon (background)
                armorSprite = ARMOR_EMPTY_SPRITE;
            }
            
            if (isRightAligned) {
                SpiffyRenderUtils.blitSpriteMirrored(
                    graphics,
                    armorSprite,
                    iconX,
                    iconY,
                    iconSize,
                    iconSize,
                    color
                );
            } else {
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    armorSprite,
                    iconX,
                    iconY,
                    iconSize,
                    iconSize,
                    color
                );
            }
        }
    }

    /**
     * Returns the camera player if available.
     */
    @Nullable
    private Player getCameraPlayer() {
        return (Minecraft.getInstance().getCameraEntity() instanceof Player p) ? p : null;
    }

    @Override
    public int getAbsoluteWidth() {
        return BAR_WIDTH;
    }

    @Override
    public int getAbsoluteHeight() {
        return BAR_HEIGHT;
    }
}
