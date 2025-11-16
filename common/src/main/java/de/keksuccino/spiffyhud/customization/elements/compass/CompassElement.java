package de.keksuccino.spiffyhud.customization.elements.compass;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.SerializationUtils;
import de.keksuccino.fancymenu.util.rendering.AspectRatio;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.fancymenu.util.resource.ResourceSupplier;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.spiffyhud.customization.marker.MarkerData;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import de.keksuccino.spiffyhud.util.death.DeathPointStorage;
import de.keksuccino.spiffyhud.util.rendering.FlatMobRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CompassElement extends AbstractElement {

    private static final Minecraft MC = Minecraft.getInstance();

    private static final int DEFAULT_BACKGROUND_COLOR = 0xB0101010;
    private static final int DEFAULT_BAR_COLOR = 0xC0FFFFFF;
    private static final int DEFAULT_MAJOR_TICK_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_MINOR_TICK_COLOR = 0x99FFFFFF;
    private static final int DEFAULT_CARDINAL_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_NUMBER_TEXT_COLOR = 0xFFDCDCDC;
    private static final int DEFAULT_NEEDLE_COLOR = 0xFFFF4545;
    private static final int DEFAULT_DEATH_POINTER_COLOR = 0xFF66C3FF;
    private static final int DEFAULT_HOSTILE_DOT_COLOR = 0xFFFF4A4A;
    private static final int DEFAULT_PASSIVE_DOT_COLOR = 0xFFFFE15A;
    private static final int MAX_MOB_DOTS_PER_TYPE = 64;
    private static final long MOB_DOTS_REFRESH_RATE_MS = 10L;

    public static final String DEFAULT_BACKGROUND_COLOR_STRING = "#B0101010";
    public static final String DEFAULT_BAR_COLOR_STRING = "#C0FFFFFF";
    public static final String DEFAULT_MAJOR_TICK_COLOR_STRING = "#FFFFFFFF";
    public static final String DEFAULT_MINOR_TICK_COLOR_STRING = "#99FFFFFF";
    public static final String DEFAULT_CARDINAL_TEXT_COLOR_STRING = "#FFFFFFFF";
    public static final String DEFAULT_NUMBER_TEXT_COLOR_STRING = "#FFDCDCDC";
    public static final String DEFAULT_NEEDLE_COLOR_STRING = "#FFFF4545";
    public static final String DEFAULT_DEATH_POINTER_COLOR_STRING = "#FF66C3FF";
    public static final String DEFAULT_HOSTILE_DOT_COLOR_STRING = "#FFFF4A4A";
    public static final String DEFAULT_PASSIVE_DOT_COLOR_STRING = "#FFFFE15A";

    @NotNull public String backgroundColor = DEFAULT_BACKGROUND_COLOR_STRING;
    @NotNull public String barColor = DEFAULT_BAR_COLOR_STRING;
    @Nullable public ResourceSupplier<ITexture> barTexture;
    @NotNull public String majorTickColor = DEFAULT_MAJOR_TICK_COLOR_STRING;
    @NotNull public String minorTickColor = DEFAULT_MINOR_TICK_COLOR_STRING;
    @NotNull public String cardinalTextColor = DEFAULT_CARDINAL_TEXT_COLOR_STRING;
    @NotNull public String numberTextColor = DEFAULT_NUMBER_TEXT_COLOR_STRING;
    @NotNull public String needleColor = DEFAULT_NEEDLE_COLOR_STRING;
    @Nullable public ResourceSupplier<ITexture> needleTexture;
    @Nullable public ResourceSupplier<ITexture> deathPointerTexture;
    @Nullable public ResourceSupplier<ITexture> hostileDotTexture;
    @Nullable public ResourceSupplier<ITexture> passiveDotTexture;
    public boolean backgroundEnabled = true;
    public boolean barEnabled = true;
    public boolean majorTicksEnabled = true;
    public boolean minorTicksEnabled = true;
    public boolean needleEnabled = true;
    public boolean cardinalTextEnabled = true;
    public boolean degreeNumbersEnabled = true;
    public boolean cardinalOutlineEnabled = true;
    public boolean degreeOutlineEnabled = true;
    public boolean deathPointerEnabled = true;
    public boolean worldMarkersEnabled = true;
    @NotNull public String deathPointerColor = DEFAULT_DEATH_POINTER_COLOR_STRING;
    public boolean hostileDotsEnabled = true;
    public boolean passiveDotsEnabled = true;
    @NotNull public String hostileDotsColor = DEFAULT_HOSTILE_DOT_COLOR_STRING;
    @NotNull public String passiveDotsColor = DEFAULT_PASSIVE_DOT_COLOR_STRING;
    public int hostileDotsRange = 200;
    public int passiveDotsRange = 200;
    public boolean hostileDotsShowHeads = false;
    public boolean passiveDotsShowHeads = false;
    private boolean hasLastDeathPointerRelative = false;
    private float lastDeathPointerRelative = 0.0F;
    @Nullable private Mob previewHostileMob;
    @Nullable private Mob previewPassiveMob;
    @Nullable private MobDots cachedMobDots;
    private long lastMobDotsCacheTime = -1L;

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
        MobDots mobDots = (this.cachedMobDots == null) ? MobDots.EMPTY : this.cachedMobDots;
        long now = System.currentTimeMillis();
        if ((this.cachedMobDots == null) || ((this.lastMobDotsCacheTime + MOB_DOTS_REFRESH_RATE_MS) < now)) {
            this.lastMobDotsCacheTime = now;
            this.cachedMobDots = this.collectMobDots(reading);
            mobDots = this.cachedMobDots;
        }
        List<ResolvedMarker> resolvedMarkers = this.collectMarkers(reading);
        ResolvedColors colors = this.resolveColors();
        Font font = MC.font;
        CompassLayout layout = this.computeLayout(font, x, y, width, height);

        RenderSystem.enableBlend();

        wrapDepthTestLocked(false, () -> {
            if (this.backgroundEnabled) {
                this.drawBackground(graphics, layout, colors.backgroundColor());
            }
            if (this.barEnabled) {
                this.drawBar(graphics, layout, colors.barColor(), reading);
            }
            this.drawGradeLines(graphics, layout, colors, reading);
            if (this.cardinalTextEnabled) {
                this.drawCardinalLabels(graphics, layout, colors, reading);
            }
            if (this.degreeNumbersEnabled) {
                this.drawDegreeNumbers(graphics, layout, colors, reading);
            }
        });

        // Draw outside locked depth test to not break entity model rendering
        if (mobDots.hasAny()) {
            this.drawMobDots(graphics, layout, colors, mobDots);
        }

        wrapDepthTestLocked(false, () -> {
            if (this.needleEnabled) {
                this.drawNeedle(graphics, layout, colors);
            }
            if (!resolvedMarkers.isEmpty()) {
                this.drawMarkerDots(graphics, layout, resolvedMarkers);
                this.drawMarkerNeedles(graphics, layout, resolvedMarkers);
            }
            this.drawDeathNeedle(graphics, layout, reading, deathPointer, colors);
            RenderingUtils.resetShaderColor(graphics);
        });

    }

    private static void wrapDepthTestLocked(boolean enableDepthTest, Runnable wrapped) {

        if (enableDepthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }
        RenderingUtils.setDepthTestLocked(true);

        wrapped.run();

        RenderingUtils.setDepthTestLocked(false);
        RenderSystem.enableDepthTest();

    }

    private void drawBackground(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, int color) {
        graphics.fill(layout.x(), layout.y(), layout.x() + layout.width(), layout.y() + layout.height(), color);
    }

    private void drawBar(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, int color, @NotNull CompassReading reading) {
        if (this.drawBarTexture(graphics, layout, reading)) {
            return;
        }
        graphics.fill(layout.x(), layout.barTop(), layout.x() + layout.width(), layout.barTop() + layout.barHeight(), color);
    }

    private boolean drawBarTexture(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull CompassReading reading) {
        TextureHandle handle = this.resolveTexture(this.barTexture);
        if (handle == null) {
            return false;
        }
        int width = Math.max(1, layout.width());
        int height = Math.max(1, layout.barHeight());
        int textureWidth = handle.width();
        int textureHeight = handle.height();
        if (width <= 0 || height <= 0 || textureWidth <= 0 || textureHeight <= 0) {
            return false;
        }
        float normalizedHeading = Mth.positiveModulo(reading.headingDegrees(), 360.0F) / 360.0F;
        float pixelShift = normalizedHeading * textureWidth;
        float offset = Mth.positiveModulo(pixelShift, textureWidth);
        float startX = layout.x() - offset - textureWidth;
        int maxX = layout.x() + width + textureWidth;
        int drawY = layout.barTop() + (layout.barHeight() - textureHeight) / 2;
        RenderSystem.enableBlend();
        graphics.enableScissor(layout.x(), layout.barTop(), layout.x() + width, layout.barTop() + height);
        graphics.setColor(1.0F, 1.0F, 1.0F, this.opacity);
        try {
            for (float drawX = startX; drawX < maxX; drawX += textureWidth) {
                int drawXi = Mth.floor(drawX);
                this.blitBarTextureTile(graphics, handle, drawXi, drawY);
            }
        } finally {
            graphics.disableScissor();
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        return true;
    }

    private void blitBarTextureTile(@NotNull GuiGraphics graphics, @NotNull TextureHandle handle, int drawX, int drawY) {
        graphics.blit(handle.location(), drawX, drawY, 0.0F, 0.0F, handle.width(), handle.height(), handle.width(), handle.height());
    }

    private void drawGradeLines(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors, @NotNull CompassReading reading) {
        boolean drawMajor = this.majorTicksEnabled;
        boolean drawMinor = this.minorTicksEnabled;
        if (!drawMajor && !drawMinor) {
            return;
        }
        for (int degrees = -180; degrees <= 180; degrees += 10) {
            boolean major = (degrees % 30) == 0;
            if (major && !drawMajor) {
                continue;
            }
            if (!major && !drawMinor) {
                continue;
            }
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

    private void drawMobDots(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors, @NotNull MobDots dots) {
        if (!dots.hasAny()) {
            return;
        }
        float minY = layout.y();
        float maxY = layout.y() + layout.height();
        float dotDiameter = Mth.clamp(layout.height() * 0.12F, 2.0F, 18.0F);
        float radius = dotDiameter / 2.0F;
        boolean drawHostileHeads = this.hostileDotsShowHeads;
        if (!dots.hostileDots().isEmpty()) {
            for (MobDotData data : dots.hostileDots()) {
                float centerY = this.computeDotCenterY(minY, maxY, data.distanceRatio());
                this.drawMobDot(graphics, layout, data, centerY, radius, drawHostileHeads, colors.hostileDotColor(), this.hostileDotTexture);
            }
        }
        boolean drawPassiveHeads = this.passiveDotsShowHeads;
        if (!dots.passiveDots().isEmpty()) {
            for (MobDotData data : dots.passiveDots()) {
                float centerY = this.computeDotCenterY(minY, maxY, data.distanceRatio());
                this.drawMobDot(graphics, layout, data, centerY, radius, drawPassiveHeads, colors.passiveDotColor(), this.passiveDotTexture);
            }
        }
    }

    private float computeDotCenterY(float minY, float maxY, float ratio) {
        float clampedRatio = Mth.clamp(ratio, 0.0F, 1.0F);
        return Mth.clamp(Mth.lerp(clampedRatio, minY, maxY), minY, maxY);
    }

    private void drawMobDot(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull MobDotData data, float centerY, float radius, boolean drawHead, int color, @Nullable ResourceSupplier<ITexture> texture) {
        float centerX = this.computeScreenX(layout, data.relativeDegrees());
        int size = Math.max(2, Mth.ceil(radius * 2.0F));
        DotBounds bounds = this.computeDotBounds(layout, centerX, centerY, radius, size);
        if (drawHead && this.drawMobHead(graphics, bounds, data.mob())) {
            return;
        }
        if (this.drawDotTexture(graphics, bounds, texture)) {
            return;
        }
        graphics.fill(bounds.left(), bounds.top(), bounds.left() + bounds.size(), bounds.top() + bounds.size(), color);
    }

    private void drawMarkerDots(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull List<ResolvedMarker> markers) {
        float dotDiameter = Mth.clamp(layout.height() * 0.12F, 2.0F, 18.0F);
        float radius = dotDiameter / 2.0F;
        float centerY = layout.y() + layout.height() / 2.0F;
        for (ResolvedMarker marker : markers) {
            if (marker.showAsNeedle()) {
                continue;
            }
            this.drawMarkerDot(graphics, layout, marker, centerY, radius);
        }
    }

    private void drawMarkerDot(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedMarker marker, float centerY, float radius) {
        float centerX = this.computeScreenX(layout, marker.relativeDegrees());
        int size = Math.max(2, Mth.ceil(radius * 2.0F));
        DotBounds bounds = this.computeDotBounds(layout, centerX, centerY, radius, size);
        ResourceSupplier<ITexture> texture = marker.dotTexture();
        if (texture == null) {
            texture = marker.needleTexture();
        }
        if (texture != null && this.drawDotTexture(graphics, bounds, texture)) {
            return;
        }
        graphics.fill(bounds.left(), bounds.top(), bounds.left() + bounds.size(), bounds.top() + bounds.size(), marker.color());
    }

    private void drawMarkerNeedles(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull List<ResolvedMarker> markers) {
        for (ResolvedMarker marker : markers) {
            if (!marker.showAsNeedle()) {
                continue;
            }
            this.drawMarkerNeedle(graphics, layout, marker);
        }
    }

    private void drawMarkerNeedle(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedMarker marker) {
        float centerX = this.computeScreenX(layout, marker.relativeDegrees());
        ResourceSupplier<ITexture> texture = marker.needleTexture();
        if (texture == null) {
            texture = marker.dotTexture();
        }
        if (texture != null && this.drawNeedleTexture(graphics, layout, centerX, texture)) {
            return;
        }
        int needleWidth = Math.max(1, Mth.floor(layout.width() * 0.008F));
        int half = Math.max(0, needleWidth / 2);
        int xi = Mth.clamp(Mth.floor(centerX) - half, layout.x(), layout.x() + layout.width() - needleWidth);
        RenderSystem.enableBlend();
        graphics.fill(xi, layout.y(), xi + needleWidth, layout.y() + layout.height(), marker.color());
    }

    private DotBounds computeDotBounds(@NotNull CompassLayout layout, float centerX, float centerY, float radius, int size) {
        int maxWidth = Math.max(1, layout.width());
        int maxHeight = Math.max(1, layout.height());
        int minX = layout.x();
        int minY = layout.y();
        int maxLeft = Math.max(minX, minX + maxWidth - size);
        int maxTop = Math.max(minY, minY + maxHeight - size);
        int left = Mth.clamp(Mth.floor(centerX - radius), minX, maxLeft);
        int top = Mth.clamp(Mth.floor(centerY - radius), minY, maxTop);
        return new DotBounds(left, top, size);
    }

    private boolean drawDotTexture(@NotNull GuiGraphics graphics, @NotNull DotBounds bounds, @Nullable ResourceSupplier<ITexture> supplier) {
        TextureHandle handle = this.resolveTexture(supplier);
        if (handle == null) {
            return false;
        }
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.opacity);
        int size = bounds.size();
        graphics.blit(handle.location(), bounds.left(), bounds.top(), 0.0F, 0.0F, size, size, size, size);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        return true;
    }

    private boolean drawMobHead(@NotNull GuiGraphics graphics, @NotNull DotBounds bounds, @Nullable Mob mob) {
        if (mob == null) {
            return false;
        }
        return FlatMobRenderUtils.renderFlatMob(graphics, bounds.left(), bounds.top(), bounds.size(), mob, this.opacity);
    }

    private void drawNeedle(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull ResolvedColors colors) {
        int needleWidth = Math.max(1, Mth.floor(layout.width() * 0.01F));
        int half = Math.max(0, needleWidth / 2);
        int centerX = layout.x() + layout.width() / 2;
        if (this.drawNeedleTexture(graphics, layout, centerX, this.needleTexture)) {
            return;
        }
        int xi = Mth.clamp(centerX - half, layout.x(), layout.x() + layout.width() - needleWidth);
        RenderSystem.enableBlend();
        graphics.fill(xi, layout.y(), xi + needleWidth, layout.y() + layout.height(), colors.needleColor());
    }

    private void drawDeathNeedle(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, @NotNull CompassReading reading, @Nullable DeathPointerData pointer, @NotNull ResolvedColors colors) {
        if (pointer == null) {
            this.hasLastDeathPointerRelative = false;
            return;
        }
        float signedHeading = toSigned(reading.headingDegrees());
        float relative = pointer.signedDegrees() - signedHeading;
        relative = this.adjustDeathPointerRelative(relative);
        float centerX = this.computeScreenXUnbounded(layout, relative);
        graphics.enableScissor(layout.x(), layout.y(), layout.x() + layout.width(), layout.y() + layout.height());
        try {
            boolean textured = this.drawNeedleTexture(graphics, layout, centerX, this.deathPointerTexture, false, true);
            if (!textured) {
                this.drawDeathNeedleStrips(graphics, layout, centerX, colors.deathNeedleColor());
            }
        } finally {
            graphics.disableScissor();
        }
    }

    private boolean drawNeedleTexture(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float centerX, @Nullable ResourceSupplier<ITexture> supplier) {
        float centerY = layout.y() + layout.height() / 2.0F;
        return this.drawNeedleTexture(graphics, layout, centerX, centerY, supplier, true, false);
    }

    private boolean drawNeedleTexture(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float centerX, @Nullable ResourceSupplier<ITexture> supplier, boolean clampCenter, boolean wrapAcross) {
        float centerY = layout.y() + layout.height() / 2.0F;
        return this.drawNeedleTexture(graphics, layout, centerX, centerY, supplier, clampCenter, wrapAcross);
    }

    private boolean drawNeedleTexture(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float centerX, float centerY, @Nullable ResourceSupplier<ITexture> supplier, boolean clampCenter, boolean wrapAcross) {
        TextureHandle handle = this.resolveTexture(supplier);
        if (handle == null) {
            return false;
        }
        if (wrapAcross) {
            float normalizedCenter = this.normalizeCenterForWrap(layout, centerX);
            this.drawNeedleTextureInstance(graphics, layout, normalizedCenter - layout.width(), centerY, handle, clampCenter);
            this.drawNeedleTextureInstance(graphics, layout, normalizedCenter, centerY, handle, clampCenter);
            this.drawNeedleTextureInstance(graphics, layout, normalizedCenter + layout.width(), centerY, handle, clampCenter);
        } else {
            this.drawNeedleTextureInstance(graphics, layout, centerX, centerY, handle, clampCenter);
        }
        return true;
    }

    private void drawNeedleTextureInstance(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float centerX, float centerY, @NotNull TextureHandle handle, boolean clampCenter) {
        int availableWidth = Math.max(1, layout.width());
        int availableHeight = Math.max(1, layout.height());
        if (handle.width() <= 0 || handle.height() <= 0) {
            return;
        }
        int[] scaled = handle.aspectRatio().getAspectRatioSizeByMaximumSize(availableWidth, availableHeight);
        int destWidth = Math.max(1, Math.min(scaled[0], availableWidth));
        int destHeight = Math.max(1, Math.min(scaled[1], availableHeight));
        float targetCenterX = clampCenter ? Mth.clamp(centerX, layout.x(), layout.x() + layout.width()) : centerX;
        float resolvedCenterY = clampCenter ? Mth.clamp(centerY, layout.y(), layout.y() + layout.height()) : centerY;
        float drawX = targetCenterX - ((float) destWidth / 2.0F);
        float drawY = resolvedCenterY - ((float) destHeight / 2.0F);
        if (clampCenter) {
            float minX = layout.x();
            float maxX = layout.x() + layout.width();
            drawX = Mth.clamp(drawX, minX, maxX - (float) destWidth);
        }
        if (clampCenter) {
            float minY = layout.y();
            float maxY = layout.y() + layout.height();
            drawY = Mth.clamp(drawY, minY, maxY - (float) destHeight);
        }
        int drawXi = Mth.floor(drawX);
        int drawYi = Mth.floor(drawY);
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.opacity);
        graphics.blit(handle.location(), drawXi, drawYi, 0.0F, 0.0F, destWidth, destHeight, destWidth, destHeight);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Nullable
    private TextureHandle resolveTexture(@Nullable ResourceSupplier<ITexture> supplier) {
        if (supplier == null) {
            return null;
        }
        try {
            ITexture texture = supplier.get();
            if (texture == null || !texture.isReady()) {
                return null;
            }
            ResourceLocation location = texture.getResourceLocation();
            if (location == null) {
                return null;
            }
            int width = texture.getWidth();
            int height = texture.getHeight();
            if (width <= 0 || height <= 0) {
                return null;
            }
            return new TextureHandle(location, width, height, texture.getAspectRatio());
        } catch (Exception ignored) {
            return null;
        }
    }

    private float computeScreenX(@NotNull CompassLayout layout, float signedDegrees) {
        float clamped = Mth.clamp(signedDegrees, -180.0F, 180.0F);
        float normalized = (clamped / 360.0F) + 0.5F;
        float px = layout.x() + normalized * layout.width();
        return Mth.clamp(px, layout.x(), layout.x() + layout.width() - 1);
    }

    private float computeScreenXUnbounded(@NotNull CompassLayout layout, float signedDegrees) {
        float normalized = (signedDegrees / 360.0F) + 0.5F;
        return layout.x() + normalized * layout.width();
    }

    private void drawScaledCenteredString(@NotNull GuiGraphics graphics, @NotNull String text, float centerX, float centerY, float scale, int color, boolean outline) {
        if (text.isEmpty() || scale <= 0.0F) {
            return;
        }
        Font font = MC.font;
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
                this.applyOpacity(parseColor(this.deathPointerColor, DEFAULT_DEATH_POINTER_COLOR)),
                this.applyOpacity(parseColor(this.hostileDotsColor, DEFAULT_HOSTILE_DOT_COLOR)),
                this.applyOpacity(parseColor(this.passiveDotsColor, DEFAULT_PASSIVE_DOT_COLOR))
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

    public @NotNull List<MarkerData> getMarkers() {
        return MarkerStorage.getMarkers(this.getMarkerGroupKey());
    }

    public boolean addMarker(@NotNull MarkerData marker) {
        return MarkerStorage.addMarker(this.getMarkerGroupKey(), marker);
    }

    public boolean editMarker(@NotNull String markerName, @NotNull Consumer<MarkerData> editor) {
        return MarkerStorage.editMarker(this.getMarkerGroupKey(), markerName, editor);
    }

    public boolean removeMarker(@NotNull String markerName) {
        return MarkerStorage.removeMarker(this.getMarkerGroupKey(), markerName);
    }

    public void clearMarkers() {
        MarkerStorage.clearGroup(this.getMarkerGroupKey());
    }

    public @NotNull String getMarkerGroupKey() {
        return this.getInstanceIdentifier();
    }

    private CompassReading collectReading(float partialTick) {
        if (this.isEditor()) {
            float simulated = (float) ((System.currentTimeMillis() % 12000L) / 12000.0D * 360.0D);
            return new CompassReading(simulated);
        }
        Player player = MC.player;
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
        Player player = MC.player;
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

    @NotNull
    private MobDots collectMobDots(@NotNull CompassReading reading) {
        boolean hostilesEnabled = this.hostileDotsEnabled;
        boolean passiveEnabled = this.passiveDotsEnabled;
        if (!hostilesEnabled && !passiveEnabled) {
            return MobDots.EMPTY;
        }
        if (this.isEditor()) {
            return this.createEditorMobDots();
        }
        Player player = MC.player;
        if (player == null) {
            return MobDots.EMPTY;
        }
        double hostileRange = Math.max(0.0D, this.hostileDotsRange);
        double passiveRange = Math.max(0.0D, this.passiveDotsRange);
        double maxRange = Math.max(hostileRange, passiveRange);
        if (maxRange <= 0.0D) {
            return MobDots.EMPTY;
        }
        var level = player.level();
        if (level == null) {
            return MobDots.EMPTY;
        }
        List<MobDotData> hostileDots = hostilesEnabled ? new ArrayList<>() : Collections.emptyList();
        List<MobDotData> passiveDots = passiveEnabled ? new ArrayList<>() : Collections.emptyList();
        AABB bounds = player.getBoundingBox().inflate(maxRange, Math.min(64.0D, maxRange), maxRange);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, bounds, this::shouldIncludeMob);
        for (Mob mob : mobs) {
            MobCategory category = mob.getType().getCategory();
            boolean isHostile = (category == MobCategory.MONSTER);
            if (isHostile) {
                if (!hostilesEnabled || hostileDots.size() >= MAX_MOB_DOTS_PER_TYPE) {
                    continue;
                }
                this.appendMobDot(hostileDots, mob, player, reading, hostileRange);
            } else {
                if (!passiveEnabled || passiveDots.size() >= MAX_MOB_DOTS_PER_TYPE) {
                    continue;
                }
                this.appendMobDot(passiveDots, mob, player, reading, passiveRange);
            }
            if ((!hostilesEnabled || hostileDots.size() >= MAX_MOB_DOTS_PER_TYPE)
                    && (!passiveEnabled || passiveDots.size() >= MAX_MOB_DOTS_PER_TYPE)) {
                break;
            }
        }
        boolean hasHostile = hostilesEnabled && !hostileDots.isEmpty();
        boolean hasPassive = passiveEnabled && !passiveDots.isEmpty();
        if (!hasHostile && !hasPassive) {
            return MobDots.EMPTY;
        }
        List<MobDotData> hostileOut = hasHostile ? List.copyOf(hostileDots) : Collections.emptyList();
        List<MobDotData> passiveOut = hasPassive ? List.copyOf(passiveDots) : Collections.emptyList();
        return new MobDots(hostileOut, passiveOut);
    }

    @NotNull
    private MobDots createEditorMobDots() {
        boolean hostilesEnabled = this.hostileDotsEnabled;
        boolean passiveEnabled = this.passiveDotsEnabled;
        if (!hostilesEnabled && !passiveEnabled) {
            return MobDots.EMPTY;
        }
        long now = System.currentTimeMillis();
        float cycleHostile = (float) ((now % 6000L) / 6000.0D * 360.0D) - 180.0F;
        float cyclePassive = (float) ((now % 5000L) / 5000.0D * 360.0D) - 180.0F;
        List<MobDotData> hostileDots = hostilesEnabled ? new ArrayList<>() : Collections.emptyList();
        List<MobDotData> passiveDots = passiveEnabled ? new ArrayList<>() : Collections.emptyList();
        Mob previewHostile = (this.hostileDotsShowHeads && hostilesEnabled) ? this.ensurePreviewMob(true) : null;
        Mob previewPassive = (this.passiveDotsShowHeads && passiveEnabled) ? this.ensurePreviewMob(false) : null;
        if (hostilesEnabled) {
            hostileDots.add(new MobDotData(Mth.wrapDegrees(cycleHostile - 60.0F), 0.2F, previewHostile));
            hostileDots.add(new MobDotData(Mth.wrapDegrees(cycleHostile + 10.0F), 0.55F, previewHostile));
            hostileDots.add(new MobDotData(Mth.wrapDegrees(cycleHostile + 85.0F), 0.85F, previewHostile));
        }
        if (passiveEnabled) {
            passiveDots.add(new MobDotData(Mth.wrapDegrees(cyclePassive - 140.0F), 0.35F, previewPassive));
            passiveDots.add(new MobDotData(Mth.wrapDegrees(cyclePassive - 20.0F), 0.65F, previewPassive));
            passiveDots.add(new MobDotData(Mth.wrapDegrees(cyclePassive + 120.0F), 0.9F, previewPassive));
        }
        List<MobDotData> hostileOut = hostilesEnabled ? List.copyOf(hostileDots) : Collections.emptyList();
        List<MobDotData> passiveOut = passiveEnabled ? List.copyOf(passiveDots) : Collections.emptyList();
        return new MobDots(hostileOut, passiveOut);
    }

    private List<ResolvedMarker> collectMarkers(@NotNull CompassReading reading) {
        if (!this.worldMarkersEnabled) {
            return Collections.emptyList();
        }
        List<MarkerData> markers = MarkerStorage.getMarkers(this.getMarkerGroupKey());
        if (markers.isEmpty()) {
            return Collections.emptyList();
        }
        Player player = MC.player;
        if (player == null) {
            return Collections.emptyList();
        }
        List<ResolvedMarker> resolved = new ArrayList<>(markers.size());
        float heading = reading.headingDegrees();
        for (MarkerData marker : markers) {
            float relative = this.computeMarkerRelativeDegrees(player, marker, heading);
            ResourceSupplier<ITexture> dotTexture = SerializationUtils.deserializeImageResourceSupplier(marker.getDotTexture());
            ResourceSupplier<ITexture> needleTexture = SerializationUtils.deserializeImageResourceSupplier(marker.getNeedleTexture());
            int color = this.applyOpacity(this.parseColor(marker.getColor(), DEFAULT_NEEDLE_COLOR));
            resolved.add(new ResolvedMarker(marker.getName(), relative, marker.isShowAsNeedle(), color, dotTexture, needleTexture));
        }
        return resolved;
    }

    private boolean shouldIncludeMob(@Nullable Mob mob) {
        return mob != null && mob.isAlive() && !mob.isRemoved() && !mob.isSpectator();
    }

    @Nullable
    private Mob ensurePreviewMob(boolean hostile) {
        Mob cached = hostile ? this.previewHostileMob : this.previewPassiveMob;
        if (cached != null) {
            if (cached.isRemoved() || cached.level() != MC.level) {
                cached = null;
            }
        }
        if (cached != null) {
            return cached;
        }
        if (MC.level == null) {
            return null;
        }
        EntityType<? extends Mob> type = hostile ? EntityType.ZOMBIE : EntityType.COW;
        Mob created = type.create(MC.level);
        if (created == null) {
            return null;
        }
        created.setNoGravity(true);
        created.setNoAi(true);
        if (hostile) {
            this.previewHostileMob = created;
        } else {
            this.previewPassiveMob = created;
        }
        return created;
    }

    private void appendMobDot(@NotNull List<MobDotData> dots, @NotNull Mob mob, @NotNull Player player, @NotNull CompassReading reading, double maxRange) {
        if (maxRange <= 0.0D) {
            return;
        }
        double dx = mob.getX() - player.getX();
        double dz = mob.getZ() - player.getZ();
        double distanceSq = dx * dx + dz * dz;
        if (distanceSq <= 1.0E-3D) {
            return;
        }
        double distance = Math.sqrt(distanceSq);
        if (distance > maxRange) {
            return;
        }
        float signed = (float) (Math.atan2(dx, -dz) * (180.0D / Math.PI));
        float absolute = normalizeUnsignedDegrees(signed);
        float relative = this.relativeToHeading(absolute, reading.headingDegrees());
        float ratio = (float) Mth.clamp(distance / maxRange, 0.0D, 1.0D);
        dots.add(new MobDotData(relative, ratio, mob));
    }

    private float computeMarkerRelativeDegrees(@NotNull Player player, @NotNull MarkerData marker, float headingDegrees) {
        double dx = marker.getResolvedMarkerPosX() - player.getX();
        double dz = marker.getResolvedMarkerPosZ() - player.getZ();
        if (Math.abs(dx) <= 1.0E-4D && Math.abs(dz) <= 1.0E-4D) {
            return 0.0F;
        }
        float signed = (float) (Math.atan2(dx, -dz) * (180.0D / Math.PI));
        float absolute = normalizeUnsignedDegrees(signed);
        return this.relativeToHeading(absolute, headingDegrees);
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

    private static float normalizeUnsignedDegrees(float signedDegrees) {
        float wrapped = signedDegrees % 360.0F;
        if (wrapped < 0.0F) {
            wrapped += 360.0F;
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
                                  int cardinalTextColor, int numberTextColor, int needleColor, int deathNeedleColor,
                                  int hostileDotColor, int passiveDotColor) {
    }

    private record CompassReading(float headingDegrees) {
    }

    private record DeathPointerData(float signedDegrees) {
    }

    private record MobDots(@NotNull List<MobDotData> hostileDots, @NotNull List<MobDotData> passiveDots) {

        private static final MobDots EMPTY = new MobDots(Collections.emptyList(), Collections.emptyList());

        private boolean hasAny() {
            return !this.hostileDots.isEmpty() || !this.passiveDots.isEmpty();
        }
    }

    private record MobDotData(float relativeDegrees, float distanceRatio, @Nullable Mob mob) {
    }

    private record ResolvedMarker(@NotNull String name, float relativeDegrees, boolean showAsNeedle, int color,
                                  @Nullable ResourceSupplier<ITexture> dotTexture,
                                  @Nullable ResourceSupplier<ITexture> needleTexture) {
    }

    private record DotBounds(int left, int top, int size) {
    }

    private record TextureHandle(ResourceLocation location, int width, int height, @NotNull AspectRatio aspectRatio) {
    }

    private void drawDeathNeedleStrips(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float centerX, int color) {
        int needleWidth = Math.max(1, Mth.floor(layout.width() * 0.006F));
        int half = Math.max(0, needleWidth / 2);
        float normalizedCenter = this.normalizeCenterForWrap(layout, centerX);
        this.drawNeedleStrip(graphics, layout, normalizedCenter - layout.width(), needleWidth, half, color);
        this.drawNeedleStrip(graphics, layout, normalizedCenter, needleWidth, half, color);
        this.drawNeedleStrip(graphics, layout, normalizedCenter + layout.width(), needleWidth, half, color);
    }

    private void drawNeedleStrip(@NotNull GuiGraphics graphics, @NotNull CompassLayout layout, float centerX, int width, int halfWidth, int color) {
        int xi = Mth.floor(centerX) - halfWidth;
        graphics.fill(xi, layout.y(), xi + width, layout.y() + layout.height(), color);
    }

    private float adjustDeathPointerRelative(float relative) {
        if (!this.hasLastDeathPointerRelative) {
            this.hasLastDeathPointerRelative = true;
            this.lastDeathPointerRelative = relative;
            return relative;
        }
        float adjusted = relative;
        while (adjusted - this.lastDeathPointerRelative > 180.0F) {
            adjusted -= 360.0F;
        }
        while (adjusted - this.lastDeathPointerRelative < -180.0F) {
            adjusted += 360.0F;
        }
        this.lastDeathPointerRelative = adjusted;
        return adjusted;
    }

    private float normalizeCenterForWrap(@NotNull CompassLayout layout, float centerX) {
        float width = Math.max(1, layout.width());
        if (width <= 0) {
            return layout.x();
        }
        float offset = centerX - layout.x();
        float normalized = offset % width;
        if (normalized < 0) {
            normalized += width;
        }
        return layout.x() + normalized;
    }

}
