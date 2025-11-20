package de.keksuccino.spiffyhud.customization.elements.vanillalike.scoreboard;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.util.ARGB;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.*;
import java.util.Comparator;

public class VanillaLikeScoreboardElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SPACER = ": ";
    private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value)
            .reversed()
            .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);

    // Minecraft instance for accessing game state
    private final Minecraft minecraft = Minecraft.getInstance();

    // These fields will hold the computed sidebar dimensions and original position
    private int sidebarWidth = 100;
    private int sidebarHeight = 100;
    private int sidebarOriginalX = 0;
    private int sidebarOriginalY = 0;

    // Flag to indicate if the sidebar should actually be drawn
    private boolean renderSidebar = false;

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;
    @Nullable
    public DrawableColor customTitleBackgroundColor = null;
    @Nullable
    public DrawableColor customLineBackgroundColor = null;

    public VanillaLikeScoreboardElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Main render method.
     *
     * First, it calls renderScoreboard (with no offset) to update the sidebar's dimensions and original position.
     * Then, it calculates an aligned position based on the element's absolute bounds and renders the scoreboard
     * with an offset so that it appears inside the element.
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        if (this.minecraft.player == null || this.minecraft.level == null) {
            return;
        }

        // --- Update sidebar dimensions (without applying any offset) ---
        // This call sets sidebarWidth, sidebarHeight, sidebarOriginalX, and sidebarOriginalY.
        this.renderSidebar = false;
        this.renderScoreboard(graphics, 0, 0, false);

        // Calculate aligned position based on the element's absolute bounds
        int elementX = this.getAbsoluteX();
        int elementY = this.getAbsoluteY();
        Integer[] alignedPosition = SpiffyAlignment.calculateElementBodyPosition(
                this.spiffyAlignment,
                elementX,
                elementY,
                this.getAbsoluteWidth(),
                this.getAbsoluteHeight(),
                this.sidebarWidth,
                this.sidebarHeight
        );

        // Compute the offset required so that the sidebar renders at the correct aligned position.
        int offsetX = alignedPosition[0] - this.sidebarOriginalX;
        int offsetY = alignedPosition[1] - this.sidebarOriginalY;

        // Render the scoreboard applying the offset to all drawing coordinates.
        this.renderSidebar = true;
        this.renderScoreboard(graphics, offsetX, offsetY, true);
    }

    /**
     * Renders the scoreboard.
     *
     * @param graphics    The graphics context.
     * @param offsetX     Horizontal offset to add to drawing coordinates.
     * @param offsetY     Vertical offset to add to drawing coordinates.
     * @param applyOffset If true, the computed offset is applied while drawing.
     */
    private void renderScoreboard(GuiGraphics graphics, int offsetX, int offsetY, boolean applyOffset) {
        Scoreboard scoreboard = this.minecraft.level.getScoreboard();
        Objective objective = null;
        PlayerTeam playerTeam = scoreboard.getPlayersTeam(this.minecraft.player.getScoreboardName());
        
        if (playerTeam != null) {
            DisplaySlot displaySlot = DisplaySlot.teamColorToSlot(playerTeam.getColor());
            if (displaySlot != null) {
                objective = scoreboard.getDisplayObjective(displaySlot);
            }
        }
        
        // Fallback to SIDEBAR display objective if necessary
        Objective objectiveToRender = (objective != null) ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        
        // In editor mode, use a dummy objective
        if (isEditor()) {
            objectiveToRender = new DummyObjective(scoreboard);
        }
        
        if (objectiveToRender != null) {
            displayScoreboardSidebar(graphics, objectiveToRender, offsetX, offsetY, applyOffset);
        }
    }

    /**
     * Record class to hold display entry information
     */
    private record DisplayEntry(Component name, Component score, int scoreWidth) {}

    /**
     * Renders the scoreboard sidebar.
     *
     * All drawing coordinates (for background, score lines, and title) are adjusted by (offsetX, offsetY)
     * if applyOffset is true. This ensures that the sidebar is drawn inside the element's bounds.
     *
     * @param guiGraphics The graphics context.
     * @param objective   The scoreboard objective to render.
     * @param offsetX     Horizontal offset to apply.
     * @param offsetY     Vertical offset to apply.
     * @param applyOffset If true, the offset is added to all drawing coordinates.
     */
    private void displayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective, int offsetX, int offsetY, boolean applyOffset) {
        Scoreboard scoreboard = objective.getScoreboard();
        
        // List to hold display entries
        DisplayEntry[] displayEntries;
        Component title = objective.getDisplayName();
        
        // In editor mode, use dummy values
        if (isEditor()) {
            title = Component.translatable("spiffyhud.elements.dummy.scoreboard_sidebar.title").withStyle(ChatFormatting.BOLD);
            String dummyLineText = I18n.get("spiffyhud.elements.dummy.scoreboard_sidebar.line");
            displayEntries = new DisplayEntry[6];
            for (int i = 0; i < 6; i++) {
                Component name = Component.literal(dummyLineText);
                Component score = Component.literal("" + i);
                displayEntries[i] = new DisplayEntry(name, score, this.getFont().width(score));
            }
        } else {
            // Get player scores using the 1.21.5 API
            displayEntries = scoreboard.listPlayerScores(objective)
                .stream()
                .filter(entry -> !entry.isHidden())
                .sorted(SCORE_DISPLAY_ORDER)
                .limit(15)
                .map(entry -> {
                    PlayerTeam team = scoreboard.getPlayersTeam(entry.owner());
                    Component name = entry.ownerName();
                    Component formattedName = PlayerTeam.formatNameForTeam(team, name);
                    Component scoreText = entry.formatValue(objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT));
                    int scoreWidth = this.getFont().width(scoreText);
                    return new DisplayEntry(formattedName, scoreText, scoreWidth);
                })
                .toArray(DisplayEntry[]::new);
        }

        // Determine the maximum width required by the title or any score entry.
        int titleWidth = this.getFont().width(title);
        int maxEntryWidth = titleWidth;
        int spacerWidth = this.getFont().width(SPACER);

        for (DisplayEntry entry : displayEntries) {
            int entryWidth = this.getFont().width(entry.name) + (entry.scoreWidth > 0 ? spacerWidth + entry.scoreWidth : 0);
            maxEntryWidth = Math.max(maxEntryWidth, entryWidth);
        }

        // Compute base positions using screen dimensions.
        int numberOfLines = displayEntries.length;
        int lineHeight = 9; // Standard line height for scoreboard
        int totalLineHeight = numberOfLines * lineHeight;
        int baseY = getScreenHeight() / 2 + totalLineHeight / 3;
        int baseX = getScreenWidth() - maxEntryWidth - 3;

        // Background colors (custom if set)
        int lineBackgroundColor = this.minecraft.options.getBackgroundColor(0.3f);
        if (this.customLineBackgroundColor != null) {
            lineBackgroundColor = this.customLineBackgroundColor.getColorInt();
        }
        int titleBackgroundColor = this.minecraft.options.getBackgroundColor(0.4f);
        if (this.customTitleBackgroundColor != null) {
            titleBackgroundColor = this.customTitleBackgroundColor.getColorInt();
        }

        // Compute the top Y position of the sidebar
        int topY = baseY - numberOfLines * lineHeight;
        // Record sidebar dimensions and original position (before applying any offset)
        this.sidebarWidth = Math.max(1, maxEntryWidth + 4); // adding padding
        this.sidebarHeight = Math.max(1, baseY - (topY - lineHeight - 1));
        this.sidebarOriginalX = baseX - 2;
        this.sidebarOriginalY = topY - lineHeight - 1;

        // Apply offset if needed
        int effectiveBaseX = baseX + (applyOffset ? offsetX : 0);
        int effectiveBaseY = baseY + (applyOffset ? offsetY : 0);
        int rightX = (getScreenWidth() - 3 + 2) + (applyOffset ? offsetX : 0);

        // If we are in rendering mode, draw each score line and then the title background.
        if (this.renderSidebar) {
            float lineAlpha = (float)ARGB.alpha(lineBackgroundColor) / 255.0f;
            float titleAlpha = (float)ARGB.alpha(titleBackgroundColor) / 255.0f;
            int opacityAdjustedLineBackground = SpiffyRenderUtils.colorWithAlpha(lineBackgroundColor, lineAlpha * this.opacity);
            int opacityAdjustedTitleBackground = SpiffyRenderUtils.colorWithAlpha(titleBackgroundColor, titleAlpha * this.opacity);
            int opacityAdjustedTextColor = ARGB.white(this.opacity); // White text with custom alpha
            
            // Calculate positions for backgrounds (vanilla-style)
            int titleTop = effectiveBaseY - numberOfLines * lineHeight - lineHeight - 1;
            int contentTop = titleTop + lineHeight;
            
            // Draw backgrounds in two calls (like vanilla)
            // First: Title background
            guiGraphics.fill(effectiveBaseX - 2, titleTop, rightX, contentTop, opacityAdjustedTitleBackground);
            // Second: Content background 
            guiGraphics.fill(effectiveBaseX - 2, contentTop, rightX, effectiveBaseY, opacityAdjustedLineBackground);
            
            // Draw title centered
            Font font = this.getFont();
            int titleX = effectiveBaseX + maxEntryWidth / 2 - titleWidth / 2;
            guiGraphics.drawString(font, title, titleX, titleTop + 1, opacityAdjustedTextColor, false);
            
            // Draw each score line
            for (int i = 0; i < displayEntries.length; i++) {
                DisplayEntry entry = displayEntries[i];
                int lineY = effectiveBaseY - (numberOfLines - i) * lineHeight;
                // Draw the player's name and score
                guiGraphics.drawString(this.getFont(), entry.name, effectiveBaseX, lineY, opacityAdjustedTextColor, false);
                guiGraphics.drawString(this.getFont(), entry.score, rightX - entry.scoreWidth, lineY, opacityAdjustedTextColor, false);
            }
        }
    }

    /**
     * Returns the current Font instance.
     */
    private Font getFont() {
        return Minecraft.getInstance().font;
    }

    /**
     * The absolute width of this element equals the sidebar width.
     */
    @Override
    public int getAbsoluteWidth() {
        return this.sidebarWidth;
    }

    /**
     * The absolute height of this element equals the sidebar height.
     */
    @Override
    public int getAbsoluteHeight() {
        return this.sidebarHeight;
    }

    /**
     * DummyObjective is used in editor mode to simulate a scoreboard objective.
     */
    private static class DummyObjective extends Objective {
        public DummyObjective(Scoreboard scoreboard) {
            // Constructor parameters in 1.21.5:
            // scoreboard, name, criteria, displayName, renderType, displayAutoUpdate, numberFormat
            super(
                scoreboard, 
                "dummy", 
                ObjectiveCriteria.DUMMY,
                Component.literal("Scoreboard"), 
                ObjectiveCriteria.RenderType.INTEGER,
                false, 
                null
            );
        }
    }
}