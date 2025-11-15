package de.keksuccino.spiffyhud.customization.actions;

import de.keksuccino.fancymenu.customization.action.ActionRegistry;
import de.keksuccino.spiffyhud.customization.actions.marker.AddMarkerAction;
import de.keksuccino.spiffyhud.customization.actions.marker.EditMarkerAction;
import de.keksuccino.spiffyhud.customization.actions.marker.RemoveMarkerAction;

public class Actions {

    public static final AddMarkerAction ADD_MARKER = new AddMarkerAction();
    public static final EditMarkerAction EDIT_MARKER = new EditMarkerAction();
    public static final RemoveMarkerAction REMOVE_MARKER = new RemoveMarkerAction();

    public static void registerAll() {
        ActionRegistry.register(ADD_MARKER);
        ActionRegistry.register(EDIT_MARKER);
        ActionRegistry.register(REMOVE_MARKER);
    }

}
