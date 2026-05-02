package de.keksuccino.spiffyhud.customization.elements.slot;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.fancymenu.util.rendering.text.markdown.ScrollableMarkdownRenderer;
import de.keksuccino.fancymenu.util.rendering.ui.UIBase;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.ExtendedButton;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SlotIdHelpScreen extends Screen {

    protected Screen parent;
    protected ScrollableMarkdownRenderer markdownRenderer;
    protected int headerHeight = 20;
    protected int footerHeight = 40;
    protected int border = 40;
    protected boolean textSet = false;
    protected final List<String> lines = List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.elements.slot.slot_id_help.text"));

    public SlotIdHelpScreen(@NotNull Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    protected void init() {

        int centerX = this.width / 2;
        int scrollWidth = this.width - this.border * 2;
        int scrollHeight = this.height - this.headerHeight - this.footerHeight;

        if (this.markdownRenderer == null) {
            this.markdownRenderer = new ScrollableMarkdownRenderer((float)(centerX - scrollWidth / 2), (float)this.headerHeight, (float)scrollWidth, (float)scrollHeight);
        } else {
            this.markdownRenderer.rebuild((float)(centerX - scrollWidth / 2), (float)this.headerHeight, (float)scrollWidth, (float)scrollHeight);
        }
        this.markdownRenderer.getMarkdownRenderer().setHeadlineLineColor(UIBase.getUITheme().ui_interface_background_color);
        this.markdownRenderer.getMarkdownRenderer().setTextBaseColor(UIBase.getUITheme().ui_interface_generic_text_color);
        this.markdownRenderer.getMarkdownRenderer().setTextShadow(false);
        this.addRenderableWidget(this.markdownRenderer);

        UIBase.applyDefaultWidgetSkinTo(this.addRenderableWidget(new ExtendedButton(centerX - 100, this.height - this.footerHeight / 2 - 10, 200, 20, Component.translatable("fancymenu.common.close"), (var1) -> this.onClose())));

    }

    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        if (!this.textSet) {
            StringBuilder lineString = new StringBuilder();
            for(String s : lines) {
                lineString.append(s).append("\n");
            }
            this.markdownRenderer.setText(lineString.toString());
            this.textSet = true;
        }

        RenderSystem.enableBlend();
        graphics.fill(0, 0, this.width, this.height, UIBase.getUITheme().ui_interface_background_color.getColorInt());
        RenderingUtils.resetShaderColor(graphics);
        graphics.fill(0, this.height - this.footerHeight, this.width, this.height, UIBase.getUITheme().ui_interface_area_background_color_type_1.getColorInt());
        RenderingUtils.resetShaderColor(graphics);

        super.render(graphics, mouseX, mouseY, partial);

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return this.markdownRenderer.mouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.markdownRenderer.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

}
