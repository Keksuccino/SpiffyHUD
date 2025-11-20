package de.keksuccino.spiffyhud.customization.elements.playernbthelper;

import de.keksuccino.fancymenu.util.ObjectUtils;
import de.keksuccino.fancymenu.util.rendering.text.markdown.ScrollableMarkdownRenderer;
import de.keksuccino.fancymenu.util.rendering.ui.UIBase;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.ExtendedButton;
import de.keksuccino.spiffyhud.util.level.EntityNbtUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PlayerNbtPathHelpScreen extends Screen {

    protected Screen parent;
    protected ScrollableMarkdownRenderer markdownRenderer;
    protected int headerHeight = 20;
    protected int footerHeight = 40;
    protected int border = 40;
    protected boolean textSet = false;
    protected final List<String> lines = ObjectUtils.build(() -> {

        List<String> l = (Minecraft.getInstance().player == null) ? new ArrayList<>() : EntityNbtUtils.getAllNbtPaths(Minecraft.getInstance().player);

        // start
        l.add(0, "^^^");
        l.add(1, " ");
        l.add(2, I18n.get("spiffyhud.elements.player_nbt_helper.paths.headline"));
        l.add(3, " ");

        // existing lines here

        // end
        l.add("^^^");

        return l;

    });

    public PlayerNbtPathHelpScreen(@NotNull Screen parent) {
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
        this.markdownRenderer.getMarkdownRenderer().setHeadlineLineColor(UIBase.getUIColorTheme().screen_background_color_darker);
        this.markdownRenderer.getMarkdownRenderer().setTextBaseColor(UIBase.getUIColorTheme().generic_text_base_color);
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

        graphics.fill(0, 0, this.width, this.height, UIBase.getUIColorTheme().screen_background_color_darker.getColorInt());
        graphics.fill(0, this.height - this.footerHeight, this.width, this.height, UIBase.getUIColorTheme().area_background_color.getColorInt());

        super.render(graphics, mouseX, mouseY, partial);

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return this.markdownRenderer.mouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return this.markdownRenderer.mouseReleased(event);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

}
