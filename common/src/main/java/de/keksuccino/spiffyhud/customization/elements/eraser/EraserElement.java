package de.keksuccino.spiffyhud.customization.elements.eraser;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.util.enums.LocalizedCycleEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EraserElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();

    @NotNull
    public AggressionLevel aggressionLevel = AggressionLevel.NORMAL;

    public EraserElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    @Override
    public boolean supportsRotation() {
        return false;
    }

    @Override
    public boolean supportsTilting() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        if (isEditor()) {
            int x = this.getAbsoluteX();
            int y = this.getAbsoluteY();
            int w = this.getAbsoluteWidth();
            int h = this.getAbsoluteHeight();
            graphics.fill(x, y, x + w, y + h, this.inEditorColor.getColorInt());
            graphics.enableScissor(x, y, x + w, y + h);
            graphics.drawCenteredString(Minecraft.getInstance().font, this.getDisplayName(), x + (w / 2), y + (h / 2) - (Minecraft.getInstance().font.lineHeight / 2), -1);
            graphics.disableScissor();
        }

    }

    public enum AggressionLevel implements LocalizedCycleEnum<AggressionLevel> {

        NORMAL("normal"),
        AGGRESSIVE("aggressive");

        private final String name;

        AggressionLevel(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getLocalizationKeyBase() {
            return "spiffyhud.aggression_level";
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
        public @NotNull AggressionLevel[] getValues() {
            return AggressionLevel.values();
        }

        @Override
        @Nullable
        public AggressionLevel getByNameInternal(@NotNull String name) {
            return AggressionLevel.getByName(name);
        }

        @Nullable
        public static AggressionLevel getByName(@NotNull String name) {
            for (AggressionLevel a : AggressionLevel.values()) {
                if (a.name.equals(name)) return a;
            }
            return null;
        }

    }

}
