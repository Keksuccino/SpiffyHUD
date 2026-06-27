package de.keksuccino.spiffyhud.customization.elements.playernbthelper;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PlayerNbtHelperEditorElement extends AbstractEditorElement<PlayerNbtHelperEditorElement, PlayerNbtHelperElement> {

    public PlayerNbtHelperEditorElement(@NotNull PlayerNbtHelperElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);
        this.settings.setParallaxAllowed(false);
        this.settings.setFadeable(false);
        this.settings.setOpacityChangeable(false);
        this.settings.setAutoSizingAllowed(false);
        this.settings.setStickyAnchorAllowed(false);
        this.settings.setAdvancedPositioningSupported(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setStayOnScreenAllowed(false);
        this.settings.setStretchable(false);
        this.settings.setDelayable(false);
        this.settings.setIdentifierCopyable(false);

    }

    @Override
    public void init() {

        super.init();

        this.rightClickMenu.addClickableEntry("nbt_paths_screen", Component.translatable("spiffyhud.elements.player_nbt_helper.paths"), (contextMenu, clickableContextMenuEntry) -> {
            Minecraft.getInstance().gui.setScreen(new PlayerNbtPathHelpScreen(this.editor));
        });

    }

    public PlayerNbtHelperElement getElement() {
        return (PlayerNbtHelperElement) this.element;
    }

}
