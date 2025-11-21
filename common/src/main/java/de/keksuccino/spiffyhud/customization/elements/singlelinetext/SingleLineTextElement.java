package de.keksuccino.spiffyhud.customization.elements.singlelinetext;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.spiffyhud.util.ComponentUtils;
import net.minecraft.client.Minecraft;
import de.keksuccino.fancymenu.util.rendering.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleLineTextElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_TEXT_SCALE_STRING = "1.0";
    private static final float DEFAULT_TEXT_SCALE = 1.0F;
    private static final float MIN_TEXT_SCALE = 0.3F;

    @Nullable
    public String text = null;
    @NotNull
    public String textScale = DEFAULT_TEXT_SCALE_STRING;

    public SingleLineTextElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        float scale = this.resolveTextScale();

        if (this.shouldRender()) {

            Component c = (this.text != null) ? ComponentUtils.fromJsonOrPlainText(this.text) : Component.literal("--------------------");
            if (c.getString().isBlank() && isEditor()) {
                c = Component.literal("--------------------");
            }
            this.baseWidth = (int) Math.ceil(Minecraft.getInstance().font.width(c) * scale);
            if ((this.baseWidth < 10) && isEditor()) {
                this.baseWidth = 10;
            }

            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(this.getAbsoluteX(), this.getAbsoluteY(), 0);
            pose.scale(scale, scale, 1.0F);
            graphics.drawString(Minecraft.getInstance().font, c, 0, 0, DrawableColor.WHITE.getColorIntWithAlpha(this.opacity));
            pose.popPose();

        } else {
            this.baseWidth = 100;
        }

        this.baseHeight = Math.max(1, (int) Math.ceil(Minecraft.getInstance().font.lineHeight * scale));

    }

    private float resolveTextScale() {
        String raw = this.textScale;
        if (raw == null || raw.isBlank()) {
            return DEFAULT_TEXT_SCALE;
        }

        String replaced = PlaceholderParser.replacePlaceholders(raw);
        if (!replaced.isBlank() && MathUtils.isFloat(replaced)) {
            try {
                float parsed = Float.parseFloat(replaced);
                if (Float.isNaN(parsed) || Float.isInfinite(parsed)) {
                    return DEFAULT_TEXT_SCALE;
                }
                return Math.max(MIN_TEXT_SCALE, parsed);
            } catch (NumberFormatException ignored) {}
        }

        return DEFAULT_TEXT_SCALE;
    }

}
