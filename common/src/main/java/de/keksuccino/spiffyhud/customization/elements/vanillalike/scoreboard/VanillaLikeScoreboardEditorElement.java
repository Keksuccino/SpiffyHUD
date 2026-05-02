package de.keksuccino.spiffyhud.customization.elements.vanillalike.scoreboard;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.input.TextValidators;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.spiffyhud.util.SpiffyAlignment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeScoreboardEditorElement extends AbstractEditorElement<VanillaLikeScoreboardEditorElement, VanillaLikeScoreboardElement> {

    public VanillaLikeScoreboardEditorElement(@NotNull VanillaLikeScoreboardElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setStretchable(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setResizeable(false);
        this.settings.setParallaxAllowed(false);
    }

    @Override
    public void init() {

        super.init();

        this.rightClickMenu.addValueCycleEntry("body_alignment", SpiffyAlignment.TOP_LEFT.cycle(this.getElement().spiffyAlignment)
                        .addCycleListener(alignment -> {
                            editor.history.saveSnapshot();
                            this.getElement().spiffyAlignment = alignment;
                        }))
                .setStackable(false);

        this.rightClickMenu.addSeparatorEntry("separator_after_body_alignment");

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "title_background_color", VanillaLikeScoreboardEditorElement.class,
                consumes -> (consumes.getElement().customTitleBackgroundColor != null) ? consumes.getElement().customTitleBackgroundColor.getHex() : null,
                (element, s) -> {
                    if (s != null) {
                        element.getElement().customTitleBackgroundColor = DrawableColor.of(s);
                        if (element.getElement().customTitleBackgroundColor == DrawableColor.EMPTY) element.getElement().customTitleBackgroundColor = null;
                    } else {
                        element.getElement().customTitleBackgroundColor = null;
                    }
                },
                null, false, false, Component.translatable("spiffyhud.elements.vanillalike.scoreboard.title_background_color"),
                true, null, TextValidators.HEX_COLOR_TEXT_VALIDATOR, null);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "lines_background_color", VanillaLikeScoreboardEditorElement.class,
                consumes -> (consumes.getElement().customLineBackgroundColor != null) ? consumes.getElement().customLineBackgroundColor.getHex() : null,
                (element, s) -> {
                    if (s != null) {
                        element.getElement().customLineBackgroundColor = DrawableColor.of(s);
                        if (element.getElement().customLineBackgroundColor == DrawableColor.EMPTY) element.getElement().customLineBackgroundColor = null;
                    } else {
                        element.getElement().customLineBackgroundColor = null;
                    }
                },
                null, false, false, Component.translatable("spiffyhud.elements.vanillalike.scoreboard.lines_background_color"),
                true, null, TextValidators.HEX_COLOR_TEXT_VALIDATOR, null);

    }

    public VanillaLikeScoreboardElement getElement() {
        return (VanillaLikeScoreboardElement) this.element;
    }

}
