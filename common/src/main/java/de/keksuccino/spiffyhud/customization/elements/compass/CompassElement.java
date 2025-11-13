package de.keksuccino.spiffyhud.customization.elements.compass;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.spiffyhud.util.death.DeathPointStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompassElement extends AbstractElement {

    private static final int DEFAULT_BACKGROUND_COLOR = 0xB0101010;
    private static final int DEFAULT_BAR_COLOR = 0xC0FFFFFF;
    private static final int DEFAULT_MAJOR_TICK_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_MINOR_TICK_COLOR = 0x99FFFFFF;
    private static final int DEFAULT_CARDINAL_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_NUMBER_TEXT_COLOR = 0xFFDCDCDC;
    private static final int DEFAULT_NEEDLE_COLOR = 0xFFFF4545;
    private static final int DEFAULT_DEATH_POINTER_COLOR = 0xFF66C3FF;

    public static final String DEFAULT_BACKGROUND_COLOR_STRING = "#B0101010";
    public static final String DEFAULT_BAR_COLOR_STRING = "#C0FFFFFF";
    public static final String DEFAULT_MAJOR_TICK_COLOR_STRING = "#FFFFFFFF";
    public static final String DEFAULT_MINOR_TICK_COLOR_STRING = "#99FFFFFF";
    public static final String DEFAULT_CARDINAL_TEXT_COLOR_STRING = "#FFFFFFFF";
    public static final String DEFAULT_NUMBER_TEXT_COLOR_STRING = "#FFDCDCDC";
    public static final String DEFAULT_NEEDLE_COLOR_STRING = "#FFFF4545";
    public static final String DEFAULT_DEATH_POINTER_COLOR_STRING = "#FF66C3FF";

    @NotNull public String backgroundColor = DEFAULT_BACKGROUND_COLOR_STRING;
    @NotNull public String barColor = DEFAULT_BAR_COLOR_STRING;
    @NotNull public String majorTickColor = DEFAULT_MAJOR_TICK_COLOR_STRING;
    @NotNull public String minorTickColor = DEFAULT_MINOR_TICK_COLOR_STRING;
    @NotNull public String cardinalTextColor = DEFAULT_CARDINAL_TEXT_COLOR_STRING;
    @NotNull public String numberTextColor = DEFAULT_NUMBER_TEXT_COLOR_STRING;
    @NotNull public String needleColor = DEFAULT_NEEDLE_COLOR_STRING;
    public boolean cardinalOutlineEnabled = true;
    public boolean degreeOutlineEnabled = true;
    public boolean deathPointerEnabled = true;
    @NotNull public String deathPointerColor = DEFAULT_DEATH_POINTER_COLOR_STRING;

    private final Minecraft minecraft = Minecraft.getInstance();

    public CompassElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
        this.stickyAnchor = true;
        this.stayOnScreen = false;
        this.supportsTilting = false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (!this.shouldRender()) {
            return;
        }

        int width = Math.max(1, this.getAbsoluteWidth());
        int height = Math.max(6, this.getAbsoluteHeight());
        int x = this.getAbsoluteX();
        int y = this.getAbsoluteY();

        CompassReading reading = this.collectReading(partial);
        DeathPointerData deathPointer = this.collectDeathPointer();
        ResolvedColors colors = this.resolveColors();
        Font font = this.minecraft.font;
        CompassLayout layout = this.computeLayout(font, x, y, width, height);

        RenderSystem.enableBlend();
        this.drawBackground(graphics, layout, colors.backgroundColor());
        this.drawBar(graphics, layout, colors.barColor());
        this.drawGradeLines(graphics, layout, colors, reading);
        this.drawCardinalLabels(graphics, layout, colors, reading);
        this.drawDegreeNumbers(graphics, layout, colors, reading);
        this.drawNeedle(graphics, layout, colors);
        this.drawDeathNeedle(graphics, layout, reading, deathPointer, colors);
        RenderingUtils.resetShaderColor(graphics);
    }

    private void drawBackground(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, int color) {
        graphics.fill(layout.x(), layout.y(), layout.x() + layout.width(), layout.y() + layout.height(), color);
    }

    private void drawBar(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, int color) {
        graphics.fill(layout.x(), layout.barTop(), layout.x() + layout.width(), layout.barTop() + layout.barHeight(), color);
    }

    private void drawGradeLines(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors, @NotNull CompassReading reading) {
        for (int degrees = -180; degrees <= 180; degrees += 10) {
            boolean major = (degrees % 30) == 0;
            int color = major ? colors.majorTickColor() : colors.minorTickColor();
            int halfHeight = major ? layout.majorTickHalfHeight() : layout.minorTickHalfHeight();
            int absolute = toAbsoluteDegrees(degrees);
            float relative = this.relativeToHeading(absolute, reading.headingDegrees());
            this.drawTick(graphics, layout, relative, halfHeight, color);
        }
    }

    private void drawTick(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float relativeDegrees, int halfHeight, int color) {
        float x = this.computeScreenX(layout, relativeDegrees);
        int xi = Mth.clamp(Mth.floor(x), layout.x(), layout.x() + layout.width() - 1);
        int top = layout.barCenterY() - halfHeight;
        int bottom = layout.barCenterY() + halfHeight;
        graphics.fill(xi, top, xi + 1, bottom, color);
    }

    private void drawCardinalLabels(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors, @NotNull CompassReading reading) {
        String[] labels = {"N", "E", "S", "W"};
        float[] angles = {0F, 90F, 180F, 270F};
        for (int i = 0; i < labels.length; i++) {
            this.drawCardinal(graphics, layout, angles[i], labels[i], colors.cardinalTextColor(), reading);
        }
    }

    private void drawCardinal(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float absoluteDegrees, @NotNull String text, int color, @NotNull CompassReading reading) {
        float relative = this.relativeToHeading(absoluteDegrees, reading.headingDegrees());
        float centerX = this.computeScreenX(layout, relative);
        this.drawScaledCenteredString(graphics, text, centerX, layout.cardinalCenterY(), layout.cardinalScale(), color, this.cardinalOutlineEnabled);
    }

    private void drawDegreeNumbers(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors, @NotNull CompassReading reading) {
        for (int degrees = -150; degrees <= 150; degrees += 30) {
            if (degrees == 0) {
                continue;
            }
            int absolute = toAbsoluteDegrees(degrees);
            if ((absolute % 90) == 0) {
                continue;
            }
            String label = Integer.toString(absolute);
            float relative = this.relativeToHeading(absolute, reading.headingDegrees());
            float centerX = this.computeScreenX(layout, relative);
            this.drawScaledCenteredString(graphics, label, centerX, layout.numberCenterY(), layout.numberScale(), colors.numberTextColor(), this.degreeOutlineEnabled);
        }
    }

    private void drawNeedle(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors) {
        int needleWidth = Math.max(1, Mth.floor(layout.width() * 0.01F));
        int half = Math.max(0, needleWidth / 2);
        int centerX = layout.x() + layout.width() / 2;
        int xi = Mth.clamp(centerX - half, layout.x(), layout.x() + layout.width() - needleWidth);
        graphics.fill(xi, layout.y(), xi + needleWidth, layout.y() + layout.height(), colors.needleColor());
    }

    private void drawDeathNeedle(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull CompassReading reading, @Nullable DeathPointerData pointer, @NotNull ResolvedColors colors) {
        if (pointer == null) {
            return;
        }
        float signedHeading = toSigned(reading.headingDegrees());
        float relative = Mth.wrapDegrees(pointer.signedDegrees() - signedHeading);
        float x = this.computeScreenX(layout, relative);
        int needleWidth = Math.max(1, Mth.floor(layout.width() * 0.006F));
        int half = Math.max(0, needleWidth / 2);
        int xi = Mth.clamp(Mth.floor(x) - half, layout.x(), layout.x() + layout.width() - needleWidth);
        graphics.fill(xi, layout.y(), xi + needleWidth, layout.y() + layout.height(), colors.deathNeedleColor());
    }

    private float computeScreenX(@NotNull CompassLayout layout, float signedDegrees) {
        float clamped = Mth.clamp(signedDegrees, -180.0F, 180.0F);
        float normalized = (clamped / 360.0F) + 0.5F;
        float px = layout.x() + normalized * layout.width();
        return Mth.clamp(px, layout.x(), layout.x() + layout.width() - 1);
    }

    private void drawScaledCenteredString(@NotNull GuiGraphics graphics, @NotNull String text, float centerX, float centerY, float scale, int color, boolean outline) {
        RenderSystem.disableDepthTest();
        RenderingUtils.setDepthTestLocked(true);
        if (text.isEmpty() || scale <= 0.0F) {
            return;
        }
        Font font = this.minecraft.font;
        float textWidth = font.width(text) * scale;
        float textHeight = font.lineHeight * scale;
        float drawX = centerX - (textWidth / 2.0F);
        float drawY = centerY - (textHeight / 2.0F);
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(drawX, drawY, 0);
        pose.scale(scale, scale, 1.0F);
        RenderSystem.enableBlend();
        if (outline) {
            int blackColor = applyOpacity(DrawableColor.BLACK.getColorInt());
            graphics.drawString(font, text, -1, 0, blackColor, false);
            graphics.drawString(font, text, 0, -1, blackColor, false);
            graphics.drawString(font, text, 1, 0, blackColor, false);
            graphics.drawString(font, text, 0, 1, blackColor, false);
            graphics.drawString(font, text, -1, -1, blackColor, false);
            graphics.drawString(font, text, 1, -1, blackColor, false);
            graphics.drawString(font, text, 1, 1, blackColor, false);
            graphics.drawString(font, text, -1, 1, blackColor, false);
        }
        graphics.drawString(font, text, 0, 0, color, false);
        pose.popPose();
        RenderingUtils.setDepthTestLocked(false);
        RenderSystem.enableDepthTest();
    }

    private ResolvedColors resolveColors() {
        return new ResolvedColors(
                this.applyOpacity(parseColor(this.backgroundColor, DEFAULT_BACKGROUND_COLOR)),
                this.applyOpacity(parseColor(this.barColor, DEFAULT_BAR_COLOR)),
                this.applyOpacity(parseColor(this.majorTickColor, DEFAULT_MAJOR_TICK_COLOR)),
                this.applyOpacity(parseColor(this.minorTickColor, DEFAULT_MINOR_TICK_COLOR)),
                this.applyOpacity(parseColor(this.cardinalTextColor, DEFAULT_CARDINAL_TEXT_COLOR)),
                this.applyOpacity(parseColor(this.numberTextColor, DEFAULT_NUMBER_TEXT_COLOR)),
                this.applyOpacity(parseColor(this.needleColor, DEFAULT_NEEDLE_COLOR)),
                this.applyOpacity(parseColor(this.deathPointerColor, DEFAULT_DEATH_POINTER_COLOR))
        );
    }

    private int applyOpacity(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        int rgb = argb & 0x00FFFFFF;
        int adjustedAlpha = (int) Mth.clamp(alpha * this.opacity, 0.0F, 255.0F);
        return (adjustedAlpha << 24) | rgb;
    }

    private int parseColor(@Nullable String configured, int fallback) {
        if (configured != null && !configured.isBlank()) {
            String replaced = PlaceholderParser.replacePlaceholders(configured).trim();
            if (!replaced.isEmpty()) {
                DrawableColor drawable = DrawableColor.of(replaced);
                if (drawable != DrawableColor.EMPTY) {
                    return drawable.getColorInt();
                }
            }
        }
        return fallback;
    }

    private CompassReading collectReading(float partialTick) {
        if (this.isEditor()) {
            float simulated = (float) ((System.currentTimeMillis() % 12000L) / 12000.0D * 360.0D);
            return new CompassReading(simulated);
        }
        Player player = this.minecraft.player;
        if (player == null) {
            float fallback = (float) ((System.currentTimeMillis() % 8000L) / 8000.0D * 360.0D);
            return new CompassReading(fallback);
        }
        float yaw = player.getViewYRot(partialTick);
        float heading = normalizeYawToHeading(yaw);
        return new CompassReading(heading);
    }

    private float normalizeYawToHeading(float yaw) {
        float normalized = yaw % 360.0F;
        if (normalized < 0.0F) {
            normalized += 360.0F;
        }
        normalized = (normalized + 180.0F) % 360.0F;
        return normalized;
    }

    @Nullable
    private DeathPointerData collectDeathPointer() {
        if (!this.deathPointerEnabled) {
            return null;
        }
        if (this.isEditor()) {
            return new DeathPointerData(60.0F);
        }
        Player player = this.minecraft.player;
        if (player == null) {
            return null;
        }
        DeathPointStorage.StoredDeathPoint point = DeathPointStorage.get();
        if (point == null || !point.dimensionMatches(player.level())) {
            return null;
        }
        if (point.squaredDistanceTo(player.getX(), player.getY(), player.getZ()) <= 1.0E-3D) {
            return null;
        }
        double dx = point.getX() - player.getX();
        double dz = point.getZ() - player.getZ();
        double distanceSq = dx * dx + dz * dz;
        if (distanceSq < 1.0E-4D) {
            return null;
        }
        double angleRad = Math.atan2(dx, -dz);
        float degrees = (float) (angleRad * (180.0D / Math.PI));
        return new DeathPointerData(degrees);
    }

    private static float toSigned(float headingDegrees) {
        return (headingDegrees > 180.0F) ? (headingDegrees - 360.0F) : headingDegrees;
    }

    private static int toAbsoluteDegrees(int signedDegrees) {
        int wrapped = signedDegrees % 360;
        if (wrapped < 0) {
            wrapped += 360;
        }
        return wrapped;
    }

    private float relativeToHeading(float targetDegrees, float headingDegrees) {
        return Mth.wrapDegrees(targetDegrees - headingDegrees);
    }

    private CompassLayout computeLayout(@NotNull Font font, int x, int y, int width, int height) {
        int barHeight = Math.max(2, Mth.floor(height * 0.2F));
        int barTop = y + (height - barHeight) / 2;
        int barCenter = barTop + barHeight / 2;
        int majorHalf = Math.min(Math.max(barHeight / 2 + 2, Mth.floor(height * 0.3F)), height / 2);
        int minorHalf = Math.min(Math.max(1, Mth.floor(height * 0.18F)), height / 2);
        float baseScale = Mth.clamp(height / 60.0F, 0.55F, 3.0F);
        float cardinalScale = baseScale;
        float numberScale = Math.max(0.4F, baseScale * 0.8F);
        float cardinalHalfHeight = font.lineHeight * cardinalScale / 2.0F;
        float numberHalfHeight = font.lineHeight * numberScale / 2.0F;
        float cardinalCenterY = Mth.clamp(y + height * 0.28F, y + cardinalHalfHeight, y + height - cardinalHalfHeight);
        float numberCenterY = Mth.clamp(y + height * 0.78F, y + numberHalfHeight, y + height - numberHalfHeight);
        return new CompassLayout(x, y, width, height, barTop, barHeight, barCenter, majorHalf, minorHalf, cardinalCenterY, numberCenterY, cardinalScale, numberScale);
    }

    private record CompassLayout(int x, int y, int width, int height, int barTop, int barHeight, int barCenterY,
                                 int majorTickHalfHeight, int minorTickHalfHeight, float cardinalCenterY,
                                 float numberCenterY, float cardinalScale, float numberScale) {
    }

    private record ResolvedColors(int backgroundColor, int barColor, int majorTickColor, int minorTickColor,
                                  int cardinalTextColor, int numberTextColor, int needleColor, int deathNeedleColor) {
    }

    private record CompassReading(float headingDegrees) {
    }

    private record DeathPointerData(float signedDegrees) {
    }
}
