# Frontier menus

The game opens into a shared Scene2D menu system with a western field-terminal theme: charcoal, brass, parchment typography, contour lines, and a ruined transmission tower. The approved screen reference and reusable generated raster primitives live under `assets/ui-architecture/`. They add blackened-steel frames, enamel controls, fasteners, and surface wear without baking live labels or layouts into images. The three bundled Liberation fonts are covered by `assets/ui/fonts/LICENSE.txt`.

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

- `menus/MenuTheme`: shared skin and styles. Font generation, post-apocalyptic nine-patch textures, and catalog loading are queued by `SplashScreenAssetLoader`; the render loop never waits for assets. `Main` owns the theme, and its `AssetManager` owns fonts and UI textures.
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
