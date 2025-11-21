package de.keksuccino.spiffyhud.customization.elements.vanillalike.playerhealth;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.util.SizeAndPositionRecorder;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaLikePlayerHealthElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    // Sprite resources for hearts in 1.21.1
    private static final ResourceLocation HEART_CONTAINER_SPRITE = new ResourceLocation("hud/heart/container");
    private static final ResourceLocation HEART_CONTAINER_BLINKING_SPRITE = new ResourceLocation("hud/heart/container_blinking");
    private static final ResourceLocation HEART_CONTAINER_HARDCORE_SPRITE = new ResourceLocation("hud/heart/container_hardcore");
    private static final ResourceLocation HEART_CONTAINER_HARDCORE_BLINKING_SPRITE = new ResourceLocation("hud/heart/container_hardcore_blinking");

    private final Minecraft minecraft = Minecraft.getInstance();
    protected final RandomSource random = RandomSource.create();
    protected int lastHealth;
    protected int displayHealth;
    protected long lastHealthTime;
    protected long healthBlinkTime;
    protected int tickCount;

    // Recorded dimensions and position for the hearts bar.
    private int barWidth = 100;
    private int barHeight = 100;

    // When true, the hearts are drawn; when false, only the bar bounds are recorded.
    private boolean shouldRenderBar = false;
    public boolean isUsedAsDummy = false;

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;

    public VanillaLikePlayerHealthElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Renders the hearts bar directly using the element's absolute position and size.
     * This method performs two passes:
     * 1. A recording pass (with shouldRenderBar==false) using a local origin (0,0)
     *    so that the bar's dimensions are captured.
     * 2. An actual drawing pass (with shouldRenderBar==true) at the computed aligned
     *    absolute coordinates (using getAbsoluteX/Y/Width/Height).
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        // Update tick counter for animations.
        this.tickCount = SpiffyUtils.getGuiAccessor().getTickCount_Spiffy();

        if (this.minecraft.player == null) return;
        if (this.minecraft.level == null) return;

        // === First Pass: Record Bar Dimensions ===
        this.shouldRenderBar = false;
        this.renderPlayerHealthInternal(graphics, 0, 0);

        // === Compute the Aligned Absolute Position for the Bar ===
        int elementAbsX = this.getAbsoluteX();
        int elementAbsY = this.getAbsoluteY();
        int elementWidth = this.getAbsoluteWidth();
        int elementHeight = this.getAbsoluteHeight();
        Integer[] alignedPosition = SpiffyAlignment.calculateElementBodyPosition(this.spiffyAlignment, elementAbsX, elementAbsY, elementWidth, elementHeight, this.barWidth, this.barHeight);
        int alignedBarX = alignedPosition[0];
        int alignedBarY = alignedPosition[1];

        RenderSystem.enableBlend();

        // === Second Pass: Draw the Hearts Bar at the Computed Absolute Position ===
        this.shouldRenderBar = true;
        this.renderPlayerHealthInternal(graphics, alignedBarX, alignedBarY);

        RenderingUtils.resetShaderColor(graphics);

    }

    /**
     * Renders (or records) the player's hearts bar.
     * @param graphics The graphics context.
     * @param originX  The absolute x-coordinate where the hearts bar should be drawn.
     * @param originY  The absolute y-coordinate where the hearts bar should be drawn.
     *
     * When shouldRenderBar is false, this method only records the bar's bounds.
     */
    private void renderPlayerHealthInternal(GuiGraphics graphics, int originX, int originY) {

        Player player = this.getCameraPlayer();
        if (player == null) {
            return;
        }

        // Enable blending and set the shader color with the desired opacity.
        RenderSystem.enableBlend();
        graphics.setColor(1.0f, 1.0f, 1.0f, this.opacity);

        // Compute current health (rounded up) and determine blink status.
        int currentHealthCeil = Mth.ceil(player.getHealth());
        boolean heartBlink = (this.healthBlinkTime > (long)this.tickCount) &&
                (((this.healthBlinkTime - (long)this.tickCount) / 3L) % 2L == 1L);
        long currentTime = Util.getMillis();

        if (currentHealthCeil < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = currentTime;
            this.healthBlinkTime = this.tickCount + 20;
        } else if (currentHealthCeil > this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = currentTime;
            this.healthBlinkTime = this.tickCount + 10;
        }
        if (currentTime - this.lastHealthTime > 1000L) {
            this.displayHealth = currentHealthCeil;
            this.lastHealthTime = currentTime;
        }
        this.lastHealth = currentHealthCeil;

        // Food data is retrieved for compatibility.
        FoodData foodData = player.getFoodData();

        // Base drawing origin.
        int baseX = originX;
        int baseY = originY;

        // Compute max health and absorption values.
        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                (float) Math.max(this.displayHealth, currentHealthCeil));
        int absorptionHalfHearts = Mth.ceil(player.getAbsorptionAmount());
        int totalHealthHearts = Mth.ceil(maxHealth / 2.0f);
        int totalHearts = totalHealthHearts + toFullHearts(absorptionHalfHearts);
        int displayedHealth = this.displayHealth;

        // --- Editor Preview Override ---
        if (isEditor()) {
            maxHealth = 40;
            currentHealthCeil = 9;
            displayedHealth = 9;
            absorptionHalfHearts = 5;
            totalHealthHearts = Mth.ceil(maxHealth / 2.0f);
            totalHearts = totalHealthHearts + toFullHearts(absorptionHalfHearts);
        }
        if (this.isUsedAsDummy) {
            maxHealth = 20;
            currentHealthCeil = 9;
            displayedHealth = 9;
            absorptionHalfHearts = 0;
            totalHealthHearts = Mth.ceil(maxHealth / 2.0f);
            totalHearts = totalHealthHearts + toFullHearts(absorptionHalfHearts);
        }

        // Determine number of slots per row (max 10) and total number of rows.
        final int fullRowSlots = 10;
        int numRows = (totalHearts + fullRowSlots - 1) / fullRowSlots;
        // Compute row spacing (mimicking vanilla spacing).
        int rowSpacing = Math.max(10 - (numRows - 2), 3);

        // Determine regeneration effect offset.
        int regenHeartIndex = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            regenHeartIndex = this.tickCount % Mth.ceil(maxHealth + 5.0f);
        }

        // Determine heart type.
        Gui.HeartType baseHeartType = Gui.HeartType.forPlayer(player);
        boolean isHardcore = player.level().getLevelData().isHardcore();

        // Recorder to capture the bounds of the hearts bar.
        SizeAndPositionRecorder recorder = new SizeAndPositionRecorder();
        recorder.setWidthOffset(9);
        recorder.setHeightOffset(9);

        /*
         * Loop over heart slots.
         * We use a single index m from 0 to totalHearts-1.
         * We derive a raw row as (m / fullRowSlots). By default we flip rows so that row 0 is at the top.
         * However, for top-based alignments we reverse the row order so that extra rows (absorption or empty hearts)
         * are rendered last.
         */
        for (int m = totalHearts - 1; m >= 0; m--) {
            int rawRow = m / fullRowSlots;
            int row;
            if (this.spiffyAlignment == SpiffyAlignment.TOP_LEFT || this.spiffyAlignment == SpiffyAlignment.TOP_CENTERED || this.spiffyAlignment == SpiffyAlignment.TOP_RIGHT) {
                row = rawRow;
            } else {
                row = numRows - 1 - rawRow;
            }
            // For the incomplete (top) row, determine how many hearts are in that row.
            int heartsInRow = (rawRow == (totalHearts - 1) / fullRowSlots) ?
                    totalHearts - rawRow * fullRowSlots : fullRowSlots;
            // Compute alignment offset for centered alignment only (left and right alignments need no offset)
            int alignmentOffset = 0;
            if (rawRow == (totalHearts - 1) / fullRowSlots) {
                if (this.spiffyAlignment == SpiffyAlignment.TOP_CENTERED || this.spiffyAlignment == SpiffyAlignment.MID_CENTERED || this.spiffyAlignment == SpiffyAlignment.BOTTOM_CENTERED) {
                    alignmentOffset = (fullRowSlots - heartsInRow) / 2;
                }
            }
            int col;
            // For right-based orientations, reverse the column index so hearts render from the right.
            if (this.spiffyAlignment == SpiffyAlignment.TOP_RIGHT || this.spiffyAlignment == SpiffyAlignment.MID_RIGHT || this.spiffyAlignment == SpiffyAlignment.BOTTOM_RIGHT) {
                col = fullRowSlots - 1 - (m % fullRowSlots);
            } else {
                col = m % fullRowSlots;
            }
            int effectiveCol = col + alignmentOffset;
            int heartX = baseX + effectiveCol * 8;
            int heartY = baseY + row * rowSpacing;

            // Add slight random offset for very low health.
            if (currentHealthCeil + absorptionHalfHearts <= 4) {
                heartY += this.random.nextInt(2);
            }
            // Adjust for regeneration effect.
            if (m < totalHealthHearts && m == regenHeartIndex) {
                heartY -= 2;
            }

            // Record heart position.
            recorder.updateX(heartX);
            recorder.updateY(heartY);

            // Render the container (empty heart) for every slot.
            if (this.shouldRenderBar) {
                renderEmptyHeart(graphics, heartX, heartY, heartBlink, isHardcore);
            }

            int heartValue = m * 2; // Each heart slot represents 2 health points.

            // Render absorption hearts (should be drawn in the top row).
            if (m >= totalHealthHearts) {
                int absorptionIndex = heartValue - totalHealthHearts * 2;
                if (absorptionIndex < absorptionHalfHearts) {
                    boolean isLastAbsorption = (absorptionIndex + 1 == absorptionHalfHearts);
                    if (this.shouldRenderBar) {
                        renderHeart(graphics,
                                baseHeartType == Gui.HeartType.WITHERED ? baseHeartType : Gui.HeartType.ABSORBING,
                                heartX, heartY, false, isHardcore, isLastAbsorption);
                    }
                }
            }
            // Render highlighted heart if blink effect is active.
            if (heartBlink && heartValue < displayedHealth) {
                boolean isLastHighlight = (heartValue + 1 == displayedHealth);
                if (this.shouldRenderBar) {
                    renderHeart(graphics, baseHeartType, heartX, heartY, true, isHardcore, isLastHighlight);
                }
            }
            // Render normal (filled) heart if player's health covers this slot.
            if (heartValue < currentHealthCeil) {
                boolean isLastHeart = (heartValue + 1 == currentHealthCeil);
                if (this.shouldRenderBar) {
                    renderHeart(graphics, baseHeartType, heartX, heartY, false, isHardcore, isLastHeart);
                }
            }
        }

        // When in recording mode, update the stored bar bounds.
        if (!this.shouldRenderBar && recorder.isUpdated()) {
            this.barWidth = recorder.getWidth();
            this.barHeight = recorder.getHeight();
        } else if (!this.shouldRenderBar) {
            this.barWidth = 1;
            this.barHeight = 9;
        }

        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Renders a heart container (empty heart)
     */
    private void renderEmptyHeart(GuiGraphics graphics, int x, int y, boolean blinking, boolean hardcore) {
        ResourceLocation spriteLocation;

        if (hardcore) {
            spriteLocation = blinking ? HEART_CONTAINER_HARDCORE_BLINKING_SPRITE : HEART_CONTAINER_HARDCORE_SPRITE;
        } else {
            spriteLocation = blinking ? HEART_CONTAINER_BLINKING_SPRITE : HEART_CONTAINER_SPRITE;
        }

        if (this.spiffyAlignment == SpiffyAlignment.TOP_RIGHT || this.spiffyAlignment == SpiffyAlignment.MID_RIGHT || this.spiffyAlignment == SpiffyAlignment.BOTTOM_RIGHT) {
            SpiffyRenderUtils.blitSpriteMirrored(graphics, spriteLocation, x, y, 9, 9);
        } else {
            graphics.blitSprite(spriteLocation, x, y, 9, 9);
        }
    }

    /**
     * Renders a single heart icon.
     *
     * @param graphics    The graphics context.
     * @param heartType   The type of heart icon to render.
     * @param x           The x-coordinate of the heart.
     * @param y           The y-coordinate of the heart.
     * @param blinking    Whether the heart should be blinking.
     * @param hardcore    Whether to use hardcore sprites.
     * @param halfHeart   Whether to render a half heart.
     */
    private void renderHeart(GuiGraphics graphics, Gui.HeartType heartType, int x, int y, boolean blinking, boolean hardcore, boolean halfHeart) {
        ResourceLocation spriteLocation = heartType.getSprite(hardcore, halfHeart, blinking);

        if (this.spiffyAlignment == SpiffyAlignment.TOP_RIGHT || this.spiffyAlignment == SpiffyAlignment.MID_RIGHT || this.spiffyAlignment == SpiffyAlignment.BOTTOM_RIGHT) {
            SpiffyRenderUtils.blitSpriteMirrored(graphics, spriteLocation, x, y, 9, 9);
        } else {
            graphics.blitSprite(spriteLocation, x, y, 9, 9);
        }
    }

    private static int toFullHearts(int halfHearts) {
        return (halfHearts + 1) / 2;
    }

    @Nullable
    private Player getCameraPlayer() {
        return (Minecraft.getInstance().getCameraEntity() instanceof Player p) ? p : null;
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