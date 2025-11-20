package de.keksuccino.spiffyhud.customization.elements.playerfoodbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.fancymenu.util.resource.ResourceSupplier;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.spiffyhud.SpiffyUtils;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerFoodBarElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_SCALE_STRING = "1.0";
    private static final float DEFAULT_SCALE = 1.0F;
    private static final float MIN_SCALE = 0.2F;
    private static final float MAX_SCALE = 70.0F;
    private static final int BASE_ICON_PIXEL_SIZE = 8;
    private static final int BASE_SLOT_COUNT = 10;
    private static final long BLINK_DURATION_MS = 650L;
    private static final long EDITOR_PREVIEW_INTERVAL_MS = 10_000L;
    private static final FoodVisualStyle[] EDITOR_PREVIEW_STYLES = FoodVisualStyle.values();

    private final Minecraft minecraft = Minecraft.getInstance();
    private final RandomSource shakeRandom = RandomSource.create();

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;
    public int iconsPerRow = 10;
    public int iconGap = 1;
    @NotNull
    public String scaleMultiplier = DEFAULT_SCALE_STRING;
    public boolean blinkOnLoss = true;
    public boolean lowFoodShakeEnabled = true;
    public int lowFoodShakeThresholdIcons = 4;

    private final EnumMap<FoodTextureKind, ResourceSupplier<ITexture>> customTextures = new EnumMap<>(FoodTextureKind.class);

    private float lastRecordedFood = -1.0F;
    private int cachedTickCount = 0;
    private final Map<Integer, BlinkState> activeBlinkSlots = new HashMap<>();

    public PlayerFoodBarElement(@NotNull ElementBuilder<?, ?> builder) {
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

        this.updateBlinkState(data.currentFood);

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

        this.drawFood(graphics, aligned[0], aligned[1], data, metrics, scale);
        RenderingUtils.resetShaderColor(graphics);
    }

    private void drawFood(@NotNull GuiGraphics graphics, int originX, int originY, @NotNull PlayerData data, @NotNull RenderMetrics metrics, float scale) {

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(originX, originY);

        long now = System.currentTimeMillis();
        boolean shake = this.shouldShake(data.currentFood);
        float shakeStrengthBase = shake ? 0.9F : 0.0F;

        int totalSlots = metrics.totalSlots;
        for (int logicalIndex = totalSlots - 1; logicalIndex >= 0; logicalIndex--) {
            SlotPlacement placement = this.computeSlotPlacement(logicalIndex, metrics);
            FoodTextureKind textureKind = this.resolveTextureKind(logicalIndex, data, now);
            float appliedShake = ((shakeStrengthBase > 0.0F) && (textureKind != FoodTextureKind.EMPTY)) ? shakeStrengthBase : 0.0F;
            this.renderSingleIcon(graphics, metrics, placement, logicalIndex, textureKind, appliedShake, scale);
        }

        pose.popMatrix();
    }

    private FoodTextureKind resolveTextureKind(int logicalIndex, @NotNull PlayerData data, long now) {
        BlinkState blink = this.activeBlinkSlots.get(logicalIndex);
        if (blink != null) {
            if (now < blink.endTimeMs) {
                FoodTextureKind previousTexture = this.textureFromFill(blink.previousFill, data.visualStyle);
                if (previousTexture == FoodTextureKind.EMPTY) {
                    previousTexture = data.visualStyle.halfTexture;
                }
                return ((now / 120L) % 2L == 0L) ? previousTexture : FoodTextureKind.EMPTY;
            } else {
                this.activeBlinkSlots.remove(logicalIndex);
            }
        }

        float displayedFood = data.currentFood;
        float slotLowerBound = logicalIndex * 2.0F;
        float fillValue = displayedFood - slotLowerBound;
        return this.textureFromFill(fillValue, data.visualStyle);
    }

    private void renderSingleIcon(@NotNull GuiGraphics graphics, @NotNull RenderMetrics metrics, @NotNull SlotPlacement placement, int logicalIndex, @NotNull FoodTextureKind textureKind, float shakeStrengthBase, float scale) {

        float gap = this.iconGap;
        float baseSpacingX = (metrics.baseIconSize + gap) * scale;
        float baseSpacingY = (metrics.baseIconSize + gap) * scale;
        float baseX = placement.column * baseSpacingX;
        float baseY = placement.row * baseSpacingY;

        float offsetX = 0.0F;
        float offsetY = 0.0F;
        if ((shakeStrengthBase > 0.0F) && (textureKind != FoodTextureKind.EMPTY)) {
            float[] offsets = this.computeShakeOffset(logicalIndex, shakeStrengthBase);
            offsetX = offsets[0];
            offsetY = offsets[1];
        }

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(baseX + offsetX, baseY + offsetY);
        pose.scale(scale, scale);
        this.drawFoodTexture(graphics, textureKind, metrics.baseIconSize);
        pose.popMatrix();
    }

    private float[] computeShakeOffset(int logicalIndex, float shakeStrengthBase) {
        this.shakeRandom.setSeed(((long) this.cachedTickCount * 341873128712L) + (logicalIndex * 132897987541L));
        float dx = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        float dy = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        return new float[] { dx, dy };
    }

    private void drawFoodTexture(@NotNull GuiGraphics graphics, @NotNull FoodTextureKind kind, int size) {
        ResourceSupplier<ITexture> supplier = this.customTextures.get(kind);
        if (supplier != null) {
            try {
                ITexture texture = supplier.get();
                if ((texture != null) && texture.isReady() && (texture.getResourceLocation() != null)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, texture.getResourceLocation(), 0, 0, 0.0F, 0.0F, size, size, size, size, ARGB.white(this.opacity));
                    return;
                }
            } catch (Exception ex) {
                LOGGER.error("[SpiffyHUD] Failed to draw food texture {}", kind.name(), ex);
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
        int perRow = Math.max(1, this.iconsPerRow);
        int rowSize = Math.min(perRow, totalSlots);
        int rows = Math.max(1, (int) Math.ceil(totalSlots / (double) perRow));
        int gap = Math.max(0, this.iconGap);
        float scaledWidth = rowSize * BASE_ICON_PIXEL_SIZE * scale + Math.max(0, rowSize - 1) * gap * scale;
        float scaledHeight = rows * BASE_ICON_PIXEL_SIZE * scale + Math.max(0, rows - 1) * gap * scale;
        int bodyWidth = Math.max(1, Mth.ceil(scaledWidth));
        int bodyHeight = Math.max(1, Mth.ceil(scaledHeight));
        return new RenderMetrics(perRow, BASE_ICON_PIXEL_SIZE, totalSlots, bodyWidth, bodyHeight, rows, gap, gap);
    }

    private void updateBlinkState(float currentFood) {
        long now = System.currentTimeMillis();
        this.cleanupExpiredBlinks(now);

        if (!this.blinkOnLoss) {
            this.activeBlinkSlots.clear();
            this.lastRecordedFood = currentFood;
            return;
        }

        if (this.lastRecordedFood < 0.0F) {
            this.lastRecordedFood = currentFood;
            return;
        }

        if (currentFood < this.lastRecordedFood - 0.01F) {
            this.registerBlinkSlots(this.lastRecordedFood, currentFood, now);
        } else if (currentFood > this.lastRecordedFood + 0.01F) {
            this.activeBlinkSlots.clear();
        }
        this.lastRecordedFood = currentFood;
    }

    private boolean shouldShake(float currentFood) {
        if (!this.lowFoodShakeEnabled) {
            return false;
        }
        if (this.lowFoodShakeThresholdIcons <= 0) {
            return false;
        }
        float threshold = this.lowFoodShakeThresholdIcons * 2.0F;
        return currentFood <= threshold;
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
            FoodVisualStyle style = this.pickEditorPreviewStyle();
            return new PlayerData(13.0F, style);
        }

        Player player = this.minecraft.player;
        if (player == null) {
            return new PlayerData(10.0F, FoodVisualStyle.NORMAL);
        }
        FoodData foodData = player.getFoodData();
        float currentFood = foodData != null ? foodData.getFoodLevel() : 0.0F;
        FoodVisualStyle style = player.hasEffect(MobEffects.HUNGER) ? FoodVisualStyle.HUNGER : FoodVisualStyle.NORMAL;
        return new PlayerData(Math.max(0.0F, Math.min(20.0F, currentFood)), style);
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

    private FoodTextureKind textureFromFill(float fillValue, @NotNull FoodVisualStyle style) {
        if (fillValue >= 2.0F) return style.fullTexture;
        if (fillValue > 0.0F) return style.halfTexture;
        return FoodTextureKind.EMPTY;
    }

    private void registerBlinkSlots(float previousFood, float currentFood, long now) {
        for (int slot = 0; slot < BASE_SLOT_COUNT; slot++) {
            float prevFill = this.fillForSlot(previousFood, slot);
            float newFill = this.fillForSlot(currentFood, slot);
            if (prevFill > newFill + 0.01F && prevFill > 0.0F) {
                this.activeBlinkSlots.put(slot, new BlinkState(prevFill, now + BLINK_DURATION_MS));
            }
        }
    }

    private float fillForSlot(float value, int slot) {
        float slotLowerBound = slot * 2.0F;
        return Mth.clamp(value - slotLowerBound, 0.0F, 2.0F);
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

    private record BlinkState(float previousFill, long endTimeMs) {
    }

    private record RenderMetrics(int iconsPerRow, int baseIconSize, int totalSlots, int bodyWidth, int bodyHeight, int rows, int horizontalGap, int verticalGap) {
    }

    private SlotPlacement computeSlotPlacement(int logicalIndex, RenderMetrics metrics) {
        int perRow = metrics.iconsPerRow;
        int rawRow = logicalIndex / perRow;
        int row = isTopAligned() ? rawRow : (metrics.rows - 1 - rawRow);

        int topRowRawIndex = (metrics.totalSlots - 1) / perRow;
        int iconsInRow = (rawRow == topRowRawIndex) ? (metrics.totalSlots - rawRow * perRow) : perRow;
        if (iconsInRow <= 0) iconsInRow = perRow;

        int alignmentOffset = 0;
        if ((rawRow == topRowRawIndex) && isCenterAligned()) {
            alignmentOffset = (perRow - iconsInRow) / 2;
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
        final float currentFood;
        final FoodVisualStyle visualStyle;

        PlayerData(float currentFood, FoodVisualStyle visualStyle) {
            this.currentFood = currentFood;
            this.visualStyle = visualStyle;
        }
    }

    private enum FoodVisualStyle {
        NORMAL(FoodTextureKind.NORMAL_HALF, FoodTextureKind.NORMAL_FULL),
        HUNGER(FoodTextureKind.HUNGER_HALF, FoodTextureKind.HUNGER_FULL);

        private final FoodTextureKind halfTexture;
        private final FoodTextureKind fullTexture;

        FoodVisualStyle(FoodTextureKind halfTexture, FoodTextureKind fullTexture) {
            this.halfTexture = halfTexture;
            this.fullTexture = fullTexture;
        }
    }

    public enum FoodTextureKind {
        EMPTY("empty", 0x80202020),
        NORMAL_HALF("normal_half", 0xFFFFF5A3),
        NORMAL_FULL("normal_full", 0xFFFFD86F),
        HUNGER_HALF("hunger_half", 0xFFC0FF7F),
        HUNGER_FULL("hunger_full", 0xFF7ED957);

        private final String translationKeySuffix;
        private final int defaultColor;

        FoodTextureKind(String translationKeySuffix, int defaultColor) {
            this.translationKeySuffix = translationKeySuffix;
            this.defaultColor = defaultColor;
        }

        public String getTranslationKey() {
            return "spiffyhud.elements.player_food_bar.texture." + this.translationKeySuffix;
        }
    }

    public void setCustomTexture(@NotNull FoodTextureKind kind, @Nullable ResourceSupplier<ITexture> supplier) {
        if (supplier == null) {
            this.customTextures.remove(kind);
        } else {
            this.customTextures.put(kind, supplier);
        }
    }

    @Nullable
    public ResourceSupplier<ITexture> getCustomTexture(@NotNull FoodTextureKind kind) {
        return this.customTextures.get(kind);
    }

    @NotNull
    public Map<FoodTextureKind, ResourceSupplier<ITexture>> getCustomTextureMap() {
        return Collections.unmodifiableMap(this.customTextures);
    }

    private FoodVisualStyle pickEditorPreviewStyle() {
        long bucket = System.currentTimeMillis() / EDITOR_PREVIEW_INTERVAL_MS;
        int index = (int) (bucket % EDITOR_PREVIEW_STYLES.length);
        return EDITOR_PREVIEW_STYLES[Math.max(0, index)];
    }

}
