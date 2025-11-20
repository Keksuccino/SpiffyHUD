package de.keksuccino.spiffyhud.customization.elements.slot;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.fancymenu.util.rendering.ui.UIBase;
import de.keksuccino.spiffyhud.util.rendering.SpiffyRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class SlotElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    @NotNull
    public String slot = "0";
    public int parsedSlot = 0;
    public boolean useSelectedSlot = false;
    public boolean showDurability = true;

    protected final Font font = Minecraft.getInstance().font;

    public SlotElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        if (this.shouldRender()) {

            String slotString = PlaceholderParser.replacePlaceholders(this.slot).trim();
            if (MathUtils.isInteger(slotString)) {
                this.parsedSlot = Integer.parseInt(slotString);
            } else {
                this.parsedSlot = 0;
            }

            if (Minecraft.getInstance().player == null) return;

            int x = this.getAbsoluteX();
            int y = this.getAbsoluteY();
            int w = this.getAbsoluteWidth();
            int h = this.getAbsoluteHeight();

            if (isEditor()) {

                MutableComponent label = this.useSelectedSlot ? Component.literal("SEL") : Component.literal("" + this.parsedSlot);
                if (this.customElementLayerName != null) {
                    label = buildComponent(this.customElementLayerName).copy();
                    label.append(this.useSelectedSlot ? Component.literal(" (SEL)") : Component.literal(" (" + this.parsedSlot + ")"));
                }

                graphics.fill(x, y, x + w, y + h, this.inEditorColor.getColorIntWithAlpha(0.5F));
                UIBase.renderBorder(graphics, x, y, x + w, y + h, 1, this.inEditorColor.getColorIntWithAlpha(0.8F), true, true, true, true);

                graphics.drawCenteredString(this.font, label, x + (w / 2), y + (h / 2) - (this.font.lineHeight / 2), -1);

            } else {

                ItemStack slotItem = this.useSelectedSlot ? Minecraft.getInstance().player.getInventory().getSelectedItem() : Minecraft.getInstance().player.getInventory().getItem(this.parsedSlot);
                if (slotItem == ItemStack.EMPTY) return;

                //Render the item slightly smaller than the actual element, so it renders at 16x16 pixels when the element is 20x20 (like Vanilla)
                this.renderItem(graphics, x + 2, y + 2, w - 4, h - 4, mouseX, mouseY, slotItem);

            }

        }

    }

    protected void renderItem(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, @NotNull ItemStack itemStack) {

        int count = itemStack.getCount();

        this.renderScaledItem(graphics, itemStack, x, y, width, height);

        if (count > 1) {
            this.renderItemCount(graphics, this.font, x, y, Math.max(width, height), count);
        }

    }

    protected void renderScaledItem(@NotNull GuiGraphics graphics, @NotNull ItemStack stack, int x, int y, int width, int height) {

        // Save the current transformation state.
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();

        // Translate to the top-left of where you want the item to be drawn.
        pose.translate(x, y);

        // Calculate a uniform scale factor based on the desired size.
        // (Items are rendered at a base size of 16x16.)
        float scale = Math.min(width, height) / 16.0F;
        pose.scale(scale, scale);

        // Now render the item at (0,0) because the translation has been applied.
        graphics.renderItem(stack, 0, 0);

        // Render durability bar if enabled and needed
        if (this.showDurability && stack.isBarVisible()) {
            int barWidth = stack.getBarWidth();
            int barColor = stack.getBarColor();
            
            // Position the bar - using the same positioning logic as in GuiGraphics
            int barX = 2;
            int barY = 13;
            
            // Draw the background (black)
            graphics.fill(barX, barY,
                          barX + 13, barY + 2, 
                          SpiffyRenderUtils.colorWithAlpha(-16777216, this.opacity));
            
            // Draw the colored part of the bar
            graphics.fill(barX, barY,
                          barX + barWidth, barY + 1,
                    SpiffyRenderUtils.colorWithAlpha(barColor | 0xFF000000, this.opacity));
        }

        // Render item cooldown overlay similar to vanilla hotbar slots.
        if (Minecraft.getInstance().player != null) {
            float cooldownProgress = Minecraft.getInstance().player.getCooldowns().getCooldownPercent(stack, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
            if (cooldownProgress > 0.0F) {
                int overlayTop = Mth.floor(16.0F * (1.0F - cooldownProgress));
                int overlayBottom = overlayTop + Mth.ceil(16.0F * cooldownProgress);
                graphics.fill(0, overlayTop, 16, overlayBottom, Integer.MAX_VALUE);
            }
        }

        // Restore the previous transformation state.
        pose.popMatrix();

    }

    protected void renderItemCount(@NotNull GuiGraphics graphics, @NotNull Font font, int x, int y, int size, int count) {

        Matrix3x2fStack pose = graphics.pose();
        String text = String.valueOf(count);
        // Calculate scaling factor relative to original 16x16 size
        float scaleFactor = size / 16.0F;

        pose.pushMatrix();
        pose.translate(0.0F, 0.0F);
        pose.pushMatrix();

        // Scale text exactly proportionally to item size
        pose.scale(scaleFactor, scaleFactor);

        // Convert item-space coordinates to scaled text-space coordinates
        int scaledX = (int)((x / scaleFactor) + 19 - 2 - font.width(text));
        int scaledY = (int)((y / scaleFactor) + 6 + 3);

        graphics.drawString(font, text, scaledX, scaledY, DrawableColor.WHITE.getColorIntWithAlpha(this.opacity), true);
        pose.popMatrix();
        pose.popMatrix();

    }

    @Override
    public @NotNull Component getDisplayName() {
        MutableComponent c = super.getDisplayName().copy();
        c.append(this.useSelectedSlot ? Component.literal(" (SEL)") : Component.literal(" (" + this.parsedSlot + ")"));
        return c;
    }

}
