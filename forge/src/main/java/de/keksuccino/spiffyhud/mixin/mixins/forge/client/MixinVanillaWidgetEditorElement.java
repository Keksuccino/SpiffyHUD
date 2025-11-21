package de.keksuccino.spiffyhud.mixin.mixins.forge.client;

import de.keksuccino.fancymenu.customization.element.elements.button.custombutton.ButtonEditorElement;
import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetEditorElement;
import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetElement;
import de.keksuccino.fancymenu.customization.widget.WidgetMeta;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.ObjectUtils;
import de.keksuccino.fancymenu.util.rendering.ui.UIBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;

@Mixin(VanillaWidgetEditorElement.class)
public abstract class MixinVanillaWidgetEditorElement extends ButtonEditorElement {

    @Unique private final List<Component> vanillaMovingNotAllowedWarning_Spiffy = ObjectUtils.build(() -> {
        List<Component> list = new ArrayList<>();
        for (Component c : LocalizationUtils.splitLocalizedLines("spiffyhud.elements.dummy.display.cant_move_vanilla_elements")) {
            if (c instanceof MutableComponent m) {
                list.add(m.setStyle(Style.EMPTY.withBold(true).withColor(UIBase.getUIColorTheme().error_text_color.getColorInt())));
            }
        }
        return list;
    });

    //unused dummy constructor
    @SuppressWarnings("all")
    private MixinVanillaWidgetEditorElement() {
        super(null, null);
    }

    @Inject(method = "renderDraggingNotAllowedOverlay", at = @At(value = "INVOKE", target = "Lde/keksuccino/fancymenu/customization/element/elements/button/custombutton/ButtonEditorElement;renderDraggingNotAllowedOverlay(Lnet/minecraft/client/gui/GuiGraphics;)V"), remap = false)
    private void after_adding_display_lines_Spiffy(GuiGraphics graphics, CallbackInfo info) {

        if (this.isSpiffyDummyElement_Spiffy()) {

            // Remove original "dragging not allowed" message
            this.topLeftDisplay.removeLine("vanilla_button_dragging_not_allowed");

            // Instead, render a custom warning when trying to move the Vanilla HUD element
            if (this.renderMovingNotAllowedTime >= System.currentTimeMillis()) {

                int bgColor = UIBase.getUIColorTheme().area_background_color.getColorIntWithAlpha(0.7F);
                Font font = Minecraft.getInstance().font;

                int totalWidth = 0;
                for (Component c : this.vanillaMovingNotAllowedWarning_Spiffy) {
                    int w = font.width(c);
                    if (w > totalWidth) {
                        totalWidth = w;
                    }
                }

                int lineHeight = font.lineHeight;
                int numLines = this.vanillaMovingNotAllowedWarning_Spiffy.size();
                int blockHeight = numLines * lineHeight + (numLines - 1) * 2;

                boolean bottomBlocked = (this.getY() + this.getHeight()) > (this.editor.height - 22);
                boolean topBlocked = (this.getY() < 22);
                boolean rightBlocked = (this.getX() + this.getWidth()) > (this.editor.width - (totalWidth + 2));

                graphics.pose().pushPose();
                graphics.pose().translate(0.0F, 0.0F, 400.0F);

                // --- Top: render above the element ---
                if (!topBlocked) {
                    // Compute startY so that the bottom of the text block is exactly 2 pixels above the element.
                    int startY = this.getY() - 2 - blockHeight;
                    if (startY < 0) {
                        startY = 0;
                    }
                    int centerX = this.getX() + (this.getWidth() / 2);
                    boolean useCentered = true;
                    boolean useRightAligned = false;
                    boolean useLeftAligned = false;
                    if (centerX + totalWidth / 2 > this.editor.width) {
                        useCentered = false;
                        useRightAligned = true;
                    } else if (centerX - totalWidth / 2 < 0) {
                        useCentered = false;
                        useLeftAligned = true;
                    }
                    int currentLine = 0;
                    for (Component c : this.vanillaMovingNotAllowedWarning_Spiffy) {
                        int lineY = startY + currentLine * (lineHeight + 2);
                        int lineWidth = font.width(c);
                        if (useCentered) {
                            // For centered text, compute the left edge from centerX.
                            int xLeft = centerX - (lineWidth / 2);
                            // Draw background: 1 pixel padding on each side.
                            graphics.fill(xLeft - 1, lineY - 1, xLeft + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                            graphics.drawCenteredString(font, c, centerX, lineY, -1);
                        } else if (useRightAligned) {
                            int textX = this.getX() + this.getWidth() - lineWidth;
                            graphics.fill(textX - 1, lineY - 1, textX + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                            graphics.drawString(font, c, textX, lineY, -1);
                        } else if (useLeftAligned) {
                            int textX = this.getX();
                            graphics.fill(textX - 1, lineY - 1, textX + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                            graphics.drawString(font, c, textX, lineY, -1);
                        }
                        currentLine++;
                    }
                }
                // --- Bottom: render below the element ---
                else if (!bottomBlocked) {
                    int startY = this.getY() + this.getHeight() + 2;
                    if (startY + blockHeight > this.editor.height) {
                        startY = this.editor.height - blockHeight;
                    }
                    int centerX = this.getX() + (this.getWidth() / 2);
                    boolean useCentered = true;
                    boolean useRightAligned = false;
                    boolean useLeftAligned = false;
                    if (centerX + totalWidth / 2 > this.editor.width) {
                        useCentered = false;
                        useRightAligned = true;
                    } else if (centerX - totalWidth / 2 < 0) {
                        useCentered = false;
                        useLeftAligned = true;
                    }
                    int currentLine = 0;
                    for (Component c : this.vanillaMovingNotAllowedWarning_Spiffy) {
                        int lineY = startY + currentLine * (lineHeight + 2);
                        int lineWidth = font.width(c);
                        if (useCentered) {
                            int xLeft = centerX - (lineWidth / 2);
                            graphics.fill(xLeft - 1, lineY - 1, xLeft + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                            graphics.drawCenteredString(font, c, centerX, lineY, -1);
                        } else if (useRightAligned) {
                            int textX = this.getX() + this.getWidth() - lineWidth;
                            graphics.fill(textX - 1, lineY - 1, textX + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                            graphics.drawString(font, c, textX, lineY, -1);
                        } else if (useLeftAligned) {
                            int textX = this.getX();
                            graphics.fill(textX - 1, lineY - 1, textX + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                            graphics.drawString(font, c, textX, lineY, -1);
                        }
                        currentLine++;
                    }
                }
                // --- Right: render to the right of the element (left aligned) ---
                else if (!rightBlocked) {
                    int startY = this.getY() + (this.getHeight() / 2) - (blockHeight / 2);
                    if (startY < 0) {
                        startY = 0;
                    } else if (startY + blockHeight > this.editor.height) {
                        startY = this.editor.height - blockHeight;
                    }
                    int startX = this.getX() + this.getWidth() + 2;
                    if (startX + totalWidth > this.editor.width) {
                        startX = this.editor.width - totalWidth;
                    }
                    int currentLine = 0;
                    for (Component c : this.vanillaMovingNotAllowedWarning_Spiffy) {
                        int lineY = startY + currentLine * (lineHeight + 2);
                        int lineWidth = font.width(c);
                        graphics.fill(startX - 1, lineY - 1, startX + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                        graphics.drawString(font, c, startX, lineY, -1);
                        currentLine++;
                    }
                }
                // --- Left: render to the left of the element (right aligned) ---
                else {
                    int startY = this.getY() + (this.getHeight() / 2) - (blockHeight / 2);
                    if (startY < 0) {
                        startY = 0;
                    } else if (startY + blockHeight > this.editor.height) {
                        startY = this.editor.height - blockHeight;
                    }
                    int currentLine = 0;
                    for (Component c : this.vanillaMovingNotAllowedWarning_Spiffy) {
                        int lineY = startY + currentLine * (lineHeight + 2);
                        int lineWidth = font.width(c);
                        int textX = this.getX() - 2 - lineWidth;
                        if (textX < 0) {
                            textX = 0;
                        }
                        graphics.fill(textX - 1, lineY - 1, textX + lineWidth + 1, lineY + lineHeight + 1, bgColor);
                        graphics.drawString(font, c, textX, lineY, -1);
                        currentLine++;
                    }
                }

                graphics.pose().popPose();

            }

        }

    }

    @Unique
    private boolean isSpiffyDummyElement_Spiffy() {
        WidgetMeta meta = ((VanillaWidgetElement)this.element).widgetMeta;
        if (meta == null) return false;
        String compId = meta.getUniversalIdentifier();
        return ((compId != null) && compId.startsWith("spiffy_") && compId.endsWith("_dummy"));
    }

}
