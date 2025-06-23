package de.keksuccino.spiffyhud.customization.elements.vanillalike.contextualbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeContextualBarElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    // Experience bar sprites
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
    private static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");

    // Jump bar sprites
    private static final ResourceLocation JUMP_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_background");
    private static final ResourceLocation JUMP_BAR_COOLDOWN_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_cooldown");
    private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");

    // Locator bar sprites
    private static final ResourceLocation LOCATOR_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("hud/locator_bar_background");
    private static final ResourceLocation LOCATOR_BAR_ARROW_UP = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up");
    private static final ResourceLocation LOCATOR_BAR_ARROW_DOWN = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down");

    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int EXPERIENCE_BAR_DISPLAY_TICKS = 100;

    // Locator bar constants
    private static final int DOT_SIZE = 9;
    private static final int VISIBLE_DEGREE_RANGE = 60;
    private static final int ARROW_WIDTH = 7;
    private static final int ARROW_HEIGHT = 5;

    private final Minecraft minecraft = Minecraft.getInstance();

    // Enum to track which contextual bar to show
    private enum ContextualInfo {
        EMPTY,
        EXPERIENCE,
        LOCATOR,
        JUMPABLE_VEHICLE
    }

    public VanillaLikeContextualBarElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Renders the appropriate contextual bar within this element's bounds.
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

        // Determine which contextual bar to show
        ContextualInfo contextualInfo = getContextualInfoState();

        // Render the appropriate bar
        switch (contextualInfo) {
            case EXPERIENCE:
                renderExperienceBar(graphics, elementX, elementY, elementWidth, elementHeight);
                break;
            case JUMPABLE_VEHICLE:
                renderJumpBar(graphics, elementX, elementY, elementWidth, elementHeight);
                break;
            case LOCATOR:
                renderLocatorBar(graphics, elementX, elementY, elementWidth, elementHeight);
                break;
            default:
                // Render nothing for EMPTY state
                break;
        }
    }

    /**
     * Determines which contextual bar should be displayed based on game state.
     * This mimics the logic from Minecraft 1.21.6's Gui class.
     */
    private ContextualInfo getContextualInfoState() {
        LocalPlayer player = this.minecraft.player;
        if (player == null) return ContextualInfo.EMPTY;

        // In editor mode, always show experience bar for preview
        if (isEditor()) {
            return ContextualInfo.EXPERIENCE;
        }

        boolean hasWaypoints = player.connection.getWaypointManager().hasWaypoints();
        boolean hasJumpableVehicle = player.jumpableVehicle() != null;
        boolean hasExperience = this.minecraft.gameMode.hasExperience();

        if (hasWaypoints) {
            if (hasJumpableVehicle && willPrioritizeJumpInfo()) {
                return ContextualInfo.JUMPABLE_VEHICLE;
            } else {
                return hasExperience && willPrioritizeExperienceInfo() ? ContextualInfo.EXPERIENCE : ContextualInfo.LOCATOR;
            }
        } else if (hasJumpableVehicle) {
            return ContextualInfo.JUMPABLE_VEHICLE;
        } else {
            return hasExperience ? ContextualInfo.EXPERIENCE : ContextualInfo.EMPTY;
        }
    }

    /**
     * Checks if experience info should be prioritized (recently gained experience).
     */
    private boolean willPrioritizeExperienceInfo() {
        LocalPlayer player = this.minecraft.player;
        return player != null && player.experienceDisplayStartTick + EXPERIENCE_BAR_DISPLAY_TICKS > player.tickCount;
    }

    /**
     * Checks if jump info should be prioritized (actively jumping or on cooldown).
     */
    private boolean willPrioritizeJumpInfo() {
        LocalPlayer player = this.minecraft.player;
        if (player == null) return false;
        
        if (player.getJumpRidingScale() > 0.0F) {
            return true;
        }
        
        PlayerRideableJumping jumpableVehicle = player.jumpableVehicle();
        return jumpableVehicle != null && jumpableVehicle.getJumpCooldown() > 0;
    }

    /**
     * Renders the experience bar.
     */
    private void renderExperienceBar(GuiGraphics graphics, int x, int y, int width, int height) {
        var player = this.minecraft.player;
        if (player == null) return;

        // Calculate the color with opacity
        int color = ARGB.color(Math.round(this.opacity * 255f), 255, 255, 255);

        // Only draw the bar if the player requires XP for the next level.
        int xpNeeded = player.getXpNeededForNextLevel();
        if ((xpNeeded > 0) || isEditor()) {
            // Calculate the width (in pixels) of the filled portion.
            int filledBarWidth = (int) (player.experienceProgress * 183.0f);
            if (isEditor()) filledBarWidth = BAR_WIDTH / 2;

            // Draw the empty (background) experience bar.
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                EXPERIENCE_BAR_BACKGROUND_SPRITE, 
                x, 
                y, 
                width, 
                height,
                color
            );

            // Draw the filled part of the bar if any XP has been gained.
            if (filledBarWidth > 0) {
                graphics.enableScissor(x, y, x + filledBarWidth, y + height);
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    EXPERIENCE_BAR_PROGRESS_SPRITE,
                    x,
                    y,
                    width,
                    height,
                    color
                );
                graphics.disableScissor();
            }
        }

        // Render the experience level number if the level is greater than zero.
        if ((player.experienceLevel > 0) || isEditor()) {
            String levelText = String.valueOf(player.experienceLevel);
            if (isEditor()) levelText = "42";

            // Center the level text horizontally within the element.
            int textWidth = this.getFont().width(levelText);
            int textX = x + (width - textWidth) / 2;
            int textY = y - 6;

            // Draw shadow around the text for better readability.
            graphics.drawString(this.getFont(), levelText, textX + 1, textY, SpiffyRenderUtils.colorWithAlpha(0, this.opacity), false);
            graphics.drawString(this.getFont(), levelText, textX - 1, textY, SpiffyRenderUtils.colorWithAlpha(0, this.opacity), false);
            graphics.drawString(this.getFont(), levelText, textX, textY + 1, SpiffyRenderUtils.colorWithAlpha(0, this.opacity), false);
            graphics.drawString(this.getFont(), levelText, textX, textY - 1, SpiffyRenderUtils.colorWithAlpha(0, this.opacity), false);
            // Draw the main level number in yellow (color code 8453920).
            graphics.drawString(this.getFont(), levelText, textX, textY, SpiffyRenderUtils.colorWithAlpha(8453920, this.opacity), false);
        }
    }

    /**
     * Renders the jump bar for rideable entities.
     */
    private void renderJumpBar(GuiGraphics graphics, int x, int y, int width, int height) {
        LocalPlayer player = this.minecraft.player;
        PlayerRideableJumping jumpableVehicle = player != null ? player.jumpableVehicle() : null;
        if (jumpableVehicle == null && !isEditor()) return;

        int color = ARGB.color(Math.round(this.opacity * 255f), 255, 255, 255);

        // Draw the background
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            JUMP_BAR_BACKGROUND_SPRITE,
            x,
            y,
            width,
            height,
            color
        );

        if (isEditor()) {
            // Show half-filled jump bar in editor
            int filledWidth = width / 2;
            graphics.enableScissor(x, y, x + filledWidth, y + height);
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                JUMP_BAR_PROGRESS_SPRITE,
                x,
                y,
                width,
                height,
                color
            );
            graphics.disableScissor();
        } else if (jumpableVehicle.getJumpCooldown() > 0) {
            // Show cooldown overlay
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                JUMP_BAR_COOLDOWN_SPRITE,
                x,
                y,
                width,
                height,
                color
            );
        } else {
            // Show jump progress
            int filledWidth = (int) (player.getJumpRidingScale() * 183.0f);
            if (filledWidth > 0) {
                graphics.enableScissor(x, y, x + filledWidth, y + height);
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    JUMP_BAR_PROGRESS_SPRITE,
                    x,
                    y,
                    width,
                    height,
                    color
                );
                graphics.disableScissor();
            }
        }
    }

    /**
     * Renders the locator bar for waypoints.
     */
    private void renderLocatorBar(GuiGraphics graphics, int x, int y, int width, int height) {
        int color = ARGB.color(Math.round(this.opacity * 255f), 255, 255, 255);

        // Draw the background
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            LOCATOR_BAR_BACKGROUND,
            x,
            y,
            width,
            height,
            color
        );

        if (isEditor()) {
            // Show some example waypoints in editor mode
            int centerX = x + width / 2;
            int dotY = y - 2;
            
            // Draw 3 example waypoint dots
            for (int i = -1; i <= 1; i++) {
                int dotX = centerX + (i * 30) - DOT_SIZE / 2;
                int dotColor = ARGB.multiply(color, ARGB.color(255, 100 + i * 50, 150, 200));
                graphics.fill(dotX, dotY, dotX + DOT_SIZE, dotY + DOT_SIZE, dotColor);
            }
        } else {
            // Render actual waypoints
            Level level = this.minecraft.cameraEntity.level();
            this.minecraft.player.connection.getWaypointManager().forEachWaypoint(this.minecraft.cameraEntity, (waypoint) -> {
                if (!isPlayerWaypoint(waypoint)) {
                    double yawAngle = waypoint.yawAngleToCamera(level, this.minecraft.gameRenderer.getMainCamera());
                    if (yawAngle > -VISIBLE_DEGREE_RANGE - 1 && yawAngle <= VISIBLE_DEGREE_RANGE) {
                        renderWaypoint(graphics, waypoint, x, y, width, yawAngle, level);
                    }
                }
            });
        }
    }

    /**
     * Checks if a waypoint belongs to the player.
     */
    private boolean isPlayerWaypoint(TrackedWaypoint waypoint) {
        return waypoint.id().left()
            .map(uuid -> uuid.equals(this.minecraft.cameraEntity.getUUID()))
            .orElse(false);
    }

    /**
     * Renders a single waypoint on the locator bar.
     */
    private void renderWaypoint(GuiGraphics graphics, TrackedWaypoint waypoint, int barX, int barY, int barWidth, double yawAngle, Level level) {
        int centerX = barX + barWidth / 2;
        Waypoint.Icon icon = waypoint.icon();
        WaypointStyle style = this.minecraft.getWaypointStyles().get(icon.style);
        float distance = Mth.sqrt((float) waypoint.distanceSquared(this.minecraft.cameraEntity));
        ResourceLocation sprite = style.sprite(distance);
        
        // Calculate waypoint color
        int waypointColor = icon.color.orElseGet(() -> 
            waypoint.id().map(
                left -> ARGB.setBrightness(ARGB.color(255, left.hashCode()), 0.9F),
                right -> ARGB.setBrightness(ARGB.color(255, right.hashCode()), 0.9F)
            )
        );
        
        // Apply element opacity
        waypointColor = ARGB.multiply(waypointColor, ARGB.color(Math.round(this.opacity * 255f), 255, 255, 255));
        
        // Calculate position on bar
        int xOffset = (int) (yawAngle * 173.0 / 2.0 / VISIBLE_DEGREE_RANGE);
        int dotX = centerX + xOffset - DOT_SIZE / 2;
        int dotY = barY - 2;
        
        // Draw waypoint dot
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            sprite,
            dotX,
            dotY,
            DOT_SIZE,
            DOT_SIZE,
            waypointColor
        );
        
        // Draw directional arrow if needed
        TrackedWaypoint.PitchDirection pitchDirection = waypoint.pitchDirectionToCamera(level, this.minecraft.gameRenderer);
        if (pitchDirection != TrackedWaypoint.PitchDirection.NONE) {
            int arrowY;
            ResourceLocation arrowSprite;
            
            if (pitchDirection == TrackedWaypoint.PitchDirection.DOWN) {
                arrowY = barY + 6;
                arrowSprite = LOCATOR_BAR_ARROW_DOWN;
            } else {
                arrowY = barY - 6;
                arrowSprite = LOCATOR_BAR_ARROW_UP;
            }
            
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                arrowSprite,
                dotX + 1,
                arrowY,
                ARROW_WIDTH,
                ARROW_HEIGHT,
                waypointColor
            );
        }
    }

    /**
     * Retrieves the current Minecraft font for rendering text.
     *
     * @return The font used by Minecraft.
     */
    private Font getFont() {
        return Minecraft.getInstance().font;
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
