package de.keksuccino.spiffyhud.customization.elements.slot;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SlotEditorElement extends AbstractEditorElement<SlotEditorElement, SlotElement> {

    public SlotEditorElement(@NotNull SlotElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);
        this.settings.setParallaxAllowed(false);

    }

    @Override
    public void init() {

        super.init();

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "inventory_slot", SlotEditorElement.class,
                slotEditorElement -> slotEditorElement.getElement().slot,
                (slotEditorElement, s) -> slotEditorElement.getElement().slot = s,
                null, false, true, Component.translatable("spiffyhud.elements.slot.slot"), true, "0", null, null)
                .setStackable(false)
                .addIsActiveSupplier((contextMenu, contextMenuEntry) -> !this.getElement().useSelectedSlot);

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "use_selected_slot", SlotEditorElement.class,
                consumes -> consumes.getElement().useSelectedSlot,
                (slotEditorElement, aBoolean) -> slotEditorElement.getElement().useSelectedSlot = aBoolean,
                "spiffyhud.elements.slot.selected");

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "show_durability", SlotEditorElement.class,
                consumes -> consumes.getElement().showDurability,
                (slotEditorElement, aBoolean) -> slotEditorElement.getElement().showDurability = aBoolean,
                "spiffyhud.elements.slot.show_durability");

        this.rightClickMenu.addSeparatorEntry("separator_before_slot_id_help");

        this.rightClickMenu.addClickableEntry("slot_id_help", Component.translatable("spiffyhud.elements.slot.slot_id_help"), (menu, entry) -> {
            this.rightClickMenu.closeMenu();
            Minecraft.getInstance().setScreen(new SlotIdHelpScreen(this.editor));
        });

    }

    public SlotElement getElement() {
        return (SlotElement) this.element;
    }

}
