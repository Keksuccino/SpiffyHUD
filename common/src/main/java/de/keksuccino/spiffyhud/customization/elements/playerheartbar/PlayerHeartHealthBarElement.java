package de.keksuccino.spiffyhud.customization.elements.playerheartbar;

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
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class PlayerHeartHealthBarElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_SCALE_STRING = "1.0";
    private static final float DEFAULT_SCALE = 1.0F;
    private static final float MIN_SCALE = 0.2F;
    private static final float MAX_SCALE = 8.0F;
    private static final int BASE_HEART_PIXEL_SIZE = 8;
    private static final long BLINK_DURATION_MS = 650L;

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
    private int lastBlinkSlotIndex = -1;
    private long blinkEndTimeMs = -1L;
    private int cachedTickCount = 0;

    public PlayerHeartHealthBarElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
        this.stickyAnchor = true;
        this.stayOnScreen = false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        this.cachedTickCount = safeTickCount();
        PlayerData data = this.collectPlayerData();
        float scale = this.resolveScale();
        RenderMetrics metrics = this.computeMetrics(data, scale);

        this.baseWidth = Math.max(1, metrics.bodyWidth);
        this.baseHeight = Math.max(1, metrics.bodyHeight);

        this.updateBlinkState(data.currentHealth, data.baseHeartSlots);

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

    private void drawHearts(@NotNull GuiGraphics graphics, int originX, int originY, @NotNull PlayerData data,
                            @NotNull RenderMetrics metrics, float scale) {

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(originX, originY, 0);

        long now = System.currentTimeMillis();
        boolean shake = this.shouldShake(data.currentHealth);
        float shakeStrengthBase = shake ? 0.9F : 0.0F;

        int slot = 0;
        for (int i = 0; i < data.baseHeartSlots; i++, slot++) {
            float heartValue = data.currentHealth - (i * 2.0F);
            HeartTextureKind texture = this.selectBaseTextureForSlot(heartValue, data.visualStyle, i, now);
            this.renderSingleHeart(graphics, metrics, slot, texture, shakeStrengthBase, scale);
        }

        for (int i = 0; i < data.absorptionSlots; i++, slot++) {
            float heartValue = data.absorption - (i * 2.0F);
            if (heartValue <= 0.0F) {
                break;
            }
            HeartTextureKind texture = this.selectAbsorptionTexture(heartValue);
            this.renderSingleHeart(graphics, metrics, slot, texture, shakeStrengthBase, scale);
        }

        pose.popPose();
    }

    private void renderSingleHeart(@NotNull GuiGraphics graphics, @NotNull RenderMetrics metrics, int slotIndex,
                                   @NotNull HeartTextureKind textureKind, float shakeStrengthBase, float scale) {

        int col = slotIndex % metrics.heartsPerRow;
        int row = slotIndex / metrics.heartsPerRow;
        int baseX = col * (metrics.baseHeartSize + metrics.horizontalGap);
        int baseY = row * (metrics.baseHeartSize + metrics.verticalGap);

        if (shakeStrengthBase > 0.0F) {
            float[] offsets = this.computeShakeOffset(slotIndex, shakeStrengthBase);
            baseX += Math.round(offsets[0]);
            baseY += Math.round(offsets[1]);
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(baseX, baseY, 0);
        pose.scale(scale, scale, 1.0F);
        this.drawHeartTexture(graphics, textureKind, metrics.baseHeartSize);
        pose.popPose();
    }

    private float[] computeShakeOffset(int slotIndex, float shakeStrengthBase) {
        this.shakeRandom.setSeed(((long) this.cachedTickCount * 341873128712L) + (slotIndex * 132897987541L));
        float dx = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        float dy = (this.shakeRandom.nextFloat() - 0.5F) * shakeStrengthBase;
        return new float[] { dx, dy };
    }

    private HeartTextureKind selectBaseTextureForSlot(float heartValue, @NotNull HeartVisualStyle style, int slotIndex, long now) {
        if (this.blinkOnLoss && slotIndex == this.lastBlinkSlotIndex && now < this.blinkEndTimeMs) {
            return ((now / 120L) % 2L == 0L) ? style.halfTexture : HeartTextureKind.EMPTY;
        }
        if (heartValue >= 2.0F) {
            return style.fullTexture;
        }
        if (heartValue > 0.0F) {
            return style.halfTexture;
        }
        return HeartTextureKind.EMPTY;
    }

    private HeartTextureKind selectAbsorptionTexture(float heartValue) {
        if (heartValue >= 2.0F) {
            return HeartTextureKind.ABSORPTION_FULL;
        }
        return HeartTextureKind.ABSORPTION_HALF;
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

    private RenderMetrics computeMetrics(@NotNull PlayerData data, float scale) {
        int totalSlots = Math.max(1, data.baseHeartSlots + data.absorptionSlots);
        int heartsPerRowClamped = Math.max(1, this.heartsPerRow);
        int rowSize = Math.min(heartsPerRowClamped, totalSlots);
        int rows = Math.max(1, (int) Math.ceil(totalSlots / (double) heartsPerRowClamped));
        int gap = Math.max(0, this.heartGap);
        int unscaledWidth = rowSize * BASE_HEART_PIXEL_SIZE + Math.max(0, rowSize - 1) * gap;
        int unscaledHeight = rows * BASE_HEART_PIXEL_SIZE + Math.max(0, rows - 1) * gap;
        int bodyWidth = Math.max(1, Mth.ceil(unscaledWidth * scale));
        int bodyHeight = Math.max(1, Mth.ceil(unscaledHeight * scale));
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

    private void updateBlinkState(float currentHealth, int baseHeartSlots) {
        if (!this.blinkOnLoss || baseHeartSlots <= 0) {
            this.lastRecordedHealth = currentHealth;
            return;
        }

        if ((this.lastRecordedHealth < 0.0F)) {
            this.lastRecordedHealth = currentHealth;
            return;
        }

        if (currentHealth < this.lastRecordedHealth - 0.01F) {
            int slot = Mth.clamp(Mth.ceil(currentHealth / 2.0F), 0, Math.max(0, baseHeartSlots - 1));
            this.lastBlinkSlotIndex = slot;
            this.blinkEndTimeMs = System.currentTimeMillis() + BLINK_DURATION_MS;
        } else if (currentHealth > this.lastRecordedHealth + 0.01F) {
            this.lastBlinkSlotIndex = -1;
        }
        this.lastRecordedHealth = currentHealth;
        if ((this.lastBlinkSlotIndex >= baseHeartSlots) || (System.currentTimeMillis() >= this.blinkEndTimeMs)) {
            this.lastBlinkSlotIndex = -1;
        }
    }

    private boolean shouldShake(float currentHealth) {
        if (!this.lowHealthShakeEnabled) {
            return false;
        }
        if (this.lowHealthShakeThresholdHearts <= 0) {
            return false;
        }
        float threshold = this.lowHealthShakeThresholdHearts * 2.0F;
        return currentHealth <= threshold;
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
            int phase = (this.cachedTickCount / 40) % 4;
            HeartVisualStyle style = switch (phase) {
                case 1 -> HeartVisualStyle.POISON;
                case 2 -> HeartVisualStyle.WITHER;
                case 3 -> HeartVisualStyle.BURNING;
                default -> HeartVisualStyle.NORMAL;
            };
            return new PlayerData(13.0F, 20.0F, 6.0F, style);
        }

        Player player = this.minecraft.player;
        if (player == null) {
            return new PlayerData(10.0F, 20.0F, 0.0F, HeartVisualStyle.NORMAL);
        }
        float currentHealth = Math.max(0.0F, player.getHealth());
        float maxHealth = Math.max(2.0F, player.getMaxHealth());
        float absorption = Math.max(0.0F, player.getAbsorptionAmount());
        HeartVisualStyle style = this.resolveVisualStyle(player);
        return new PlayerData(currentHealth, maxHealth, absorption, style);
    }

    private HeartVisualStyle resolveVisualStyle(@NotNull Player player) {
        if (player.hasEffect(MobEffects.WITHER)) {
            return HeartVisualStyle.WITHER;
        }
        if (player.hasEffect(MobEffects.POISON)) {
            return HeartVisualStyle.POISON;
        }
        if (player.getTicksFrozen() > 0) {
            return HeartVisualStyle.FROZEN;
        }
        if (player.isOnFire()) {
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

    private record RenderMetrics(int heartsPerRow, int baseHeartSize, int totalSlots, int bodyWidth, int bodyHeight,
                                 int rows, int horizontalGap, int verticalGap) {
    }

    private static class PlayerData {
        final float currentHealth;
        final float maxHealth;
        final float absorption;
        final HeartVisualStyle visualStyle;
        final int baseHeartSlots;
        final int absorptionSlots;

        PlayerData(float currentHealth, float maxHealth, float absorption, HeartVisualStyle style) {
            this.currentHealth = Math.max(0.0F, currentHealth);
            this.maxHealth = Math.max(2.0F, maxHealth);
            this.absorption = Math.max(0.0F, absorption);
            this.visualStyle = style;
            this.baseHeartSlots = Math.max(1, Mth.ceil(this.maxHealth / 2.0F));
            this.absorptionSlots = Math.max(0, Mth.ceil(this.absorption / 2.0F));
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
            return "spiffyhud.elements.player_heart_health_bar.texture." + this.translationKeySuffix;
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
}
