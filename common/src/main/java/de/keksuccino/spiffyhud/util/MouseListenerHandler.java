package de.keksuccino.spiffyhud.util;

import de.keksuccino.fancymenu.customization.ScreenCustomization;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class MouseListenerHandler {

    private static final Map<String, MouseListener> ACTIONS = new HashMap<>();

    public static String addListener(@NotNull MouseListener listener) {
        String id = ScreenCustomization.generateUniqueIdentifier();
        ACTIONS.put(id, listener);
        return id;
    }

    public static void removeListener(@NotNull String id) {
        ACTIONS.remove(id);
    }

    public static void notifyListeners(int button, int action, int modifiers) {
        ACTIONS.forEach((s, listener) -> listener.clicked(button, action, modifiers));
    }

    @FunctionalInterface
    public interface MouseListener {
        void clicked(int button, int action, int modifiers);
    }

}
