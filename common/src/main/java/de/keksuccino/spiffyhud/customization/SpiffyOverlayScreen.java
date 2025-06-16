package de.keksuccino.spiffyhud.customization;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.ScreenCustomization;
import de.keksuccino.fancymenu.customization.element.anchor.ElementAnchorPoints;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.ObjectHolder;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.fancymenu.util.rendering.ui.widget.RendererWidget;
import de.keksuccino.spiffyhud.customization.elements.Elements;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.air.VanillaLikePlayerAirElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.armor.VanillaLikePlayerArmorElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.bossbars.VanillaLikeBossOverlayElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.effects.VanillaLikeEffectsElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.experience.VanillaLikeExperienceElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.food.VanillaLikePlayerFoodElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.hotbar.VanillaLikeHotbarElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.jumpmeter.VanillaLikeJumpMeterElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.mounthealth.VanillaLikeMountHealthElement;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.playerhealth.VanillaLikePlayerHealthElement;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SpiffyOverlayScreen extends Screen {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_background");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_progress");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_background");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_progress");

    private static final VanillaLikeHotbarElement HOTBAR_ELEMENT = Elements.VANILLA_LIKE_HOTBAR.buildDefaultInstance();
    private static final VanillaLikeJumpMeterElement JUMP_METER_ELEMENT = Elements.VANILLA_LIKE_JUMP_METER.buildDefaultInstance();
    private static final VanillaLikeExperienceElement EXPERIENCE_ELEMENT = Elements.VANILLA_LIKE_EXPERIENCE.buildDefaultInstance();
    private static final VanillaLikePlayerFoodElement FOOD_ELEMENT = Elements.VANILLA_LIKE_PLAYER_FOOD.buildDefaultInstance();
    private static final VanillaLikePlayerArmorElement ARMOR_ELEMENT = Elements.VANILLA_LIKE_PLAYER_ARMOR.buildDefaultInstance();
    private static final VanillaLikePlayerAirElement AIR_ELEMENT = Elements.VANILLA_LIKE_PLAYER_AIR.buildDefaultInstance();
    private static final VanillaLikeMountHealthElement MOUNT_HEALTH_ELEMENT = Elements.VANILLA_LIKE_MOUNT_HEALTH.buildDefaultInstance();
    private static final VanillaLikePlayerHealthElement PLAYER_HEALTH_ELEMENT = Elements.VANILLA_LIKE_PLAYER_HEALTH.buildDefaultInstance();
    private static final VanillaLikeBossOverlayElement BOSS_OVERLAY_ELEMENT = Elements.VANILLA_LIKE_BOSS_OVERLAY.buildDefaultInstance();
    private static final VanillaLikeEffectsElement EFFECTS_ELEMENT = Elements.VANILLA_LIKE_EFFECTS.buildDefaultInstance();

    public final boolean showFancyMenuOverlay;
    protected final Font font = Minecraft.getInstance().font;
    protected final Minecraft minecraft = Minecraft.getInstance();

    public SpiffyOverlayScreen(boolean showFancyMenuOverlay) {
        super(Component.empty());
        this.forceEnableCustomizations();
        this.showFancyMenuOverlay = showFancyMenuOverlay;
    }

    protected void forceEnableCustomizations() {
        if (!ScreenCustomization.isCustomizationEnabledForScreen(this)) {
            LOGGER.info("[SPIFFY HUD] Force-enabling customizations for SpiffyOverlayScreen..");
            ScreenCustomization.setCustomizationForScreenEnabled(this, true);
        }
    }

    @Override
    protected void init() {

        this.addRenderableWidget(this.buildHotbarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.hotbar"));

        this.addRenderableWidget(this.buildJumpMeterWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.jump_meter"));

        this.addRenderableWidget(this.buildExperienceBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.experience_bar"));

        this.addRenderableWidget(this.buildSelectedItemNameWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.selected_item_name"));

        this.addRenderableWidget(this.buildScoreboardSidebarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.scoreboard_sidebar"));

        this.addRenderableWidget(this.buildFoodBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.food_bar"));

        this.addRenderableWidget(this.buildArmorBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.armor_bar"));

        this.addRenderableWidget(this.buildAirBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.air_bar"));

        this.addRenderableWidget(this.buildHealthBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.health_bar"));

        this.addRenderableWidget(this.buildMountHealthBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.mount_health_bar"));

        this.addRenderableWidget(this.buildOverlayMessageWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.overlay_message"));

        this.addRenderableWidget(this.buildCrosshairWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.crosshair"));

        this.addRenderableWidget(this.buildAttackIndicatorWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.attack_indicator"));

        this.addRenderableWidget(this.buildTitleWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.title"));

        this.addRenderableWidget(this.buildSubtitleWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.subtitle"));

        this.addRenderableWidget(this.buildBossBarWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.boss_bars"));

        this.addRenderableWidget(this.buildEffectsWidget()).setMessage(Component.translatable("spiffyhud.elements.dummy.effects"));

    }

    @Override
    public void render(@NotNull GuiGraphics $$0, int $$1, int $$2, float $$3) {

        //Don't render widgets when not in the editor
        if (!(Minecraft.getInstance().screen instanceof LayoutEditorScreen)) return;

        this.children().forEach(guiEventListener -> {
            if (guiEventListener instanceof Renderable renderable) {
                renderable.render($$0, $$1, $$2, $$3);
            }
        });

    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
    }

    protected RendererWidget buildHotbarWidget() {
        int screenCenter = this.width / 2;
        int x = screenCenter - 91;
        int y = this.height - 22;
        int widgetWidth = 182;
        int widgetHeight = 22;
        return new SpiffyRendererWidget(x, y, widgetWidth, widgetHeight, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            HOTBAR_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            HOTBAR_ELEMENT.posOffsetX = gx;
            HOTBAR_ELEMENT.posOffsetY = gy - 2;
            HOTBAR_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.HOTBAR_IDENTIFIER);
    }

    protected RendererWidget buildJumpMeterWidget() {
        int screenCenter = this.width / 2;
        int x = screenCenter - 91;
        int y = (this.height - 32 + 3) - 5; // custom offset
        int width = 182;
        int height = 5;
        return new SpiffyRendererWidget(x, y, width, height, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            JUMP_METER_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            JUMP_METER_ELEMENT.posOffsetX = gx;
            JUMP_METER_ELEMENT.posOffsetY = gy;
            JUMP_METER_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.JUMP_METER_IDENTIFIER);
    }

    protected RendererWidget buildExperienceBarWidget() {
        int screenCenter = this.width / 2;
        int x = screenCenter - 91;
        int y = this.height - 32 + 3;
        int width = 182;
        int height = 5;
        return new SpiffyRendererWidget(x, y, width, height, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            EXPERIENCE_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            EXPERIENCE_ELEMENT.posOffsetX = gx;
            EXPERIENCE_ELEMENT.posOffsetY = gy;
            EXPERIENCE_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.EXPERIENCE_BAR_IDENTIFIER);
    }

    protected RendererWidget buildSelectedItemNameWidget() {

        MutableComponent mutableComponent = Component.empty().append(Component.translatable("spiffyhud.elements.dummy.selected_item_name")).withStyle(ChatFormatting.LIGHT_PURPLE);
        int textWidth = font.width(mutableComponent);
        int textX = (this.width - textWidth) / 2;
        int textY = (this.height - 59) - 18; // -18 is a custom offset to move the text above the bars that got moved up because of the Jump Meter

        return new SpiffyRendererWidget(textX, textY, textWidth, font.lineHeight,
                (graphics, mouseX, mouseY, partial, x, y, width, height, widget) -> {
                    RenderSystem.enableBlend();
                    graphics.fill(textX - 2, textY - 2, textX + textWidth + 2, textY + font.lineHeight + 2, Minecraft.getInstance().options.getBackgroundColor(0));
                    graphics.drawString(font, mutableComponent, textX, textY, -1);
                    RenderingUtils.resetShaderColor(graphics);
                }
        ).setWidgetIdentifierFancyMenu(VanillaHudElements.SELECTED_ITEM_NAME_IDENTIFIER);

    }

    protected RendererWidget buildScoreboardSidebarWidget() {

        String spacer = ": ";

        //Build line entries
        record DisplayEntry(Component name, Component score, int scoreWidth) {}
        List<DisplayEntry> entryList = new ArrayList<>();
        Component name = Component.translatable("spiffyhud.elements.dummy.scoreboard_sidebar.line");
        Component score = Component.literal("0").withStyle(ChatFormatting.RED);
        int scoreWidth = font.width(score);
        entryList.add(new DisplayEntry(Component.empty(), Component.empty(), scoreWidth));
        entryList.add(new DisplayEntry(name, score, scoreWidth));
        entryList.add(new DisplayEntry(name, score, scoreWidth));
        entryList.add(new DisplayEntry(name, score, scoreWidth));
        entryList.add(new DisplayEntry(name, score, scoreWidth));
        entryList.add(new DisplayEntry(name, score, scoreWidth));
        entryList.add(new DisplayEntry(name, score, scoreWidth));
        DisplayEntry[] entries = entryList.toArray(new DisplayEntry[0]);

        Component title = Component.translatable("spiffyhud.elements.dummy.scoreboard_sidebar.title").withStyle(ChatFormatting.BOLD);
        int titleWidth = font.width(title);
        int totalSidebarWidth = titleWidth;
        int spacerWidth = font.width(spacer);
        for (DisplayEntry entry : entries) {
            totalSidebarWidth = Math.max(totalSidebarWidth, font.width(entry.name) + (entry.scoreWidth > 0 ? spacerWidth + entry.scoreWidth : 0));
        }
        int linesCount = entries.length;
        int linesHeight = linesCount * font.lineHeight;
        int sidebarYEnd = this.height / 2 + linesHeight / 3;
        int sidebarXStart = this.width - totalSidebarWidth - 3;
        int sidebarXEnd = this.width - 3 + 2;
        int backgroundColorNormal = minecraft.options.getBackgroundColor(0.3f);
        int backgroundColorTitle = minecraft.options.getBackgroundColor(0.4f);
        int sidebarYBase = sidebarYEnd - linesCount * font.lineHeight;
        int finalTotalSidebarWidth = totalSidebarWidth;
        int sidebarY = sidebarYBase - font.lineHeight - 1;
        int sidebarHeight = Math.max(1, sidebarYEnd - sidebarY);

        return new SpiffyRendererWidget(sidebarXStart, sidebarY, totalSidebarWidth, sidebarHeight,
                (graphics, mouseX, mouseY, partial, x, y, width, height, widget) -> {
                    RenderSystem.enableBlend();
                    //Render title background
                    graphics.fill(sidebarXStart - 2, sidebarYBase - font.lineHeight - 1, sidebarXEnd, sidebarYBase - 1, backgroundColorTitle);
                    //Render lines background
                    graphics.fill(sidebarXStart - 2, sidebarYBase - 1, sidebarXEnd, sidebarYEnd, backgroundColorNormal);
                    //Render title
                    graphics.drawString(font, title, sidebarXStart + finalTotalSidebarWidth / 2 - titleWidth / 2, sidebarYBase - font.lineHeight, -1, false);
                    //Render lines
                    for (int t = 0; t < linesCount; ++t) {
                        DisplayEntry entry = entries[t];
                        int u = sidebarYEnd - (linesCount - t) * font.lineHeight;
                        graphics.drawString(font, entry.name, sidebarXStart, u, -1, false);
                        graphics.drawString(font, entry.score, sidebarXEnd - entry.scoreWidth, u, -1, false);
                    }
                    RenderingUtils.resetShaderColor(graphics);
                }
        ).setWidgetIdentifierFancyMenu(VanillaHudElements.SCOREBOARD_SIDEBAR_IDENTIFIER);

    }

    protected RendererWidget buildFoodBarWidget() {
        int barX = this.width / 2 + 91;
        int barY = (this.height - 39) - 5;
        return new SpiffyRendererWidget(barX - 80, barY, 80, 9, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            FOOD_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            FOOD_ELEMENT.posOffsetX = gx;
            FOOD_ELEMENT.posOffsetY = gy;
            FOOD_ELEMENT.spiffyAlignment = SpiffyAlignment.MID_RIGHT;
            FOOD_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.FOOD_BAR_IDENTIFIER);
    }

    protected RendererWidget buildArmorBarWidget() {
        int barX = this.width / 2 - 91;
        int barY = (this.height - 39 - 10) - 5;
        return new SpiffyRendererWidget(barX, barY, 80, 9, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            ARMOR_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            ARMOR_ELEMENT.posOffsetX = gx;
            ARMOR_ELEMENT.posOffsetY = gy;
            ARMOR_ELEMENT.spiffyAlignment = SpiffyAlignment.MID_LEFT;
            ARMOR_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.ARMOR_BAR_IDENTIFIER);
    }

    protected RendererWidget buildAirBarWidget() {
        int barX = this.width / 2 + 91;
        int barY = (this.height - 39 - 10) - 5;
        return new SpiffyRendererWidget(barX - 80, barY, 80, 9, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            AIR_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            AIR_ELEMENT.posOffsetX = gx;
            AIR_ELEMENT.posOffsetY = gy;
            AIR_ELEMENT.spiffyAlignment = SpiffyAlignment.MID_RIGHT;
            AIR_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.AIR_BAR_IDENTIFIER);
    }

    protected RendererWidget buildMountHealthBarWidget() {
        int barX = this.width / 2 + 91;
        int barY = (this.height - 39 - 10) - 15;
        return new SpiffyRendererWidget(barX - 80, barY, 80, 9, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            MOUNT_HEALTH_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            MOUNT_HEALTH_ELEMENT.posOffsetX = gx;
            MOUNT_HEALTH_ELEMENT.posOffsetY = gy;
            MOUNT_HEALTH_ELEMENT.spiffyAlignment = SpiffyAlignment.MID_RIGHT;
            MOUNT_HEALTH_ELEMENT.isUsedAsDummy = true;
            MOUNT_HEALTH_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.MOUNT_HEALTH_BAR_IDENTIFIER);
    }

    protected RendererWidget buildHealthBarWidget() {
        int barX = this.width / 2 - 91;
        int barY = (this.height - 39) - 5;
        return new SpiffyRendererWidget(barX, barY, 80, 9, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            PLAYER_HEALTH_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            PLAYER_HEALTH_ELEMENT.posOffsetX = gx;
            PLAYER_HEALTH_ELEMENT.posOffsetY = gy;
            PLAYER_HEALTH_ELEMENT.spiffyAlignment = SpiffyAlignment.MID_LEFT;
            PLAYER_HEALTH_ELEMENT.isUsedAsDummy = true;
            PLAYER_HEALTH_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.PLAYER_HEALTH_BAR_IDENTIFIER);
    }

    protected RendererWidget buildOverlayMessageWidget() {
        Component message = Component.literal("Overlay Message");
        int messageWidth = font.width(message);
        int textX = (this.width / 2) - (messageWidth / 2);
        int textY = ((this.height - 68) - 4) - 18;
        ObjectHolder<Float> animatedTickHolder = ObjectHolder.of(0.0f);
        return new SpiffyRendererWidget(textX - 2, textY - 2, messageWidth + 4, font.lineHeight + 4, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            RenderSystem.enableBlend();
            // Update the animated tick value by incrementing it
            animatedTickHolder.set(animatedTickHolder.get() + 0.005f);
            // Use the animated tick value to create a color cycle
            int animatedTextColor = Mth.hsvToRgb(animatedTickHolder.get() % 1.0f, 0.7f, 0.6f) | 0xFF000000;
            graphics.drawString(Minecraft.getInstance().font, message, textX, textY, animatedTextColor);
            RenderingUtils.resetShaderColor(graphics);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.OVERLAY_MESSAGE_IDENTIFIER);
    }

    protected RendererWidget buildCrosshairWidget() {
        int crosshairX = (this.width - 15) / 2;
        int crosshairY = (this.height - 15) / 2;
        return new SpiffyRendererWidget(crosshairX, crosshairY, 15, 15, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            graphics.blitSprite(CROSSHAIR_SPRITE, crosshairX, crosshairY, 15, 15);
            RenderSystem.defaultBlendFunc();
            RenderingUtils.resetShaderColor(graphics);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.CROSSHAIR_IDENTIFIER);
    }

    protected RendererWidget buildAttackIndicatorWidget() {

        int crossX = (this.width / 2) + 1;
        int crossY = (this.height / 2 - 7 + 16) + 40;
        int hotX = crossX - 2 - 18;
        int hotY = crossY - 6;
        float progress = 0.5F;

        return new SpiffyRendererWidget(hotX, hotY, 18 + 2 + 16, 18,
                (graphics, mouseX, mouseY, partial, x, y, width, height, widget) -> {

                    RenderSystem.enableBlend();

                    // Render crosshair indicator with sprite system
                    int fillWidth = (int)(progress * 16.0f);
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    graphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, crossX, crossY, 16, 4);
                    if (fillWidth > 0) {
                        graphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, crossX, crossY, fillWidth, 4);
                    }
                    RenderSystem.defaultBlendFunc();

                    RenderSystem.enableBlend();

                    // Render hotbar indicator with sprite system
                    int fillHeight = (int)(progress * 18.0f);
                    graphics.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, hotX, hotY, 18, 18);
                    if (fillHeight > 0) {
                        graphics.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - fillHeight, hotX, hotY + 18 - fillHeight, 18, fillHeight);
                    }

                    RenderingUtils.resetShaderColor(graphics);

                }
        ).setWidgetIdentifierFancyMenu(VanillaHudElements.ATTACK_INDICATOR_IDENTIFIER);

    }

    protected RendererWidget buildTitleWidget() {

        Component title = Component.translatable("spiffyhud.elements.dummy.title");
        int titleWidth = font.width(title);
        int totalWidth = titleWidth * 4;
        int totalHeight = font.lineHeight * 4;
        int textX = (this.width / 2) - (totalWidth / 2);
        int textY = (this.height / 2) - (12 * 4);

        return new SpiffyRendererWidget(textX, textY, totalWidth, totalHeight,
                (graphics, mouseX, mouseY, partial, x, y, width, height, widget) -> {
                    RenderSystem.enableBlend();
                    graphics.pose().pushPose();
                    graphics.pose().translate((float)this.width / 2, (float)this.height / 2, 0.0f);
                    RenderSystem.enableBlend();
                    //Render title
                    graphics.pose().pushPose();
                    graphics.pose().scale(4.0f, 4.0f, 4.0f);
                    graphics.drawString(font, title, -titleWidth / 2, -12, -1);
                    graphics.pose().popPose();
                    graphics.pose().popPose();
                    RenderingUtils.resetShaderColor(graphics);
                }
        ).setWidgetIdentifierFancyMenu(VanillaHudElements.TITLE_IDENTIFIER);

    }

    protected RendererWidget buildSubtitleWidget() {
        Component subtitle = Component.literal("Subtitle");
        int subtitleWidth = font.width(subtitle);
        int totalWidth = subtitleWidth * 2;
        int totalHeight = font.lineHeight * 2;
        int textX = (this.width / 2) - (totalWidth / 2);
        int textY = (this.height / 2) + (6 * 2);
        return new SpiffyRendererWidget(textX, textY, totalWidth, totalHeight, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            RenderSystem.enableBlend();
            graphics.pose().pushPose();
            graphics.pose().translate(this.width / 2, this.height / 2, 0.0f);
            graphics.pose().pushPose();
            graphics.pose().scale(2.0f, 2.0f, 2.0f);
            graphics.drawString(Minecraft.getInstance().font, subtitle, -subtitleWidth / 2, 6, 0xFFFFFF);
            graphics.pose().popPose();
            graphics.pose().popPose();
            RenderingUtils.resetShaderColor(graphics);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.SUBTITLE_IDENTIFIER);
    }

    protected RendererWidget buildBossBarWidget() {
        int barX = this.width / 2 - 91;
        int barY = 12;
        int totalY = barY - 9;
        int totalHeight = 53;
        return new SpiffyRendererWidget(barX, totalY, 182, totalHeight, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            BOSS_OVERLAY_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            BOSS_OVERLAY_ELEMENT.posOffsetX = gx;
            BOSS_OVERLAY_ELEMENT.posOffsetY = gy;
            BOSS_OVERLAY_ELEMENT.spiffyAlignment = SpiffyAlignment.TOP_CENTERED;
            BOSS_OVERLAY_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.BOSS_BARS_IDENTIFIER);
    }

    protected RendererWidget buildEffectsWidget() {
        return new SpiffyRendererWidget(this.width - 50 - 1, 1, 50, 50, (graphics, mX, mY, partial, gx, gy, gwidth, gheight, widget) -> {
            EFFECTS_ELEMENT.anchorPoint = ElementAnchorPoints.TOP_LEFT;
            EFFECTS_ELEMENT.posOffsetX = gx;
            EFFECTS_ELEMENT.posOffsetY = gy;
            EFFECTS_ELEMENT.spiffyAlignment = SpiffyAlignment.TOP_RIGHT;
            EFFECTS_ELEMENT.render(graphics, mX, mY, partial);
        }).setWidgetIdentifierFancyMenu(VanillaHudElements.EFFECTS_IDENTIFIER);
    }

    public static class SpiffyRendererWidget extends RendererWidget {

        public SpiffyRendererWidget(int x, int y, int width, int height, @NotNull RendererWidgetBody body) {
            super(x, y, width, height, body);
        }

        @Override
        public void render(@NotNull GuiGraphics $$0, int $$1, int $$2, float $$3) {
            //Don't render widgets when not in the editor
            if (!(Minecraft.getInstance().screen instanceof LayoutEditorScreen)) return;
            super.render($$0, $$1, $$2, $$3);
        }

    }

}
