package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.util.MouseListenerHandler;
import net.minecraft.client.resources.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClicksPerSecondPlaceholder extends Placeholder {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConcurrentLinkedDeque<Long> CLICK_TIMESTAMPS = new ConcurrentLinkedDeque<>();
    private static final long TIME_WINDOW_MS = 1000; // 1 second window
    private static boolean initialized = false;
    private static final Object INIT_LOCK = new Object();

    public ClicksPerSecondPlaceholder() {
        super("clicks_per_second");
        initialize();
    }

    private void initialize() {
        synchronized (INIT_LOCK) {
            if (!initialized) {
                // Register mouse listener for click tracking
                MouseListenerHandler.addListener((button, action, modifiers) -> {
                    // Only track left clicks (button == 0) when pressed (action == 1)
                    if (button == 0 && action == 1) {
                        CLICK_TIMESTAMPS.add(System.currentTimeMillis());
                    }
                });
                initialized = true;
            }
        }
    }

    private void cleanOldClicks() {
        try {
            long currentTime = System.currentTimeMillis();
            long cutoffTime = currentTime - TIME_WINDOW_MS;
            // Remove clicks older than the time window
            CLICK_TIMESTAMPS.removeIf(timestamp -> timestamp < cutoffTime);
        } catch (Exception e) {
            LOGGER.error("[SPIFFYHUD] Error while cleaning old clicks of ClicksPerSecondPlaceholder.", e);
        }
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        // Perform an additional cleanup when getting the value to ensure accuracy
        cleanOldClicks();
        // Return the current click count
        return String.valueOf(CLICK_TIMESTAMPS.size());
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return null;
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.clicks_per_second");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.clicks_per_second.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        return new DeserializedPlaceholderString(this.getIdentifier(), null, "");
    }

}
