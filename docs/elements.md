# Spiffy's Elements

SpiffyHUD adds many new elements to FancyMenu, specifically made to be used in the HUD.

Spiffy's elements only work in the HUD.

# Elements in Detail

The following list contains most, if not all, elements that SpiffyHUD adds to the base mod (FancyMenu). The list can sometimes be a little outdated due to updates of SpiffyHUD.

## Inventory Slot
Displays the contents of a specific inventory slot (or the currently selected hotbar slot) with optional durability overlay.

*   **Use Cases:**
    *   Mirror your selected hotbar item near a custom crosshair or compass.
    *   Pin an armor/offhand slot to the HUD for quick condition checks.
    *   Create custom inventory readouts inside cinematic HUD layouts.
*   **Key Features:**
    *   Can follow the selected hotbar slot or a fixed slot ID (0–40 as explained in the element’s help popup).
    *   Optional durability bar rendering for tools and armor.
    *   Works with FancyMenu positioning, scaling, and animations for precise placement.

## Single-Line Text
Renders a single JSON chat component, letting you reuse `/tellraw` style text (including hover/click actions) directly in the HUD.

*   **Use Cases:**
    *   Add short labels, tips, or roleplay captions that stay readable in-game.
    *   Show placeholder-driven stats (e.g., player speed or biome) beside other HUD pieces.
    *   Display stylized status banners using Minecraft’s formatting codes.
*   **Key Features:**
    *   Accepts full JSON text components and FancyMenu placeholders.
    *   Adjustable text scale for pixel-perfect alignment with other elements.
    *   Honors Minecraft color/formatting codes without extra setup.

## Player Mirror
Shows the live 3D model of the local player just like the Inventory preview.

*   **Use Cases:**
    *   Create selfie-style overlays for streams or screenshots.
    *   Let players check armor skins and cosmetics without opening menus.
    *   Build RPG character cards inside the HUD.
*   **Key Features:**
    *   Uses the real client player model, armor, cape, and held items.
    *   Animates naturally with current pose and movement.
    *   Fully repositionable/scalable through FancyMenu layout controls.

## Chat Customizer
Lets you adjust chat placement and visuals directly from the HUD.

*   **Use Cases:**
    *   Move chat to a different corner for ultra-wide monitors.
    *   Apply semi-transparent themes that match your HUD colors.
    *   Increase line spacing for readability during fast combat.
*   **Key Features:**
    *   Choose the screen corner where chat renders.
    *   Independent color controls for chat background and input field (HEX or placeholders).
    *   Configurable line spacing for dense or airy layouts.

## Player NBT Helper
Utility element that exposes every NBT path of the client player for use with placeholders.

*   **Use Cases:**
    *   Discover exact NBT paths when wiring new variable-driven HUD pieces.
    *   Debug custom datapack or mod attributes stored on the player.
    *   Document NBT keys for team members without external tools.
*   **Key Features:**
    *   Lists all available player NBT data paths directly in-game.
    *   Designed purely for inspection—no rendering noise when unused.
    *   Pairs with FancyMenu’s “Get NBT Data” placeholders.

## Overlay Remover
Removes built-in screen overlays such as pumpkin head overlay, portal overlay, powder snow frost overlay, spyglass overlay, and the world-border vignette.

*   **Use Cases:**
    *   Clean screenshots or cinematic recordings without swapping resource packs.
    *   Accessibility-friendly HUDs by suppressing obstructive overlays.
    *   Allow modpacks to disable specific overlays while keeping others intact.
*   **Key Features:**
    *   Overlay-type selector (all, portal, powder snow, pumpkin, spyglass, vignette).
    *   Works per-layout, so different HUDs can suppress different overlays.
    *   Simple area-based element you can stack or animate with FancyMenu tools.

## Eraser
Area-based remover that hides any HUD content rendered beneath its bounds. Useful for modded HUD pieces Spiffy cannot move directly.

*   **Use Cases:**
    *   Delete parts of third-party HUDs without disabling the mods that add them.
*   **Key Features:**
    *   Any HUD fragment fully covered by the eraser disappears; partial coverage keeps it visible.
    *   Independent of element type—affects vanilla and modded overlays alike.

## Compass
Renders a horizontal RPG-style compass bar with optional markers, needles, and mob dots.

*   **Use Cases:**
    *   Build exploration or dungeon HUDs that highlight objectives and last-death spots.
    *   Provide lightweight radar for nearby hostiles or passive mobs.
    *   Show party-made world markers with distance labels during events.
*   **Key Features:**
    *   Customizable background, bar, tick lines, and cardinal letter textures/colors.
    *   World markers and last-death pointer with optional distance labels and textures.
    *   Separate hostile/passive mob dots (or full models) with range, scale, and texture controls, plus placeholder-friendly offsets.

## Player Heart Health Bar
Customizable heart rows that track player health, including absorption and status variants.

*   **Use Cases:**
    *   Replace vanilla hearts with themed textures for packs or servers.
    *   Add a minimal, multi-row health readout for hardcore overlays.
    *   Emphasize danger with shake/blink feedback at low health.
*   **Key Features:**
    *   Per-state textures (normal, absorption, burning, frozen, poison, wither, empty/half/full).
    *   Configurable hearts-per-row, spacing, scale, blinking, and low-health shake threshold.
    *   Supports placeholder-driven scaling for animated layouts.

## Player Food Icon Bar
Shows hunger as rows of customizable food icons, with support for normal and “hungry” textures.

*   **Use Cases:**
    *   Match hunger icons to custom resource-pack art styles.
    *   Build compact multi-row food bars for minimal HUDs.
    *   Provide motion feedback (blink/shake) when food is lost or low.
*   **Key Features:**
    *   Per-state textures (normal full/half, hungry full/half, empty).
    *   Adjustable icons-per-row, horizontal gaps, scale, and low-food shake threshold.
    *   Optional blink when losing hunger.

## Player Armor Icon Bar
Displays armor value as customizable icon rows.

*   **Use Cases:**
    *   Theme armor pips to match faction colors or server branding.
    *   Present armor on a dedicated HUD rail separate from health/food.
    *   Highlight loss events with blink or shake to draw attention.
*   **Key Features:**
    *   Textures for full/half/empty armor icons with scaling and per-row layout controls.
    *   Optional blink on damage and low-armor shake threshold.
    *   Adjustable horizontal gaps for tight or airy spacing.

## Player Air Bubble Bar
Tracks remaining breath with bubble rows and optional pop/shake effects.

*   **Use Cases:**
    *   Diving or underwater base HUDs that keep air status front and center.
    *   Stylized bubble displays using custom textures or colors.
    *   Animated “panic” feedback when air is low.
*   **Key Features:**
    *   Per-state textures (full, half, empty, popping) with scale control.
    *   Configurable bubbles-per-row, gaps, and optional pop animation duration.
    *   Low-air shake toggle with threshold for attention-grabbing motion.

## Mount Heart Health Bar
Renders mount health as customizable heart rows, falling back to empty hearts when no mount is ridden.

*   **Use Cases:**
    *   Show horse/strider health near the hotbar without vanilla placement limits.
    *   Theme mount hearts differently from player hearts for clarity.
    *   Keep layouts stable while dismounted thanks to empty-heart fallback.
*   **Key Features:**
    *   Same texture set as player hearts (including burning, frozen, poison, wither, absorption).
    *   Configurable hearts-per-row, spacing, scale, blink, and low-health shake threshold.
    *   Respects FancyMenu positioning and sizing rules for seamless alignment.

## Vanilla-Like Hotbar
Faithful recreation of the vanilla hotbar that you can move or scale with FancyMenu.

*   **Use Cases:**
    *   Relocate the hotbar for ultra-wide or VR-friendly HUDs.
    *   Run side-by-side with custom bars for comparison or transition layouts.
    *   Keep the vanilla look while pairing with other Spiffy elements.
*   **Key Features:**
    *   Uses vanilla rendering (item cooldowns, selected slot highlight, offhand overlay).
    *   Sticky anchor enabled by default for classic bottom alignment.
    *   Honors resource packs so textures stay consistent with the game.

## Vanilla-Like Scoreboard Sidebar
Shows the live scoreboard sidebar with vanilla styling and optional background tinting.

*   **Use Cases:**
    *   Move objective lists out of the way of minimaps or chat.
    *   Match scoreboard placement to custom themed HUD corners.
    *   Provide per-layout scoreboard visibility for minigames or events.
*   **Key Features:**
    *   Renders the real scoreboard title and lines with dynamic sizing.
    *   Separate background color controls for title and lines (HEX).
    *   Stays in sync with server objectives without extra scripting.

## Vanilla-Like Player Health Bar
Recreates the vanilla heart bar for the player.

*   **Use Cases:**
    *   Relocate default hearts without changing their look.
    *   Keep hardcore-style hearts while using custom HUD layouts.
*   **Key Features:**
    *   Vanilla heart rendering including absorption, poison, wither, and hardcore variants.
    *   Sticky anchoring by default to mirror vanilla behavior.
    *   Respects game rules and status effects automatically.

## Vanilla-Like Player Food Bar
Recreates the standard hunger bar.

*   **Use Cases:**
    *   Move hunger to a new column beside custom health displays.
    *   Keep vanilla visuals for players who prefer familiar cues.
*   **Key Features:**
    *   Vanilla food icons with saturation “hungry” states and flashing at low food.
    *   Sticky anchoring for easy bottom alignment.
    *   Auto-updates with player hunger and status effects (e.g., hunger debuff).

## Vanilla-Like Player Armor Bar
Faithful armor pip rendering.

*   **Use Cases:**
    *   Align armor next to custom heart or stamina displays.
    *   Preserve vanilla readability while shifting overall HUD layout.
    *   Provide an alternate location for accessibility-focused packs.
*   **Key Features:**
    *   Uses vanilla armor icons (full/half) with correct enchantment shine behavior.
    *   Sticky anchoring enabled by default.
    *   Scales with FancyMenu sizing/positioning settings.

## Vanilla-Like Player Air Bar
Standard bubble bar that appears when underwater.

*   **Use Cases:**
    *   Place air status near minimaps or diving HUD clusters.
    *   Keep vanilla feedback while hiding the original bubble bar.
    *   Pair with underwater resource-pack themes without custom textures.
*   **Key Features:**
    *   Vanilla bubble rendering with correct depletion timing and flashing.
    *   Only appears when relevant, matching vanilla logic.
    *   Sticky anchoring defaults for bottom placement.

## Vanilla-Like Player Experience Bar
Dynamic contextual bar that switches between the XP bar, jump meter, or locator bar depending on gameplay state.

*   **Use Cases:**
    *   Move the XP bar to a custom location without losing jump-meter support for mounts.
    *   Surface the new 1.21 locator bar alongside waypoints in bespoke HUDs.
    *   Keep layouts stable by forcing the locator or XP bar to always show in editors or presets.
*   **Key Features:**
    *   Automatically chooses experience, jump, or locator display using vanilla rules (waypoints, mounts, recent XP gain).
    *   Options to always show the locator bar or always show the XP bar even in editor mode.
    *   Uses vanilla sprites and level text rendering, including contextual priority and fade timings.

## Vanilla-Like Mount Health Bar
Recreates the default mount heart bar.

*   **Use Cases:**
    *   Move horse/strider health to a clearer spot when riding.
    *   Keep vanilla hearts for mounts while using custom player health visuals.
*   **Key Features:**
    *   Vanilla mount heart rendering with correct row splits for large health pools.
    *   Sticky anchoring for familiar positioning.
    *   Only appears while mounted, staying hidden otherwise.

## Vanilla-Like Effects Overlay
Shows active status effects using the vanilla list style.

*   **Use Cases:**
    *   Move potion effects away from minimaps or chat.
    *   Build compact effect columns for modded HUD presets.
    *   Keep vanilla icons/timers for player familiarity.
*   **Key Features:**
    *   Uses vanilla icons, timers, and ordering.
    *   Resizes automatically to current effect count.
    *   Sticky anchor defaults help mirror the original placement.

## Vanilla-Like Boss Overlay
Stacks vanilla boss bars in a movable container.

*   **Use Cases:**
    *   Keep boss bars visible while using cinematic camera overlays.
    *   Prevent overlap with custom center-screen widgets.
    *   Relocate bars for multi-boss encounters or datapack events.
*   **Key Features:**
    *   Renders the same boss bars the vanilla HUD would show (dragon, warden pulse, etc.).
    *   Automatic stacking and resizing based on active bosses.
    *   Sticky anchoring for quick placement along screen edges.

## Vanilla-Like Attack Indicator
Replicates the vanilla attack indicator, with a toggle for the hotbar-style or crosshair-style version.

*   **Use Cases:**
    *   Place the indicator near custom crosshairs or weapon stats.
    *   Keep PvP timing cues visible after hiding the default HUD.
    *   Offer alternate layouts that swap between crosshair and hotbar indicators.
*   **Key Features:**
    *   Hotbar-style toggle mirrors the vanilla “Attack Indicator: HOTBAR/CROSSHAIR” option.
    *   Uses vanilla fill/ready animation and cooldown timing.
    *   Sticky anchoring defaults for familiar alignment, adjustable via FancyMenu.
