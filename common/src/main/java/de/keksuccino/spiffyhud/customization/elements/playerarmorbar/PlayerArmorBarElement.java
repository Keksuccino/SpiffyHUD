package de.keksuccino.spiffyhud.customization.elements.playerarmorbar;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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

public class PlayerArmorBarElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_SCALE_STRING = "1.0";
    private static final float DEFAULT_SCALE = 1.0F;
    private static final float MIN_SCALE = 0.2F;
    private static final float MAX_SCALE = 70.0F;
    private static final int BASE_ICON_PIXEL_SIZE = 8;
    private static final int BASE_SLOT_COUNT = 10;
    private static final long BLINK_DURATION_MS = 650L;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final RandomSource shakeRandom = RandomSource.create();

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;
    public int iconsPerRow = 10;
    public int iconGap = 1;
    @NotNull
    public String scaleMultiplier = DEFAULT_SCALE_STRING;
    public boolean blinkOnLoss = true;
    public boolean lowArmorShakeEnabled = true;
    public int lowArmorShakeThresholdIcons = 4;

    private final EnumMap<ArmorTextureKind, ResourceSupplier<ITexture>> customTextures = new EnumMap<>(ArmorTextureKind.class);

    private float lastRecordedArmor = -1.0F;
    private int cachedTickCount = 0;
    private final Map<Integer, BlinkState> activeBlinkSlots = new HashMap<>();

    public PlayerArmorBarElement(@NotNull ElementBuilder<?, ?> builder) {
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

        this.updateBlinkState(data.currentArmor);

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
        this.drawArmor(graphics, aligned[0], aligned[1], data, metrics, scale);
        RenderingUtils.resetShaderColor(graphics);
    }

    private void drawArmor(@NotNull GuiGraphics graphics, int originX, int originY, @NotNull PlayerData data,
                           @NotNull RenderMetrics metrics, float scale) {

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(originX, originY, 0);

        long now = System.currentTimeMillis();
        boolean shake = this.shouldShake(data.currentArmor);
        float shakeStrengthBase = shake ? 0.9F : 0.0F;

        int totalSlots = metrics.totalSlots;
        for (int logicalIndex = totalSlots - 1; logicalIndex >= 0; logicalIndex--) {
            SlotPlacement placement = this.computeSlotPlacement(logicalIndex, metrics);
            ArmorTextureKind textureKind = this.resolveTextureKind(logicalIndex, data, now);
            float appliedShake = ((shakeStrengthBase > 0.0F) && (textureKind != ArmorTextureKind.EMPTY)) ? shakeStrengthBase : 0.0F;
            this.renderSingleIcon(graphics, metrics, placement, logicalIndex, textureKind, appliedShake, scale);
        }

        pose.popPose();
    }

    private ArmorTextureKind resolveTextureKind(int logicalIndex, @NotNull PlayerData data, long now) {
        BlinkState blink = this.activeBlinkSlots.get(logicalIndex);
        if (blink != null) {
            if (now < blink.endTimeMs) {
                ArmorTextureKind previousTexture = this.textureFromFill(blink.previousFill);
                if (previousTexture == ArmorTextureKind.EMPTY) {
                    previousTexture = ArmorTextureKind.HALF;
                }
                return ((now / 120L) % 2L == 0L) ? previousTexture : ArmorTextureKind.EMPTY;
            } else {
                this.activeBlinkSlots.remove(logicalIndex);
            }
        }

        float displayedArmor = data.currentArmor;
        float slotLowerBound = logicalIndex * 2.0F;
        float fillValue = displayedArmor - slotLowerBound;
        return this.textureFromFill(fillValue);
    }

    private void renderSingleIcon(@NotNull GuiGraphics graphics, @NotNull RenderMetrics metrics, @NotNull SlotPlacement placement,
                                   int logicalIndex, @NotNull ArmorTextureKind textureKind, float shakeStrengthBase, float scale) {

        float gap = this.iconGap;
        float baseSpacingX = (metrics.baseIconSize + gap) * scale;
        float baseSpacingY = (metrics.baseIconSize + gap) * scale;
        float baseX = placement.column * baseSpacingX;
        float baseY = placement.row * baseSpacingY;

        float offsetX = 0.0F;
        float offsetY = 0.0F;
        if ((shakeStrengthBase > 0.0F) && (textureKind != ArmorTextureKind.EMPTY)) {
            float[] offsets = this.computeShakeOffset(logicalIndex, shakeStrengthBase);
            offsetX = offsets[0];
            offsetY = offsets[1];
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(baseX + offsetX, baseY + offsetY, 0);
        pose.scale(scale, scale, 1.0F);
        this.drawArmorTexture(graphics, textureKind, metrics.baseIconSize);
        pose.popPose();
    }

    private float[] computeShakeOffset(int logicalIndex, float shakeStrengthBase) {
        this.shakeRandom.setSeed(((long) this.cachedTickCount * 341873128712L) + (logicalIndex * 132897987541L));
        float dx = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        float dy = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        return new float[] { dx, dy };
    }

    private void drawArmorTexture(@NotNull GuiGraphics graphics, @NotNull ArmorTextureKind kind, int size) {
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
                LOGGER.error("[SpiffyHUD] Failed to draw armor texture {}", kind.name(), ex);
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

    private void updateBlinkState(float currentArmor) {
        long now = System.currentTimeMillis();
        this.cleanupExpiredBlinks(now);

        if (!this.blinkOnLoss) {
            this.activeBlinkSlots.clear();
            this.lastRecordedArmor = currentArmor;
            return;
        }

        if (this.lastRecordedArmor < 0.0F) {
            this.lastRecordedArmor = currentArmor;
            return;
        }

        if (currentArmor < this.lastRecordedArmor - 0.01F) {
            this.registerBlinkSlots(this.lastRecordedArmor, currentArmor, now);
        } else if (currentArmor > this.lastRecordedArmor + 0.01F) {
            this.activeBlinkSlots.clear();
        }
        this.lastRecordedArmor = currentArmor;
    }

    private boolean shouldShake(float currentArmor) {
        if (!this.lowArmorShakeEnabled) {
            return false;
        }
        if (this.lowArmorShakeThresholdIcons <= 0) {
            return false;
        }
        float threshold = this.lowArmorShakeThresholdIcons * 2.0F;
        return currentArmor <= threshold;
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
            return new PlayerData(13.0F);
        }

        Player player = this.minecraft.player;
        if (player == null) {
            return new PlayerData(0.0F);
        }
        float currentArmor = player.getArmorValue();
        return new PlayerData(Math.max(0.0F, Math.min(BASE_SLOT_COUNT * 2.0F, currentArmor)));
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

    private ArmorTextureKind textureFromFill(float fillValue) {
        if (fillValue >= 2.0F) return ArmorTextureKind.FULL;
        if (fillValue > 0.0F) return ArmorTextureKind.HALF;
        return ArmorTextureKind.EMPTY;
    }

    private void registerBlinkSlots(float previousArmor, float currentArmor, long now) {
        for (int slot = 0; slot < BASE_SLOT_COUNT; slot++) {
            float prevFill = this.fillForSlot(previousArmor, slot);
            float newFill = this.fillForSlot(currentArmor, slot);
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

    private record RenderMetrics(int iconsPerRow, int baseIconSize, int totalSlots, int bodyWidth, int bodyHeight,
                                 int rows, int horizontalGap, int verticalGap) {
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
        final float currentArmor;

        PlayerData(float currentArmor) {
            this.currentArmor = currentArmor;
        }
    }

    public enum ArmorTextureKind {
        EMPTY("empty", 0x80202020),
        HALF("half", 0xFF8AA6C9),
        FULL("full", 0xFFBACBEA);

        private final String translationKeySuffix;
        private final int defaultColor;

        ArmorTextureKind(String translationKeySuffix, int defaultColor) {
            this.translationKeySuffix = translationKeySuffix;
            this.defaultColor = defaultColor;
        }

        public String getTranslationKey() {
            return "spiffyhud.elements.player_armor_bar.texture." + this.translationKeySuffix;
        }
    }

    public void setCustomTexture(@NotNull ArmorTextureKind kind, @Nullable ResourceSupplier<ITexture> supplier) {
        if (supplier == null) {
            this.customTextures.remove(kind);
        } else {
            this.customTextures.put(kind, supplier);
        }
    }

    @Nullable
    public ResourceSupplier<ITexture> getCustomTexture(@NotNull ArmorTextureKind kind) {
        return this.customTextures.get(kind);
    }

    @NotNull
    public Map<ArmorTextureKind, ResourceSupplier<ITexture>> getCustomTextureMap() {
        return Collections.unmodifiableMap(this.customTextures);
    }

}
