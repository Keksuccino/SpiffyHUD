package de.keksuccino.spiffyhud.customization.elements.playernbthelper;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PlayerNbtHelperElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    public PlayerNbtHelperElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {

        if (isEditor()) {
            int x = this.getAbsoluteX();
            int y = this.getAbsoluteY();
            int w = this.getAbsoluteWidth();
            int h = this.getAbsoluteHeight();
            graphics.fill(x, y, x + w, y + h, this.inEditorColor.getDrawable().getColorInt());
            graphics.enableScissor(x, y, x + w, y + h);
            graphics.centeredText(Minecraft.getInstance().font, this.getDisplayName(), x + (w / 2), y + (h / 2) - (Minecraft.getInstance().font.lineHeight / 2), -1);
            graphics.disableScissor();
        }

    }

}
