package de.keksuccino.spiffyhud.customization.elements.player;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.spiffyhud.util.rendering.GuiEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PlayerElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final GuiEntityRenderer renderer = new GuiEntityRenderer();

    public PlayerElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {

        if (this.shouldRender() && (Minecraft.getInstance().player != null)) {

            this.renderer.renderEntity(graphics, this.getAbsoluteX(), this.getAbsoluteY(), this.getAbsoluteWidth(), this.getAbsoluteHeight(), this.opacity, Minecraft.getInstance().player);

        }

    }

}
