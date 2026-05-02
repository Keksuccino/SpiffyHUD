package de.keksuccino.spiffyhud.customization.elements.overlayremover;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import de.keksuccino.fancymenu.util.Pair;
import de.keksuccino.fancymenu.util.enums.LocalizedCycleEnum;
import de.keksuccino.fancymenu.util.rendering.RenderingUtils;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class OverlayRemoverElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    @NotNull
    public OverlayType overlayType = OverlayType.VIGNETTE;

    protected static final Map<OverlayType, Pair<Long, Boolean>> CACHED_OVERLAY_VISIBILITY = new HashMap<>();

    public OverlayRemoverElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        if (isEditor()) {
            int x = this.getAbsoluteX();
            int y = this.getAbsoluteY();
            int w = this.getAbsoluteWidth();
            int h = this.getAbsoluteHeight();
            graphics.fill(x, y, x + w, y + h, this.inEditorColor.getDrawable().getColorInt());
            graphics.enableScissor(x, y, x + w, y + h);
            graphics.drawCenteredString(Minecraft.getInstance().font, this.getDisplayName(), x + (w / 2), y + (h / 2) - (Minecraft.getInstance().font.lineHeight / 2), -1);
            graphics.disableScissor();
        }

    }

    public enum OverlayType implements LocalizedCycleEnum<OverlayType> {

        POWDER_SNOW("powder_snow"),
        PUMPKIN("pumpkin"),
        VIGNETTE("vignette"),
        SPYGLASS("spyglass"),
        PORTAL("portal"),
        ALL("all");

        public final String name;

        OverlayType(@NotNull String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getLocalizationKeyBase() {
            return "spiffyhud.elements.overlay_remover.overlay_type";
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }

        @Override
        public @NotNull Style getValueComponentStyle() {
            return WARNING_TEXT_STYLE.get();
        }

        @Override
        public @NotNull OverlayType[] getValues() {
            return OverlayType.values();
        }

        @Override
        public @Nullable OverlayType getByNameInternal(@NotNull String s) {
            return getByName(s);
        }

        @Nullable
        public static OverlayType getByName(@NotNull String name) {
            for (OverlayType type : OverlayType.values()) {
                if (type.name.equals(name)) return type;
            }
            return null;
        }

    }

    public static boolean isOverlayTypeHidden(@NotNull OverlayType type) {
        long now = System.currentTimeMillis();
        Pair<Long, Boolean> cached = CACHED_OVERLAY_VISIBILITY.get(type);
        if (cached != null) {
            if ((cached.getFirst() + 100) > now) {
                return cached.getSecond();
            }
        }
        boolean returnVal = false;
        ScreenCustomizationLayer layer = ScreenCustomizationLayerHandler.getLayerOfScreen(SpiffyOverlayScreen.class);
        if (layer != null) {
            for (AbstractElement element : layer.allElements) {
                if (element instanceof OverlayRemoverElement e) {
                    if (e.shouldRender()) {
                        if (e.overlayType == OverlayType.ALL) returnVal = true;
                        if (e.overlayType == type) returnVal = true;
                        if (returnVal) break;
                    }
                }
            }
        }
        CACHED_OVERLAY_VISIBILITY.put(type, Pair.of(now, returnVal));
        return returnVal;
    }

}
