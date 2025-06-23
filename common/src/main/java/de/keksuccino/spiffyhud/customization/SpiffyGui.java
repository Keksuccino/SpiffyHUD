package de.keksuccino.spiffyhud.customization;

import de.keksuccino.fancymenu.customization.ScreenCustomization;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.events.screen.*;
import de.keksuccino.fancymenu.util.event.acara.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpiffyGui implements Renderable {

    public static final SpiffyGui INSTANCE = new SpiffyGui();

    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean initialized = false;
    private static SpiffyOverlayScreen spiffyOverlayScreen = new SpiffyOverlayScreen(false);

    private SpiffyGui() {

        if (!initialized) {
            initialized = true;
        }

        this.setNewOverlayScreen();
        this.initOverlayScreen(false);
        this.tick();

    }


    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        if (!this.shouldRenderCustomizations()) return;

        this.runLayerTask(() -> {

            EventHandler.INSTANCE.postEvent(new ScreenTickEvent.Pre(spiffyOverlayScreen));
            spiffyOverlayScreen.tick();
            EventHandler.INSTANCE.postEvent(new ScreenTickEvent.Post(spiffyOverlayScreen));

            this.restoreRenderDefaults(graphics);

            EventHandler.INSTANCE.postEvent(new RenderScreenEvent.Pre(spiffyOverlayScreen, graphics, mouseX, mouseY, partial));
            spiffyOverlayScreen.render(graphics, mouseX, mouseY, partial);
            this.restoreRenderDefaults(graphics);
            EventHandler.INSTANCE.postEvent(new RenderScreenEvent.Post(spiffyOverlayScreen, graphics, mouseX, mouseY, partial));

            this.restoreRenderDefaults(graphics);

        });

    }

    private void restoreRenderDefaults(@NotNull GuiGraphics graphics) {
        // Unused in 1.21.5
    }

    private boolean shouldRenderCustomizations() {
        if (Minecraft.getInstance().screen instanceof LayoutEditorScreen) return false;
        return (spiffyOverlayScreen != null) && (this.getLayer() != null);
    }

    @Nullable
    private ScreenCustomizationLayer getLayer() {
        if (spiffyOverlayScreen == null) return null;
        ScreenCustomizationLayer l = ScreenCustomizationLayerHandler.getLayerOfScreen(spiffyOverlayScreen);
        if (l != null) l.loadEarly = true;
        return l;
    }

    public void onResize() {
        try {
            this.initOverlayScreen(true);
        } catch (Exception ex) {
            LOGGER.error("[SPIFFY HUD] Failed to resize SpiffyGui!", ex);
        }
    }

    public void tick() {
        try {
            if (Shared.reInitHudLayouts) {
                Shared.reInitHudLayouts = false;
                this.initOverlayScreen(true);
            }
        } catch (Exception ex) {
            LOGGER.error("[SPIFFY HUD] Failed to tick SpiffyGui!", ex);
        }
    }

    private void setNewOverlayScreen() {
        spiffyOverlayScreen = new SpiffyOverlayScreen(false);
        ScreenCustomizationLayerHandler.registerScreen(spiffyOverlayScreen);
        this.getLayer(); //dummy call to let the method set loadEarly to true
    }

    private void initOverlayScreen(boolean resize) {
        this.runLayerTask(() -> {
            try {
                double cachedScale = Minecraft.getInstance().getWindow().getGuiScale();
                if (!resize) {
                    EventHandler.INSTANCE.postEvent(new OpenScreenEvent(spiffyOverlayScreen));
                }
                spiffyOverlayScreen.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                spiffyOverlayScreen.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                EventHandler.INSTANCE.postEvent(new InitOrResizeScreenStartingEvent(spiffyOverlayScreen, resize ? InitOrResizeScreenEvent.InitializationPhase.RESIZE : InitOrResizeScreenEvent.InitializationPhase.INIT));
                EventHandler.INSTANCE.postEvent(new InitOrResizeScreenEvent.Pre(spiffyOverlayScreen, resize ? InitOrResizeScreenEvent.InitializationPhase.RESIZE : InitOrResizeScreenEvent.InitializationPhase.INIT));
                spiffyOverlayScreen.init(Minecraft.getInstance(), spiffyOverlayScreen.width, spiffyOverlayScreen.height);
                EventHandler.INSTANCE.postEvent(new InitOrResizeScreenEvent.Post(spiffyOverlayScreen, resize ? InitOrResizeScreenEvent.InitializationPhase.RESIZE : InitOrResizeScreenEvent.InitializationPhase.INIT));
                EventHandler.INSTANCE.postEvent(new InitOrResizeScreenCompletedEvent(spiffyOverlayScreen, resize ? InitOrResizeScreenEvent.InitializationPhase.RESIZE : InitOrResizeScreenEvent.InitializationPhase.INIT));
                if (!resize) {
                    EventHandler.INSTANCE.postEvent(new OpenScreenPostInitEvent(spiffyOverlayScreen));
                }
                //This is to ignore scale changes applied by Spiffy layouts (because scaling not supported)
                Minecraft.getInstance().getWindow().setGuiScale(cachedScale);
            } catch (Exception ex) {
                LOGGER.error("[SPIFFY HUD] Failed to initialize SpiffyOverlayScreen!", ex);
            }
        });
    }

    private void runLayerTask(@NotNull Runnable run) {
        try {
            boolean customizationEnabled = ScreenCustomization.isScreenCustomizationEnabled();
            ScreenCustomization.setScreenCustomizationEnabled(true);
            Screen current = Minecraft.getInstance().screen;
            if (!(current instanceof SpiffyOverlayScreen)) {
                Minecraft.getInstance().screen = spiffyOverlayScreen;
                run.run();
                Minecraft.getInstance().screen = current;
            }
            ScreenCustomization.setScreenCustomizationEnabled(customizationEnabled);
        } catch (Exception ex) {
            LOGGER.error("[SPIFFY HUD] Failed to run layer task!", ex);
        }
    }

}
