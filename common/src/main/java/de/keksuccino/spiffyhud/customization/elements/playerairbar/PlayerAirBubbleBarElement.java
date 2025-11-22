package de.keksuccino.spiffyhud.customization.elements.playerairbar;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.fancymenu.util.rendering.gui.GuiGraphics;
import de.keksuccino.fancymenu.util.resource.ResourceSupplier;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerAirBubbleBarElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_SCALE_STRING = "1.0";
    private static final float DEFAULT_SCALE = 1.0F;
    private static final float MIN_SCALE = 0.2F;
    private static final float MAX_SCALE = 70.0F;
    private static final int BASE_BUBBLE_PIXEL_SIZE = 8;
    private static final int BASE_SLOT_COUNT = 10;
    public static final int DEFAULT_POPPING_DURATION_MS = 217;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final RandomSource shakeRandom = RandomSource.create();

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;
    public int bubblesPerRow = 10;
    public int bubbleGap = 1;
    @NotNull
    public String scaleMultiplier = DEFAULT_SCALE_STRING;
    public boolean blinkOnLoss = true;
    public boolean lowAirShakeEnabled = true;
    public int lowAirShakeThresholdBubbles = 4;
    public int poppingDurationMs = DEFAULT_POPPING_DURATION_MS;

    private final EnumMap<AirTextureKind, ResourceSupplier<ITexture>> customTextures = new EnumMap<>(AirTextureKind.class);

    private float lastRecordedAirAmount = -1.0F;
    private int lastRecordedMaxAir = -1;
    private int cachedTickCount = 0;
    private final Map<Integer, BlinkState> activeBlinkSlots = new HashMap<>();

    public PlayerAirBubbleBarElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
        this.stickyAnchor = true;
        this.stayOnScreen = false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        this.cachedTickCount = safeTickCount();
        PlayerData data = this.collectPlayerData();
        float scale = this.resolveScale();
        RenderMetrics metrics = this.computeMetrics(scale);

        this.baseWidth = Math.max(1, metrics.bodyWidth);
        this.baseHeight = Math.max(1, metrics.bodyHeight);

        this.updateBlinkState(data);

        if (!this.shouldRender()) {
            return;
        }

        Integer[] aligned = SpiffyAlignment.calculateElementBodyPosition(
                this.spiffyAlignment,
                this.getAbsoluteX(),
                this.getAbsoluteY(),
                this.getAbsoluteWidth(),
                this.getAbsoluteHeight(),
                metrics.bodyWidth,
                metrics.bodyHeight
        );

        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.opacity);
        this.drawBubbles(graphics, aligned[0], aligned[1], data, metrics, scale);
        RenderingUtils.resetShaderColor(graphics);
    }

    private void drawBubbles(@NotNull GuiGraphics graphics, int originX, int originY, @NotNull PlayerData data,
                             @NotNull RenderMetrics metrics, float scale) {

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(originX, originY, 0);

        long now = System.currentTimeMillis();
        boolean shake = this.shouldShake(data);
        float shakeStrengthBase = shake ? 0.9F : 0.0F;

        int totalSlots = metrics.totalSlots;
        for (int logicalIndex = totalSlots - 1; logicalIndex >= 0; logicalIndex--) {
            SlotPlacement placement = this.computeSlotPlacement(logicalIndex, metrics);
            AirTextureKind textureKind = this.resolveTextureKind(logicalIndex, data, now);
            float appliedShake = ((shakeStrengthBase > 0.0F) && (textureKind != AirTextureKind.EMPTY)) ? shakeStrengthBase : 0.0F;
            this.renderSingleBubble(graphics, metrics, placement, logicalIndex, textureKind, appliedShake, scale);
        }

        pose.popPose();
    }

    private AirTextureKind resolveTextureKind(int logicalIndex, @NotNull PlayerData data, long now) {
        BlinkState blink = this.activeBlinkSlots.get(logicalIndex);
        if (blink != null) {
            if (now < blink.endTimeMs) {
                return AirTextureKind.POPPING;
            } else {
                this.activeBlinkSlots.remove(logicalIndex);
            }
        }

        int fullCutoff = data.fullBubbles;
        int poppingCutoff = data.fullBubbles + data.poppingBubbles;
        if (logicalIndex < fullCutoff) {
            return AirTextureKind.FULL;
        }
        if (logicalIndex < poppingCutoff) {
            return AirTextureKind.POPPING;
        }
        return AirTextureKind.EMPTY;
    }

    private void renderSingleBubble(@NotNull GuiGraphics graphics, @NotNull RenderMetrics metrics, @NotNull SlotPlacement placement,
                                    int logicalIndex, @NotNull AirTextureKind textureKind, float shakeStrengthBase, float scale) {

        float gap = this.bubbleGap;
        float baseSpacingX = (metrics.baseBubbleSize + gap) * scale;
        float baseSpacingY = (metrics.baseBubbleSize + gap) * scale;
        float baseX = placement.column * baseSpacingX;
        float baseY = placement.row * baseSpacingY;

        float offsetX = 0.0F;
        float offsetY = 0.0F;
        if ((shakeStrengthBase > 0.0F) && (textureKind != AirTextureKind.EMPTY)) {
            float[] offsets = this.computeShakeOffset(logicalIndex, shakeStrengthBase);
            offsetX = offsets[0];
            offsetY = offsets[1];
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(baseX + offsetX, baseY + offsetY, 0);
        pose.scale(scale, scale, 1.0F);
        this.drawBubbleTexture(graphics, textureKind, metrics.baseBubbleSize);
        pose.popPose();
    }

    private float[] computeShakeOffset(int logicalIndex, float shakeStrengthBase) {
        this.shakeRandom.setSeed(((long) this.cachedTickCount * 341873128712L) + (long) logicalIndex * 132897987541L);
        float dx = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        float dy = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        return new float[] { dx, dy };
    }

    private void drawBubbleTexture(@NotNull GuiGraphics graphics, @NotNull AirTextureKind kind, int size) {
        ResourceSupplier<ITexture> supplier = this.customTextures.get(kind);
        if (supplier != null) {
            try {
                ITexture texture = supplier.get();
                if ((texture != null) && texture.isReady() && (texture.getResourceLocation() != null)) {
                    RenderSystem.enableBlend();
                    graphics.blit(texture.getResourceLocation(), 0, 0, 0.0F, 0.0F, size, size, size, size);
                    return;
                }
            } catch (Exception ex) {
                LOGGER.error("[SpiffyHUD] Failed to draw air bubble texture {}", kind.name(), ex);
            }
        }
        graphics.fill(0, 0, size, size, this.applyOpacity(kind.defaultColor));
    }

    private int applyOpacity(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        int rgb = argb & 0x00FFFFFF;
        int adjustedAlpha = (int) Math.max(0, Math.min(255, alpha * this.opacity));
        return (adjustedAlpha << 24) | rgb;
    }

    private RenderMetrics computeMetrics(float scale) {
        int totalSlots = BASE_SLOT_COUNT;
        int perRow = Math.max(1, this.bubblesPerRow);
        int rowSize = Math.min(perRow, totalSlots);
        int rows = Math.max(1, (int) Math.ceil(totalSlots / (double) perRow));
        int gap = Math.max(0, this.bubbleGap);
        float scaledWidth = rowSize * BASE_BUBBLE_PIXEL_SIZE * scale + Math.max(0, rowSize - 1) * gap * scale;
        float scaledHeight = rows * BASE_BUBBLE_PIXEL_SIZE * scale + Math.max(0, rows - 1) * gap * scale;
        int bodyWidth = Math.max(1, Mth.ceil(scaledWidth));
        int bodyHeight = Math.max(1, Mth.ceil(scaledHeight));
        return new RenderMetrics(perRow, BASE_BUBBLE_PIXEL_SIZE, totalSlots, bodyWidth, bodyHeight, rows, gap, gap);
    }

    private void updateBlinkState(@NotNull PlayerData data) {
        long now = System.currentTimeMillis();
        this.cleanupExpiredBlinks(now);

        float currentAir = data.currentAir;
        int currentMaxAir = data.maxAir;
        if (!this.blinkOnLoss || this.poppingDurationMs <= 0) {
            this.activeBlinkSlots.clear();
            this.lastRecordedAirAmount = currentAir;
            this.lastRecordedMaxAir = currentMaxAir;
            return;
        }

        if (this.lastRecordedAirAmount < 0.0F) {
            this.lastRecordedAirAmount = currentAir;
            this.lastRecordedMaxAir = currentMaxAir;
            return;
        }

        if (currentAir < this.lastRecordedAirAmount - 0.01F) {
            int previousMax = (this.lastRecordedMaxAir > 0) ? this.lastRecordedMaxAir : currentMaxAir;
            this.registerBlinkSlots(this.lastRecordedAirAmount, previousMax, currentAir, currentMaxAir, now);
        } else if (currentAir > this.lastRecordedAirAmount + 0.01F) {
            this.activeBlinkSlots.clear();
        }
        this.lastRecordedAirAmount = currentAir;
        this.lastRecordedMaxAir = currentMaxAir;
    }

    private boolean shouldShake(@NotNull PlayerData data) {
        if (!this.lowAirShakeEnabled) {
            return false;
        }
        if (this.lowAirShakeThresholdBubbles <= 0) {
            return false;
        }
        if (data.airPerSlot <= 0.0F) {
            return false;
        }
        float threshold = this.lowAirShakeThresholdBubbles * data.airPerSlot;
        return data.currentAir <= threshold;
    }

    private int safeTickCount() {
        try {
            return SpiffyUtils.getGuiAccessor().getTickCount_Spiffy();
        } catch (Exception ex) {
            return 0;
        }
    }

    private PlayerData collectPlayerData() {
        if (isEditor()) {
            int fakeMaxAir = 300;
            int fakeCurrentAir = (int) (fakeMaxAir * 0.7F);
            return new PlayerData(fakeCurrentAir, fakeMaxAir);
        }

        Player player = this.minecraft.player;
        if (player == null) {
            int defaultMax = 300;
            return new PlayerData(defaultMax, defaultMax);
        }

        int maxAir = Math.max(1, player.getMaxAirSupply());
        int currentAir = Mth.clamp(player.getAirSupply(), 0, maxAir);
        return new PlayerData(currentAir, maxAir);
    }

    private float resolveScale() {
        String raw = this.scaleMultiplier;
        if (raw == null || raw.isBlank()) {
            return DEFAULT_SCALE;
        }
        String replaced = PlaceholderParser.replacePlaceholders(raw).trim();
        if (MathUtils.isFloat(replaced)) {
            try {
                float parsed = Float.parseFloat(replaced);
                if (!Float.isFinite(parsed)) {
                    return DEFAULT_SCALE;
                }
                return Mth.clamp(parsed, MIN_SCALE, MAX_SCALE);
            } catch (NumberFormatException ignored) {}
        }
        return DEFAULT_SCALE;
    }

    private void registerBlinkSlots(float previousAir, int previousMaxAir, float currentAir, int currentMaxAir, long now) {
        int duration = Math.max(0, this.poppingDurationMs);
        if (duration <= 0) {
            return;
        }
        BubbleCounts previousCounts = calculateBubbleCounts(previousAir, previousMaxAir);
        BubbleCounts currentCounts = calculateBubbleCounts(currentAir, currentMaxAir);
        for (int slot = 0; slot < BASE_SLOT_COUNT; slot++) {
            boolean wasFull = slot < previousCounts.full();
            boolean isFull = slot < currentCounts.full();
            if (wasFull && !isFull) {
                this.activeBlinkSlots.put(slot, new BlinkState(now + duration));
            }
        }
    }

    private static BubbleCounts calculateBubbleCounts(float airValue, int maxAir) {
        int safeMaxAir = Math.max(1, maxAir);
        int clampedAir = Mth.clamp(Mth.floor(airValue), 0, safeMaxAir);
        double ratio = (double) BASE_SLOT_COUNT / (double) safeMaxAir;
        int full = Mth.ceil(((double) clampedAir - 2.0D) * ratio);
        int popping = Mth.ceil((double) clampedAir * ratio) - full;
        full = Mth.clamp(full, 0, BASE_SLOT_COUNT);
        popping = Mth.clamp(popping, 0, BASE_SLOT_COUNT - full);
        return new BubbleCounts(full, popping);
    }

    private void cleanupExpiredBlinks(long now) {
        Iterator<Map.Entry<Integer, BlinkState>> iterator = this.activeBlinkSlots.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, BlinkState> entry = iterator.next();
            if (now >= entry.getValue().endTimeMs) {
                iterator.remove();
            }
        }
    }

    private record BlinkState(long endTimeMs) {
    }

    private record BubbleCounts(int full, int popping) {
    }

    private record RenderMetrics(int bubblesPerRow, int baseBubbleSize, int totalSlots, int bodyWidth, int bodyHeight,
                                 int rows, int horizontalGap, int verticalGap) {
    }

    private SlotPlacement computeSlotPlacement(int logicalIndex, RenderMetrics metrics) {
        int perRow = metrics.bubblesPerRow;
        int rawRow = logicalIndex / perRow;
        int row = isTopAligned() ? rawRow : (metrics.rows - 1 - rawRow);

        int topRowRawIndex = (metrics.totalSlots - 1) / perRow;
        int bubblesInRow = (rawRow == topRowRawIndex) ? (metrics.totalSlots - rawRow * perRow) : perRow;
        if (bubblesInRow <= 0) bubblesInRow = perRow;

        int alignmentOffset = 0;
        if ((rawRow == topRowRawIndex) && isCenterAligned()) {
            alignmentOffset = (perRow - bubblesInRow) / 2;
        }

        int rawColumn = logicalIndex % perRow;
        int column = isRightAligned() ? (perRow - 1 - rawColumn) : rawColumn;
        column = Math.max(0, column + alignmentOffset);

        return new SlotPlacement(column, Math.max(0, row));
    }

    private boolean isTopAligned() {
        return switch (this.spiffyAlignment) {
            case TOP_LEFT, TOP_CENTERED, TOP_RIGHT -> true;
            default -> false;
        };
    }

    private boolean isCenterAligned() {
        return switch (this.spiffyAlignment) {
            case TOP_CENTERED, MID_CENTERED, BOTTOM_CENTERED -> true;
            default -> false;
        };
    }

    private boolean isRightAligned() {
        return switch (this.spiffyAlignment) {
            case TOP_RIGHT, MID_RIGHT, BOTTOM_RIGHT -> true;
            default -> false;
        };
    }

    private record SlotPlacement(int column, int row) {
    }

    private static class PlayerData {
        final float currentAir;
        final int maxAir;
        final float airPerSlot;
        final int fullBubbles;
        final int poppingBubbles;

        PlayerData(int currentAir, int maxAir) {
            this.maxAir = Math.max(1, maxAir);
            this.currentAir = Mth.clamp(currentAir, 0, this.maxAir);
            this.airPerSlot = this.maxAir / (float) BASE_SLOT_COUNT;
            BubbleCounts counts = calculateBubbleCounts(this.currentAir, this.maxAir);
            this.fullBubbles = counts.full();
            this.poppingBubbles = counts.popping();
        }
    }

    public enum AirTextureKind {
        EMPTY("empty", 0x80232332),
        FULL("full", 0xFF78C3FF),
        POPPING("popping", 0xFFBEE0FF);

        private final String translationKeySuffix;
        private final int defaultColor;

        AirTextureKind(String translationKeySuffix, int defaultColor) {
            this.translationKeySuffix = translationKeySuffix;
            this.defaultColor = defaultColor;
        }

        public String getTranslationKey() {
            return "spiffyhud.elements.player_air_bubble_bar.texture." + this.translationKeySuffix;
        }
    }

    public void setCustomTexture(@NotNull AirTextureKind kind, @Nullable ResourceSupplier<ITexture> supplier) {
        if (supplier == null) {
            this.customTextures.remove(kind);
        } else {
            this.customTextures.put(kind, supplier);
        }
    }

    @Nullable
    public ResourceSupplier<ITexture> getCustomTexture(@NotNull AirTextureKind kind) {
        return this.customTextures.get(kind);
    }

    @NotNull
    public Map<AirTextureKind, ResourceSupplier<ITexture>> getCustomTextureMap() {
        return Collections.unmodifiableMap(this.customTextures);
    }
}
