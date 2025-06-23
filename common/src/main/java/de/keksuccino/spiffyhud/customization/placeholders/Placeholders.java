package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.PlaceholderRegistry;

public class Placeholders {

    public static final CameraRotationXPlaceholder CAMERA_ROTATION_X = new CameraRotationXPlaceholder();
    public static final CameraRotationYPlaceholder CAMERA_ROTATION_Y = new CameraRotationYPlaceholder();
    public static final CameraRotationDeltaXPlaceholder CAMERA_ROTATION_DELTA_X = new CameraRotationDeltaXPlaceholder();
    public static final CameraRotationDeltaYPlaceholder CAMERA_ROTATION_DELTA_Y = new CameraRotationDeltaYPlaceholder();
    public static final PlayerPositionDeltaXPlaceholder PLAYER_POSITION_DELTA_X = new PlayerPositionDeltaXPlaceholder();
    public static final PlayerPositionDeltaYPlaceholder PLAYER_POSITION_DELTA_Y = new PlayerPositionDeltaYPlaceholder();
    public static final PlayerPositionDeltaZPlaceholder PLAYER_POSITION_DELTA_Z = new PlayerPositionDeltaZPlaceholder();
    public static final PlayerItemUseProgressPlaceholder PLAYER_ITEM_USE_PROGRESS = new PlayerItemUseProgressPlaceholder();
    public static final PlayerNbtDataPlaceholder PLAYER_NBT_DATA = new PlayerNbtDataPlaceholder();
    public static final ActionBarMessagePlaceholder ACTION_BAR_MESSAGE = new ActionBarMessagePlaceholder();
    public static final SlotItemDisplayNamePlaceholder SLOT_ITEM_DISPLAY_NAME = new SlotItemDisplayNamePlaceholder();
    public static final ActionBarMessageTimePlaceholder ACTION_BAR_MESSAGE_TIME = new ActionBarMessageTimePlaceholder();
    public static final HighlightedItemTimePlaceholder HIGHLIGHTED_ITEM_TIME = new HighlightedItemTimePlaceholder();

    public static void registerAll() {

        PlaceholderRegistry.register(CAMERA_ROTATION_X);
        PlaceholderRegistry.register(CAMERA_ROTATION_Y);
        PlaceholderRegistry.register(CAMERA_ROTATION_DELTA_X);
        PlaceholderRegistry.register(CAMERA_ROTATION_DELTA_Y);
        PlaceholderRegistry.register(PLAYER_POSITION_DELTA_X);
        PlaceholderRegistry.register(PLAYER_POSITION_DELTA_Y);
        PlaceholderRegistry.register(PLAYER_POSITION_DELTA_Z);
        PlaceholderRegistry.register(PLAYER_ITEM_USE_PROGRESS);
        PlaceholderRegistry.register(PLAYER_NBT_DATA);
        PlaceholderRegistry.register(ACTION_BAR_MESSAGE);
        PlaceholderRegistry.register(SLOT_ITEM_DISPLAY_NAME);
        PlaceholderRegistry.register(ACTION_BAR_MESSAGE_TIME);
        PlaceholderRegistry.register(HIGHLIGHTED_ITEM_TIME);

    }

}
