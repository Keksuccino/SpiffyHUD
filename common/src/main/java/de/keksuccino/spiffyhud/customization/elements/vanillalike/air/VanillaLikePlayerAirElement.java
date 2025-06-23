package de.keksuccino.spiffyhud.customization.elements.vanillalike.air;

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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaLikePlayerAirElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    // Updated sprite resources for 1.21.5
    private static final ResourceLocation AIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/air");
    private static final ResourceLocation AIR_POPPING_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_bursting");
    private static final ResourceLocation AIR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_empty");

    // Define constants for bubble dimensions.
    private static final int BUBBLE_SIZE = 9;      // The width (and height) of a bubble in pixels.
    private static final int BUBBLE_SPACING = 8;   // The spacing offset between bubbles.
    private static final int AIR_BUBBLE_TOTAL = 10; // Total number of air bubbles

    private static final int TOTAL_BAR_WIDTH = 81;
    private static final int TOTAL_BAR_HEIGHT = 9;
    
    // Constants from Minecraft's Gui class
    private static final int AIR_BUBBLE_POPPING_DURATION = 2;
    private static final int EMPTY_AIR_BUBBLE_DELAY_DURATION = 1;

    private final Minecraft minecraft = Minecraft.getInstance();
    protected int tickCount;

    // Variables to record the calculated air bar size and its drawing origin.
    private int barWidth = 100;
    private int barHeight = 100;

    // Flag to control whether actual drawing should occur.
    private boolean shouldRenderBar = false;

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;

    public VanillaLikePlayerAirElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        // Update the tick counter.
        this.tickCount = SpiffyUtils.getGuiAccessor().getTickCount_Spiffy();

        if (this.minecraft.player == null) return;
        if (this.minecraft.level == null) return;

        // First pass: perform a dry-run (without drawing) to update the bar's dimensions.
        this.shouldRenderBar = false;
        // We call renderPlayerAir with an offset of (0,0) because we only need to update barWidth and barHeight.
        this.renderPlayerAir(graphics, 0, 0);

        // Retrieve the element's absolute position and size.
        int elementAbsX = this.getAbsoluteX();
        int elementAbsY = this.getAbsoluteY();
        int elementWidth = this.getAbsoluteWidth();
        int elementHeight = this.getAbsoluteHeight();

        // Calculate the aligned position for the air bar within this element.
        Integer[] alignedPosition = SpiffyAlignment.calculateElementBodyPosition(
                this.spiffyAlignment,
                elementAbsX,
                elementAbsY,
                elementWidth,
                elementHeight,
                this.barWidth,
                this.barHeight
        );
        int barAbsX = alignedPosition[0];
        int barAbsY = alignedPosition[1];

        // Now render the air bar at the calculated position
        this.shouldRenderBar = true;
        this.renderPlayerAir(graphics, barAbsX, barAbsY);
    }

    /**
     * Renders the player's air bubbles.
     *
     * @param graphics The graphics context.
     * @param offsetX  The absolute X coordinate where the air bar should start.
     * @param offsetY  The absolute Y coordinate where the air bar should start.
     */
    private void renderPlayerAir(GuiGraphics graphics, int offsetX, int offsetY) {
        Player player = getCameraPlayer();
        if (player == null) {
            return;
        }

        // Only render air bubbles if player is underwater or previously had reduced air
        boolean isInWater = player.isEyeInFluid(FluidTags.WATER);
        int maxAir = player.getMaxAirSupply();
        int currentAir = Math.min(player.getAirSupply(), maxAir);
        
        if (!isInWater && currentAir >= maxAir) {
            return;
        }

        // Using the same logic as in Minecraft's Gui class for 1.21.5
        int currentAirBubble = getCurrentAirSupplyBubble(currentAir, maxAir, -2);
        int lastAirBubble = getCurrentAirSupplyBubble(currentAir, maxAir, 0);
        int emptyBubbleStart = 10 - getCurrentAirSupplyBubble(currentAir, maxAir, getEmptyBubbleDelayDuration(currentAir, isInWater));
        boolean bubbleBursting = currentAirBubble != lastAirBubble;

        // Calculate the total width of the air bar (10 bubbles with spacing)
        int totalBarWidth = BUBBLE_SIZE + (AIR_BUBBLE_TOTAL - 1) * BUBBLE_SPACING;

        // Determine whether we need to mirror based on alignment
        boolean shouldMirror = this.spiffyAlignment != SpiffyAlignment.TOP_LEFT && 
                               this.spiffyAlignment != SpiffyAlignment.MID_LEFT && 
                               this.spiffyAlignment != SpiffyAlignment.BOTTOM_LEFT;

        // Calculate the color with opacity
        int color = ARGB.color(Math.round(this.opacity * 255f), 255, 255, 255);

        for (int i = 1; i <= AIR_BUBBLE_TOTAL; i++) {
            int bubbleX = offsetX + (i - 1) * BUBBLE_SPACING;
            int bubbleY = offsetY;

            if (this.shouldRenderBar) {
                ResourceLocation bubbleSprite;
                if (i <= currentAirBubble) {
                    // Full air bubble
                    bubbleSprite = AIR_SPRITE;
                } else if (bubbleBursting && i == lastAirBubble && isInWater) {
                    // Popping/bursting air bubble
                    bubbleSprite = AIR_POPPING_SPRITE;
                } else if (i > AIR_BUBBLE_TOTAL - emptyBubbleStart) {
                    // Empty air bubble
                    bubbleSprite = AIR_EMPTY_SPRITE;
                } else {
                    // No bubble to render
                    continue;
                }
                
                if (shouldMirror) {
                    // Use the mirrored sprite rendering for non-left alignments
                    SpiffyRenderUtils.blitSpriteMirrored(
                        graphics,
                        bubbleSprite, 
                        bubbleX, 
                        bubbleY, 
                        BUBBLE_SIZE, 
                        BUBBLE_SIZE,
                        color
                    );
                } else {
                    // Use normal sprite rendering for left alignments
                    graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        bubbleSprite, 
                        bubbleX, 
                        bubbleY, 
                        BUBBLE_SIZE, 
                        BUBBLE_SIZE, 
                        color
                    );
                }
            }
        }

        // Update the bar's recorded drawing size
        this.barWidth = totalBarWidth;
        this.barHeight = BUBBLE_SIZE;
    }

    /**
     * Calculate the current air supply bubble count based on vanilla logic.
     */
    private static int getCurrentAirSupplyBubble(int currentAirSupply, int maxAirSupply, int offset) {
        return Mth.ceil((float)((currentAirSupply + offset) * 10) / maxAirSupply);
    }

    /**
     * Determine empty bubble delay duration based on vanilla logic.
     */
    private static int getEmptyBubbleDelayDuration(int airSupply, boolean inWater) {
        return airSupply != 0 && inWater ? 1 : 0;
    }

    @Nullable
    private Player getCameraPlayer() {
        return (Minecraft.getInstance().getCameraEntity() instanceof Player p) ? p : null;
    }

    @Override
    public int getAbsoluteWidth() {
        return TOTAL_BAR_WIDTH;
    }

    @Override
    public int getAbsoluteHeight() {
        return TOTAL_BAR_HEIGHT;
    }
}
