# Spiffy's Placeholders

Placeholders are dynamic text elements that display real-time information about the game state, player, or environment. SpiffyHUD extends FancyMenu's extensive placeholder system by adding additional HUD-specific placeholders to the mix. FancyMenu already provides a rich collection of placeholders for UI customization, and SpiffyHUD builds upon this foundation with specialized placeholders focused on in-game HUD elements and player state information.

By leveraging both FancyMenu's existing placeholders and SpiffyHUD's additional offerings, you can create highly dynamic and informative HUD layouts that respond to various game conditions and player states. SpiffyHUD's placeholders are designed specifically to enhance your in-game HUD customization experience.

# Available Placeholders

## Camera Related

### Camera Rotation X
**Identifier**: `camera_rotation_x`  
**Description**: Returns the camera's X rotation (pitch) as a value between -180.0 and 180.0 degrees.  
**Example Output**: `45.32`  
**Usage Example**: `Pitch: {"placeholder":"camera_rotation_x"}`  
**Notes**: 
- Looking straight ahead is 0 degrees
- Looking up produces negative values
- Looking down produces positive values

### Camera Rotation Y
**Identifier**: `camera_rotation_y`  
**Description**: Returns the camera's Y rotation (yaw) as a value between -180.0 and 180.0 degrees.  
**Example Output**: `-95.67`  
**Usage Example**: `Yaw: {"placeholder":"camera_rotation_y"}`  
**Notes**:
- -180/180 degrees is looking north
- -90 degrees is looking east
- 0 degrees is looking south
- 90 degrees is looking west

### Camera Rotation Delta X
**Identifier**: `camera_rotation_delta_x`  
**Description**: Displays the change in camera pitch (X axis) between ticks as a decimal value. The value may be positive or negative.  
**Example Output**: `-1.25` (when looking up), `2.75` (when looking down)  
**Usage Example**: `Pitch Change: {"placeholder":"camera_rotation_delta_x"}`  
**Notes**: Useful for tracking camera movement speed or creating motion-based HUD elements.

### Camera Rotation Delta Y
**Identifier**: `camera_rotation_delta_y`  
**Description**: Displays the change in camera yaw (Y axis) between ticks as a decimal value. The value may be positive or negative.  
**Example Output**: `-3.12` (when turning right), `2.45` (when turning left)  
**Usage Example**: `Yaw Change: {"placeholder":"camera_rotation_delta_y"}`  
**Notes**: Useful for tracking rotation speed or creating turn indicators.

## Player Movement Related

### Player Position Delta X
**Identifier**: `player_position_delta_x`  
**Description**: Displays the change in player position along the X axis between ticks as a decimal value. The value can be positive or negative.  
**Example Output**: `0.23` (when moving east), `-0.23` (when moving west)  
**Usage Example**: `X Movement: {"placeholder":"player_position_delta_x"}`  
**Notes**: Useful for tracking lateral movement speed.

### Player Position Delta Y
**Identifier**: `player_position_delta_y`  
**Description**: Displays the change in player position along the Y axis between ticks as a decimal value. The value can be positive or negative.  
**Example Output**: `0.42` (when moving up), `-0.08` (when falling/moving down)  
**Usage Example**: `Vertical Speed: {"placeholder":"player_position_delta_y"}`  
**Notes**: Useful for jump meters, fall indicators, or elevator displays.

### Player Position Delta Z
**Identifier**: `player_position_delta_z`  
**Description**: Displays the change in player position along the Z axis between ticks as a decimal value. The value can be positive or negative.  
**Example Output**: `0.31` (when moving south), `-0.31` (when moving north)  
**Usage Example**: `Z Movement: {"placeholder":"player_position_delta_z"}`  
**Notes**: Useful for tracking forward/backward movement speed.

## Player Item Related

### Player Item Use Progress
**Identifier**: `player_item_use_progress`  
**Description**: Returns the current item use progress of the player as a decimal between 0.0 (0%) and 1.0 (100%). This applies to eating food, drinking potions, drawing a bow, etc.  
**Example Output**: `0.45` (45% progress)  
**Usage Example**: `Eating: {"placeholder":"player_item_use_progress"}`  
**Notes**: Can be combined with the "Is Player Using Item" requirement for conditional display.

### Slot Item Display Name
**Identifier**: `slot_item_display_name`  
**Parameters**:
- `slot` (required): The inventory slot ID to check
- `ignore_spectator` (optional): Set to "true" to ignore Spectator GUI slots in Spectator mode

**Description**: Gets the display name of an item in a specific inventory slot. Returns the display name in Minecraft's JSON text component format.
**Example Output**: `{"text":"Iron Helmet","color":"gray"}`  
**Usage Example**: `Hotbar 1: {"placeholder":"slot_item_display_name","values":{"slot":"0"}}`  
**Usage with multiple parameters**: `Hotbar 1: {"placeholder":"slot_item_display_name","values":{"slot":"0","ignore_spectator":"true"}}`  
**Notes**: 
- Slot IDs are explained in detail in the Slot ID Help screen within the mod
- Common slots: 0-8 (hotbar), 36-39 (armor), 40 (offhand)
- Normal Text elements can't parse JSON text components. You need to use a **Single-Line Text** element for parsing the component. Make sure to put only the placeholder in the content of the Single-Line Text element for parsing it.

## Game UI Related

### Action Bar Message
**Identifier**: `action_bar_message`  
**Description**: Gets the current action bar message (the text above the hotbar). Returns an empty text when no action bar message is displayed.  
**Example Output**: `"Found Cave"`, `"Now playing: C418 - Sweden"`  
**Usage Example**: `Latest Message: {"placeholder":"action_bar_message"}`  
**Notes**: Useful for preserving or repositioning action bar messages.

### Action Bar Message Time
**Identifier**: `action_bar_message_time`  
**Description**: The time left in ticks that the original vanilla action bar message gets displayed.  
**Example Output**: `40` (2 seconds left)  
**Usage Example**: `Message Time: {"placeholder":"action_bar_message_time"}`  
**Notes**: 20 ticks = 1 second in normal gameplay.

### Highlighted Item Time
**Identifier**: `highlighted_item_time`  
**Description**: The time left in ticks that the original vanilla highlighted item's name gets displayed above the hotbar.  
**Example Output**: `35` (1.75 seconds left)  
**Usage Example**: `Item Display Time: {"placeholder":"highlighted_item_time"}`  
**Notes**: Useful for creating custom item highlight displays with precise timing.

# Usage Tips

1. **Formatting Numbers**: Many placeholders return decimal values that may have many digits. Consider using a text formatter to limit decimal places.

2. **Conditional Display**: Combine placeholders with requirements like "Is Player Using Item" for contextual HUD elements.

3. **Performance Considerations**: Some placeholders (particularly NBT data) may have performance impacts if updated too frequently. Use refresh_cooldown where appropriate.

4. **Combining Placeholders**: Multiple placeholders can be used in a single text element:
   ```
   Speed: X={"placeholder":"player_position_delta_x"} Y={"placeholder":"player_position_delta_y"} Z={"placeholder":"player_position_delta_z"}
   ```

5. **Creating Specialized HUDs**: Combine movement delta placeholders to create speedometers, or use rotation deltas to create orientation indicators.
