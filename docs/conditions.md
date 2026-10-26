# Spiffy's Requirements

Requirements in SpiffyHUD are condition-based rules that determine when HUD elements should be displayed or hidden. SpiffyHUD extends FancyMenu's requirement system by adding HUD-specific conditions that are particularly useful for in-game interfaces. These additional requirements complement FancyMenu's existing collection to provide finer control over when and how your HUD elements appear.

# Available Requirements

## Is Player Using Item

**Identifier**: `is_player_using_item`  
**Description**: Returns true when the player is using an item. Using an item refers to activities such as eating food, drinking a potion, drawing a bow, etc.  
**Use Cases**:

1. **Item Use Progress HUD**: Show a progress bar only when the player is consuming items
2. **Eating Indicators**: Display food-specific information when consuming food items
3. **Combat HUD Elements**: Show specialized combat indicators when drawing a bow or charging a crossbow

**Notes**: 
- Combines well with the `player_item_use_progress` placeholder to show item use progression
- Can be used to create contextual HUD elements that only appear during specific activities
- No parameters needed - simply checks the player's current state

## Is Player In Structure

**Identifier**: `is_player_in_structure`  
**Parameters**:
- `key` (required): The structure key/type to check for

**Description**: Checks if the player is currently in a specific structure type.  
**Use Cases**:

1. **Location-Specific HUD**: Show different HUD elements based on the player's location
2. **Structure Alerts**: Display warnings when entering dangerous structures like Nether fortresses or woodland mansions
3. **Environmental Information**: Show structure-specific details like village statistics when in a village


**Notes**:
- Requires SpiffyHUD to be installed on the server for this requirement to work
- Structure detection is based on Minecraft's structure generation system
- Can be used to create location-aware HUD elements that respond to the player's environment

# Usage Tips

1. **Context-Sensitive HUDs**: Create HUDs that change based on what the player is doing or where they are located.

2. **Optimization**: Use requirements to only show HUD elements when they're relevant, reducing visual clutter.

3. **Gameplay Enhancement**: Design specialized HUDs for different activities (combat, exploration, building) that appear only when needed.

4. **Integration with Placeholders**: Combine requirements with placeholders for powerful conditional displays.

5. **Structure-Based Information**: Create specialized displays for different structures, like danger meters in the Nether or treasure finders in ancient cities.
