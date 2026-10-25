# Frontier menus

The game opens into the layered coffee-enamel terminal main menu. Its landscape,
hinged panel, perimeter rim, button faces and round amber lamp are separate assets,
with live Scene2D button captions. Other pages retain the shared charcoal/brass
menu theme, with amber text on dark surfaces and espresso text on enamel.
The current UI uses bundled Manrope Regular and SemiBold; provenance and the
SIL Open Font License are in `assets/ui/fonts/manrope/`. No new runtime dependency
is required. The older Liberation files remain for legacy references.

## Terminal main menu

The [terminal pack](../../assets/ui-architecture/terminal-layers-v1/README.md)
is assembled at 1920 x 1080 logical units. The full rectangular composition stays
centered and scales uniformly to fit the window, including enlargement on 1440p
and 4K displays. An `ExtendViewport` gives surplus space to a separate, subdued
painted background. At 2560 x 1080 the terminal stays 1920 pixels wide with 320
pixels of painted margin on each side. Taller windows use painted top/bottom
margins. Disabling decorative backgrounds makes these margins plain dark metal
color; it does not remove the actual terminal or its central painting.

`MainScreenGUI` places four terminal PNGs and the shared component-atlas lamps;
neither the flattened
preview nor the ORA document is loaded by the game. `TerminalMenuAssets.PAINTING`
identifies the landscape to replace with an artist's work. Keep its 1920 x 1080
aspect ratio to preserve the composition. The tagline and small riveted plate
remain blank. The grille, socket and volume dial are painted decoration.

The six actions retain their existing behavior. Continue resumes the in-memory
journey and is disabled when none exists. New journey and Quit request confirmation
when appropriate. Button labels remain separate text and honor Larger body text.
Every button shows the round `lamp-off` sprite at rest, including disabled actions.
It switches to the matching `lamp-on` sprite on the hovered action, or the
keyboard-focused action when no other button is hovered. Hover has priority,
so only one lamp is lit. Both states keep the same position and size.
Buttons also have a lighter pressed tint to preserve espresso text contrast;
disabled actions cannot activate or light up.
`Tab` / `Shift+Tab`, `Enter` / `Space` and `Escape` use the shared menu focus rules.
Confirmation text is enlarged for this canvas and dialogs remain modal.

The startup queue loads four terminal textures, the entire 33-region coffee-enamel
atlas and the Manrope fonts through `AssetManager`. `theme.components.region(name)`
exposes the loaded surfaces, icons, lamps, hinges, screws, dials and other hardware.
Terminal textures use mipmaps and linear filtering; the existing atlas uses linear
filtering. Both Manrope weights are static instances with the source license retained.
The 2x button font is rendered at half scale inside the reference composition.
Stages own their batches; the application owns the shared textures and fonts.
Returning to the title rebuilds its controls without unloading its art. Other menu
viewports and the world renderer are unchanged. The full-display 3D main-map
direction remains separate from this menu implementation.

### Runtime verification

Run `./gradlew test lwjgl3:terminalMenuSmoke` with a desktop OpenGL environment.
On Linux without a display server, `./gradlew lwjgl3:terminalMenuSmoke -PheadlessTerminal`
uses GLFW's null platform and a real OSMesa context (requires the system OSMesa
library). That mode is restricted to the test launcher; the game launcher is unchanged.
The harness starts the real `Main` with isolated preferences and checks navigation,
all 33 atlas regions, Manrope loading, amber/espresso styles, idle/disabled lamp-off,
disabled/enabled Continue, keyboard activation, hover/focus precedence, modal
confirmation/cancellation, input mapping after resize, label bounds, and asset lifetime.
It checks 1920 x 1080, 1280 x 720 with larger text, 2560 x 1080, 900 x 600,
2560 x 1440 and 3840 x 2160, plus the decorations setting and hide/show reuse.
Captures and a result file go to `lwjgl3/build/terminal-smoke/`.

All 32 JVM tests and the updated real GL smoke harness passed on 2026-10-26 using
software OpenGL/OSMesa. Idle, hovered, large-text and confirmation captures were
visually reviewed. The optional kit helper also compiled for Java 11 / libGDX 1.13.1.
These checks do not measure production GPU performance or test
every operating system's display-density settings. Enlarging the artwork does
not add detail to the original generated sources; higher-resolution artist masters
can replace the separate paintings later.

Terminal review screenshots are generated locally by the smoke harness; this code update omits them.

## Play through the menus

Run `./gradlew lwjgl3:run` from the repository root.

1. Choose **New journey**, enter a name, select a background and calling, assign physical and mental attributes on separate steps, and review the starting kit. The standard array spends all 27 points.
2. Sign the ledger to begin at Mercy Crossing. On **Travel**, select Blackwire Pass and confirm the journey. Travel consumes supplies and advances the campaign clock.
3. Select the current location and choose **Enter local area** to explore the existing world scene.
4. Press **Escape** to pause. Open the character sheet, inventory, journal, settings, or save/load archive. Returning through nested menus restores the previous screen.
5. Save in one of three manual slots before quitting. **Load journey** restores a saved traveler and their overland state. Entering a local area resumes its own saved position. **Continue journey** resumes the current in-memory journey after returning to the title.

The travel map reads its waypoints from the frontier catalog. Destinations with a `worldMap` path enter that local area; overland waypoints without one explicitly disable local entry. The map art and travel rules read the same route catalog. Road costs are deterministic. Foraging takes 12 hours and restores two supplies, allowing a caravan with no money or supplies to recover. At Mercy Crossing, inventory offers four supplies for five brass.

Gameplay shortcuts: `WASD` movement, `Escape` pause, `C` character, `I` inventory, `J` journal, `M` travel. The existing `Tab` camera-follow and `+/-` zoom controls remain available in the world. Within menus, `Tab`/`Shift+Tab` change focus and `Enter`/`Space` activate focused buttons. Hover or focus a stat marked `[?]` to see its explanation and calculation. `Escape` first dismisses stat help, then returns or cancels a dialog. Primary creation/settings actions remain in the footer.


## Bounded pages and stat help

The character record has four sections: **Overview**, **Abilities**, **Training**, and **Background**. The six ability cards show modifiers and base saving throws, while training lists calculated bonuses and identifies trained skills. Matching background/calling training adds proficiency only once. Equipment access remains in the footer.

Character creation uses five steps: Identity, Calling, Physical, Mental, and Review. Both attribute steps share the same draft and point budget. The standard array sets all six scores. Settings separates Display, Interface, Controls, and Audio; changing tabs preserves unapplied settings. Inventory separates Equipment from Supplies & belongings. The journal separates Objectives, Travel ledger, and Frontier lore, and displays one complete history entry at a time with newer/older controls.

`TabbedMenuPage` provides a persistent tab strip and Previous/Next controls; these screens do not use a scrolling content pane. `MenuHelp` owns one tooltip at a time, clamps it within the screen, and dismisses it on page changes, resize, focus loss, or Escape. Tooltip actors do not intercept clicks. `CharacterStats` centralizes skill calculations and the explanations used by character, creation, and inventory pages.

The layout regression harness checks actor/text bounds across every section at 1300x800 and at 900x600 with larger text, including 28-character names and 100 long journal entries. It also checks Next navigation, keyboard tooltips, mouse hover, Escape dismissal, and tooltip bounds. Run after building the JAR, from `assets/`:

```sh
java --class-path ../lwjgl3/build/libs/Drop-1.0.0.jar ../tools/MenuLayoutSmoke.java
```

[Ability cards](character-abilities.png) · [Stat tooltip, small window](stat-tooltip-small.png) · [Paged ledger, small window](journal-ledger-small.png)

## Extension points

- `menus/MenuTheme`: shared skin and styles. Font generation and catalog loading are queued by `SplashScreenAssetLoader`; the render loop never waits for assets. `Main` owns the theme, and its `AssetManager` owns fonts.
- `screens/MenuScreen`: reusable stage, viewport, keyboard focus, and screen lifecycle. Each screen owns its stage; hiding a screen clears input focus without disposing reusable UI resources.
- `screens/GUIs/menus/MenuPage`: consistent layout, controls, error notices, confirmation dialogs, and footer actions. Feature pages keep their own draft/view state. The existing `MainScreenGUI` and `BattleScreenGUI` remain in their matching package.
- `menus/MenuRegistry` and `MenuId`: explicit page registration and named routes. To add a page, implement `MenuPage`, add its route and registration, then expose navigation from the appropriate screen.
- `ScreenManager`: push/pop history for nested menus; root transitions clear history. Hidden gameplay receives no frame updates. Confirmations use modal Scene2D dialogs with focus restored on dismissal.
- `menus/MenuController`: injected session/navigation boundary. `BattleScreen` captures and restores the actual player position, independently of the camera-follow target, and selects a world scene through the injected scene factory. Leaving gameplay stops movement and stores a position for that area.
- `menus/CharacterRecord`, `Journey`, and `FrontierCatalog`: widget-independent state and provisional tabletop rules. Character creation edits a draft and only replaces the journey on final confirmation. Equipment changes are reflected in the character record and armor defense.
- `menus/JourneyStore`: three validated, versioned manual snapshots in the `corovaner.journeys` libGDX preferences store. Settings use a separate `corovaner.settings` store. Save errors are displayed; incompatible/corrupt slots cannot be loaded and can be explicitly overwritten.
- `assets/data/frontier.json`: origins, archetypes, equipment names, destination descriptions/coordinates, and roads. IDs are persistent save identifiers. The initial character and introductory quest currently use the `caravan`, `gunslinger`, `mercy`, and `pass` IDs; migrate references and old saves if these change.

To support another playable destination, set its `worldMap` path in the catalog and add a connecting road. The catalog loader declares those maps as dependencies so they finish loading before menu entry. The journey stores positions by destination ID; keep IDs stable when changing asset paths.

For future save changes, add a migration at the store boundary before `Journey.validate`, then increment the snapshot version. Do not silently reinterpret old IDs or attribute formulas.

## Current scope

The six-attribute point buy, health, defense, training, equipment, supplies, and travel clock are a playable menu/campaign prototype. Combat resolution, damage, experience progression, dynamic loot, item comparisons, companions, and faction reputation are not implemented by this menu change. Weapon/armor toggles change the saved character record; they do not replace character model meshes. HP is currently the initial maximum, and saving throws use base ability modifiers. The journal combines an arrival objective with real journey history.

Display mode, vsync, larger text, and decorative backgrounds apply and persist. Audio controls are intentionally unavailable while the project has no audio channels. Movement rebinding is not introduced here. Forest Crossroads, Abandoned Quarry, and Forgotten Graveyard are accessible by roads from Blackwire Pass through the same travel UI. Each area retains its own saved player position.

## Verification

`./gradlew build test lwjgl3:jar` compiles and packages both modules and runs the configured tests. Menu tests cover point costs, overspending, invalid routes, insufficient supplies, resource recovery, unique visits, snapshot round trips, damaged saves, catalog validation, nested screen suspension, resize restoration, and single disposal.

The repeatable desktop harness is `tools/MenuSmoke.java`. After building the JAR, run it with `assets/` as the working directory:

```sh
java --class-path ../lwjgl3/build/libs/Drop-1.0.0.jar ../tools/MenuSmoke.java
```

It opens a temporary game window, writes review screenshots, and exits with `MENU_SMOKE_PASSED` on success. It exercised the real `Main` application with isolated preferences under `/tmp`: character creation, route confirmation, local entry, pause, equipment, larger-text settings, save/load, journal, and a 900x600 resize. It also exercised keyboard activation of a confirmation and Escape return navigation. Review captures:

- [Main menu](main-menu.png)
- [Character creation](character-creation.png) and [review](character-review.png)
- [Travel](travel.png) and [small window](travel-small-window.png)
- [Character sheet](character-sheet.png), [inventory](inventory.png), and [journal](journal.png)
- [Pause](pause.png), [settings](settings.png), [larger text](settings-large-text.png), and [saves](saves.png)
- [World HUD](battle-hud.png)
