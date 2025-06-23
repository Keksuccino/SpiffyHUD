package de.keksuccino.spiffyhud;

import de.keksuccino.fancymenu.customization.overlay.CustomizationOverlay;
import de.keksuccino.fancymenu.events.screen.InitOrResizeScreenCompletedEvent;
import de.keksuccino.fancymenu.events.screen.OpenScreenEvent;
import de.keksuccino.fancymenu.events.screen.RenderScreenEvent;
import de.keksuccino.fancymenu.networking.PacketHandler;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.event.acara.EventListener;
import de.keksuccino.fancymenu.util.rendering.ui.UIBase;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.TooltipHandler;
import de.keksuccino.fancymenu.util.rendering.ui.widget.button.ExtendedButton;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.networking.packets.structure.structures.StructuresPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SpiffyEvents {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation EDIT_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath("spiffyhud", "textures/edit_button.png");

    private ExtendedButton spiffyButton;

    @EventListener
    public void onInitOrResizeScreenCompleted(InitOrResizeScreenCompletedEvent e) {

        if ((e.getScreen() instanceof PauseScreen p) && p.showsPauseMenu() && CustomizationOverlay.isOverlayVisible(p)) {

            this.spiffyButton = new ExtendedButton(-30, 40, 80, 40, Component.empty(), (button) -> {

                Minecraft.getInstance().setScreen(new SpiffyOverlayScreen(true));

            }) {

                @Override
                public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

                    var m = CustomizationOverlay.getCurrentMenuBarInstance();
                    if ((m == null) || !m.isUserNavigatingInMenuBar()) {
                        TooltipHandler.INSTANCE.addTooltip(Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.edit_hud.desc")).setDefaultStyle().setScale(UIBase.getUIScale()), () -> this.isHovered, false, true);
                    }

                    if (this.isHoveredOrFocused()) {
                        this.setX(-20);
                    } else {
                        this.setX(-30);
                    }

                    super.render(graphics, mouseX, mouseY, partial);

                    graphics.blit(RenderPipelines.GUI_TEXTURED, EDIT_BUTTON_TEXTURE, this.getX(), this.getY(), 0.0f, 0.0f, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());

                }

            };

            UIBase.applyDefaultWidgetSkinTo(this.spiffyButton);

            //2 because MenuBar and DebugOverlay need to be at pos 0 and 1
            if (e.getWidgets().size() >= 2) {
                e.getWidgets().add(2, this.spiffyButton);
            } else {
                e.getWidgets().add(0, this.spiffyButton);
            }

        }

    }

    @EventListener(priority = 0) //FM is -1
    public void onScreenRenderPost(RenderScreenEvent.Post e) {

        if ((e.getScreen() instanceof PauseScreen p) && p.showsPauseMenu() && CustomizationOverlay.isOverlayVisible(p)) {
            if (this.spiffyButton != null) {
                this.spiffyButton.render(e.getGraphics(), e.getMouseX(), e.getMouseY(), e.getPartial());
            }
        }

    }

    @EventListener
    public void onOpenScreen(OpenScreenEvent e) {
        if (Minecraft.getInstance().level != null) {
            StructuresPacket question = new StructuresPacket();
            question.get = true;
            PacketHandler.sendToServer(question);
        }
    }

}
