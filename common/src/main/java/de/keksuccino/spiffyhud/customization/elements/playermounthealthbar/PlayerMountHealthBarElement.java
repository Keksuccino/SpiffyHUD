package de.keksuccino.spiffyhud.customization.elements.playermounthealthbar;

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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

public class PlayerMountHealthBarElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_SCALE_STRING = "1.0";
    private static final float DEFAULT_SCALE = 1.0F;
    private static final float MIN_SCALE = 0.2F;
    private static final float MAX_SCALE = 70.0F;
    private static final int BASE_HEART_PIXEL_SIZE = 8;
    private static final long BLINK_DURATION_MS = 650L;
    private static final long EDITOR_PREVIEW_INTERVAL_MS = 10_000L;
    private static final HeartVisualStyle[] EDITOR_PREVIEW_STYLES = HeartVisualStyle.values();

    private final Minecraft minecraft = Minecraft.getInstance();
    private final RandomSource shakeRandom = RandomSource.create();

    @NotNull
    public SpiffyAlignment spiffyAlignment = SpiffyAlignment.TOP_LEFT;
    public int heartsPerRow = 10;
    public int heartGap = 1;
    @NotNull
    public String scaleMultiplier = DEFAULT_SCALE_STRING;
    public boolean blinkOnLoss = true;
    public boolean lowHealthShakeEnabled = true;
    public int lowHealthShakeThresholdHearts = 4;

    private final EnumMap<HeartTextureKind, ResourceSupplier<ITexture>> customTextures = new EnumMap<>(HeartTextureKind.class);

    private float lastRecordedHealth = -1.0F;
    private int cachedTickCount = 0;
    private final Map<Integer, BlinkState> activeBlinkSlots = new HashMap<>();

    public PlayerMountHealthBarElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
        this.stickyAnchor = true;
        this.stayOnScreen = false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        this.cachedTickCount = safeTickCount();
        MountData data = this.collectMountData();
        float scale = this.resolveScale();
        RenderMetrics metrics = this.computeMetrics(data, scale);

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
        this.drawHearts(graphics, aligned[0], aligned[1], data, metrics, scale);
        RenderingUtils.resetShaderColor(graphics);
    }

    private void drawHearts(@NotNull GuiGraphics graphics, int originX, int originY, @NotNull MountData data,
                            @NotNull RenderMetrics metrics, float scale) {

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(originX, originY, 0);

        long now = System.currentTimeMillis();
        boolean shake = this.shouldShake(data);
        float shakeStrengthBase = shake ? 0.9F : 0.0F;

        int totalSlots = data.baseHeartSlots + data.absorptionSlots;
        for (int logicalIndex = totalSlots - 1; logicalIndex >= 0; logicalIndex--) {
            boolean isAbsorption = logicalIndex >= data.baseHeartSlots;
            SlotPlacement placement = this.computeSlotPlacement(logicalIndex, metrics);

            HeartTextureKind textureKind;
            if (isAbsorption) {
                int absorptionSlot = logicalIndex - data.baseHeartSlots;
                float absorptionValue = data.absorption - (absorptionSlot * 2.0F);
                if (absorptionValue <= 0.0F) continue;
                textureKind = (absorptionValue >= 2.0F) ? HeartTextureKind.ABSORPTION_FULL : HeartTextureKind.ABSORPTION_HALF;
            } else {
                textureKind = this.resolveBaseTexture(logicalIndex, data, now);
            }

            float appliedShake = ((shakeStrengthBase > 0.0F) && (textureKind != HeartTextureKind.EMPTY)) ? shakeStrengthBase : 0.0F;
            this.renderSingleHeart(graphics, metrics, placement, logicalIndex, textureKind, appliedShake, scale);
        }

        pose.popPose();
    }

    private HeartTextureKind resolveBaseTexture(int logicalIndex, @NotNull MountData data, long now) {
        BlinkState blink = this.activeBlinkSlots.get(logicalIndex);
        if (blink != null) {
            if (now < blink.endTimeMs) {
                HeartTextureKind previous = this.textureFromFill(blink.previousFill, data.visualStyle);
                if (previous == HeartTextureKind.EMPTY) {
                    previous = data.visualStyle.halfTexture;
                }
                return ((now / 120L) % 2L == 0L) ? previous : HeartTextureKind.EMPTY;
            } else {
                this.activeBlinkSlots.remove(logicalIndex);
            }
        }
        float displayedHealth = data.currentHealth;
        float heartLowerBound = logicalIndex * 2.0F;
        float fillValue = displayedHealth - heartLowerBound;

        if ((logicalIndex == data.baseHeartSlots - 1) && (data.currentHealth >= data.maxHealth - 0.01F)) {
            return data.visualStyle.fullTexture;
        }

        return this.textureFromFill(fillValue, data.visualStyle);
    }

    private void renderSingleHeart(@NotNull GuiGraphics graphics, @NotNull RenderMetrics metrics, @NotNull SlotPlacement placement,
                                   int logicalIndex, @NotNull HeartTextureKind textureKind, float shakeStrengthBase, float scale) {

        float gap = this.heartGap;
        float baseSpacingX = (metrics.baseHeartSize + gap) * scale;
        float baseSpacingY = (metrics.baseHeartSize + gap) * scale;
        float baseX = placement.column * baseSpacingX;
        float baseY = placement.row * baseSpacingY;

        float offsetX = 0.0F;
        float offsetY = 0.0F;
        if ((shakeStrengthBase > 0.0F) && (textureKind != HeartTextureKind.EMPTY)) {
            float[] offsets = this.computeShakeOffset(logicalIndex, shakeStrengthBase);
            offsetX = offsets[0];
            offsetY = offsets[1];
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(baseX + offsetX, baseY + offsetY, 0);
        pose.scale(scale, scale, 1.0F);
        this.drawHeartTexture(graphics, textureKind, metrics.baseHeartSize);
        pose.popPose();
    }

    private float[] computeShakeOffset(int logicalIndex, float shakeStrengthBase) {
        this.shakeRandom.setSeed(((long) this.cachedTickCount * 341873128712L) + (logicalIndex * 132897987541L));
        float dx = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        float dy = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        return new float[] { dx, dy };
    }

    private void drawHeartTexture(@NotNull GuiGraphics graphics, @NotNull HeartTextureKind kind, int size) {
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
                LOGGER.error("[SpiffyHUD] Failed to draw heart texture {}", kind.name(), ex);
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

    private RenderMetrics computeMetrics(@NotNull MountData data, float scale) {
        int totalSlots = Math.max(1, data.baseHeartSlots + data.absorptionSlots);
        int heartsPerRowClamped = Math.max(1, this.heartsPerRow);
        int rowSize = Math.min(heartsPerRowClamped, totalSlots);
        int rows = Math.max(1, (int) Math.ceil(totalSlots / (double) heartsPerRowClamped));
        int gap = Math.max(0, this.heartGap);
        float scaledWidth = rowSize * BASE_HEART_PIXEL_SIZE * scale + Math.max(0, rowSize - 1) * gap * scale;
        float scaledHeight = rows * BASE_HEART_PIXEL_SIZE * scale + Math.max(0, rows - 1) * gap * scale;
        int bodyWidth = Math.max(1, Mth.ceil(scaledWidth));
        int bodyHeight = Math.max(1, Mth.ceil(scaledHeight));
        return new RenderMetrics(
                heartsPerRowClamped,
                BASE_HEART_PIXEL_SIZE,
                Math.max(1, totalSlots),
                bodyWidth,
                bodyHeight,
                rows,
                gap,
                gap
        );
    }

    private void updateBlinkState(@NotNull MountData data) {
        float currentHealth = data.currentHealth;
        int baseHeartSlots = data.baseHeartSlots;
        long now = System.currentTimeMillis();
        this.cleanupExpiredBlinks(now);

        if (!data.hasMountedEntity) {
            this.activeBlinkSlots.clear();
            this.lastRecordedHealth = currentHealth;
            return;
        }

        if (!this.blinkOnLoss || baseHeartSlots <= 0) {
            this.activeBlinkSlots.clear();
            this.lastRecordedHealth = currentHealth;
            return;
        }

        if (this.lastRecordedHealth < 0.0F) {
            this.lastRecordedHealth = currentHealth;
            return;
        }

        if (currentHealth < this.lastRecordedHealth - 0.01F) {
            this.registerBlinkSlots(this.lastRecordedHealth, currentHealth, baseHeartSlots, now);
        } else if (currentHealth > this.lastRecordedHealth + 0.01F) {
            this.activeBlinkSlots.clear();
        }
        this.lastRecordedHealth = currentHealth;
    }

    private boolean shouldShake(@NotNull MountData data) {
        if (!this.lowHealthShakeEnabled || !data.hasMountedEntity) {
            return false;
        }
        if (this.lowHealthShakeThresholdHearts <= 0) {
            return false;
        }
        float threshold = this.lowHealthShakeThresholdHearts * 2.0F;
        return data.currentHealth <= threshold;
    }

    private int safeTickCount() {
        try {
            return SpiffyUtils.getGuiAccessor().getTickCount_Spiffy();
        } catch (Exception ex) {
            return 0;
        }
    }

    private MountData collectMountData() {
        if (isEditor()) {
            HeartVisualStyle style = this.pickEditorPreviewStyle();
            return new MountData(32.0F, 40.0F, 0.0F, style);
        }

        LivingEntity mount = this.resolveMountedEntity();
        if (mount == null) {
            return new MountData(0.0F, 20.0F, 0.0F, HeartVisualStyle.NORMAL, false);
        }

        float currentHealth = Math.max(0.0F, mount.getHealth());
        float maxHealth = Math.max(2.0F, mount.getMaxHealth());
        float absorption = Math.max(0.0F, mount.getAbsorptionAmount());
        HeartVisualStyle style = this.resolveVisualStyle(mount);
        return new MountData(currentHealth, maxHealth, absorption, style);
    }

    @Nullable
    private LivingEntity resolveMountedEntity() {
        Player player = this.minecraft.player;
        if (player == null) {
            return null;
        }
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    private HeartVisualStyle resolveVisualStyle(@NotNull LivingEntity entity) {
        if (entity.hasEffect(MobEffects.WITHER)) {
            return HeartVisualStyle.WITHER;
        }
        if (entity.hasEffect(MobEffects.POISON)) {
            return HeartVisualStyle.POISON;
        }
        if (entity.getTicksFrozen() > 0) {
            return HeartVisualStyle.FROZEN;
        }
        if (entity.isOnFire()) {
            return HeartVisualStyle.BURNING;
        }
        return HeartVisualStyle.NORMAL;
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

    private HeartTextureKind textureFromFill(float fillValue, @NotNull HeartVisualStyle style) {
        if (fillValue >= 2.0F) return style.fullTexture;
        if (fillValue > 0.0F) return style.halfTexture;
        return HeartTextureKind.EMPTY;
    }

    private void registerBlinkSlots(float previousHealth, float currentHealth, int baseHeartSlots, long now) {
        for (int slot = 0; slot < baseHeartSlots; slot++) {
            float prevFill = this.fillForSlot(previousHealth, slot);
            float newFill = this.fillForSlot(currentHealth, slot);
            if (prevFill > newFill + 0.01F && prevFill > 0.0F) {
                this.activeBlinkSlots.put(slot, new BlinkState(prevFill, now + BLINK_DURATION_MS));
            }
        }
    }

    private float fillForSlot(float value, int slot) {
        float heartLowerBound = slot * 2.0F;
        return Mth.clamp(value - heartLowerBound, 0.0F, 2.0F);
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

    private record RenderMetrics(int heartsPerRow, int baseHeartSize, int totalSlots, int bodyWidth, int bodyHeight,
                                 int rows, int horizontalGap, int verticalGap) {
    }

    private SlotPlacement computeSlotPlacement(int logicalIndex, RenderMetrics metrics) {
        int perRow = metrics.heartsPerRow;
        int rawRow = logicalIndex / perRow;
        int row = isTopAligned() ? rawRow : (metrics.rows - 1 - rawRow);

        int topRowRawIndex = (metrics.totalSlots - 1) / perRow;
        int heartsInRow = (rawRow == topRowRawIndex) ? (metrics.totalSlots - rawRow * perRow) : perRow;
        if (heartsInRow <= 0) heartsInRow = perRow;

        int alignmentOffset = 0;
        if ((rawRow == topRowRawIndex) && isCenterAligned()) {
            alignmentOffset = (perRow - heartsInRow) / 2;
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

    private static class MountData {
        final float currentHealth;
        final float maxHealth;
        final float absorption;
        final HeartVisualStyle visualStyle;
        final int baseHeartSlots;
        final int absorptionSlots;
        final boolean hasMountedEntity;

        MountData(float currentHealth, float maxHealth, float absorption, HeartVisualStyle style) {
            this(currentHealth, maxHealth, absorption, style, true);
        }

        MountData(float currentHealth, float maxHealth, float absorption, HeartVisualStyle style, boolean hasMountedEntity) {
            this.currentHealth = Math.max(0.0F, currentHealth);
            this.maxHealth = Math.max(2.0F, maxHealth);
            this.absorption = Math.max(0.0F, absorption);
            this.visualStyle = style;
            this.baseHeartSlots = Math.max(1, Mth.ceil(this.maxHealth / 2.0F));
            this.absorptionSlots = Math.max(0, Mth.ceil(this.absorption / 2.0F));
            this.hasMountedEntity = hasMountedEntity;
        }
    }

    private enum HeartVisualStyle {
        NORMAL(HeartTextureKind.HALF, HeartTextureKind.FULL),
        WITHER(HeartTextureKind.WITHER_HALF, HeartTextureKind.WITHER_FULL),
        POISON(HeartTextureKind.POISON_HALF, HeartTextureKind.POISON_FULL),
        FROZEN(HeartTextureKind.FROZEN_HALF, HeartTextureKind.FROZEN_FULL),
        BURNING(HeartTextureKind.BURNING_HALF, HeartTextureKind.BURNING_FULL);

        private final HeartTextureKind halfTexture;
        private final HeartTextureKind fullTexture;

        HeartVisualStyle(HeartTextureKind halfTexture, HeartTextureKind fullTexture) {
            this.halfTexture = halfTexture;
            this.fullTexture = fullTexture;
        }
    }

    public enum HeartTextureKind {
        EMPTY("empty", 0x80202020),
        HALF("half", 0xFFFF8B8B),
        FULL("full", 0xFFFF4F4F),
        ABSORPTION_HALF("absorption_half", 0xFFFFF1A6),
        ABSORPTION_FULL("absorption_full", 0xFFFFD54F),
        WITHER_HALF("wither_half", 0xFF6F6F6F),
        WITHER_FULL("wither_full", 0xFF454545),
        POISON_HALF("poison_half", 0xFF8CF07D),
        POISON_FULL("poison_full", 0xFF4FBD45),
        FROZEN_HALF("frozen_half", 0xFF9EE7FF),
        FROZEN_FULL("frozen_full", 0xFF59C6FF),
        BURNING_HALF("burning_half", 0xFFFFB774),
        BURNING_FULL("burning_full", 0xFFFF7A00);

        private final String translationKeySuffix;
        private final int defaultColor;

        HeartTextureKind(String translationKeySuffix, int defaultColor) {
            this.translationKeySuffix = translationKeySuffix;
            this.defaultColor = defaultColor;
        }

        public String getTranslationKey() {
            return "spiffyhud.elements.player_mount_health_bar.texture." + this.translationKeySuffix;
        }
    }

    public void setCustomTexture(@NotNull HeartTextureKind kind, @Nullable ResourceSupplier<ITexture> supplier) {
        if (supplier == null) {
            this.customTextures.remove(kind);
        } else {
            this.customTextures.put(kind, supplier);
        }
    }

    @Nullable
    public ResourceSupplier<ITexture> getCustomTexture(@NotNull HeartTextureKind kind) {
        return this.customTextures.get(kind);
    }

    @NotNull
    public Map<HeartTextureKind, ResourceSupplier<ITexture>> getCustomTextureMap() {
        return Collections.unmodifiableMap(this.customTextures);
    }

    private HeartVisualStyle pickEditorPreviewStyle() {
        long bucket = System.currentTimeMillis() / EDITOR_PREVIEW_INTERVAL_MS;
        int index = (int) (bucket % EDITOR_PREVIEW_STYLES.length);
        return EDITOR_PREVIEW_STYLES[Math.max(0, index)];
    }

}
