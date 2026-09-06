# Local maps and battle controls

Three editable 64 × 64 isometric maps, using the same 64 × 32 ground tiles and
six external tilesets as `mountinPass.tmx`. No new textures or dependencies.

| Location | Layout | Map | Preview |
| --- | --- | --- | --- |
| Forest Crossroads | Four road approaches, wooded clearings, and a side trail to a caravan camp with tents, supplies, and a fire pit. | [forestCrossroads.tmx](../../assets/maps/forestCrossroads.tmx) | [Screenshot](forestCrossroads.png) |
| Abandoned Quarry | A raised western shelf, exposed stone seams, two routes through the quarry floor, and an abandoned work yard. | [abandonedQuarry.tmx](../../assets/maps/abandonedQuarry.tmx) | [Screenshot](abandonedQuarry.png) |
| Forgotten Graveyard | Broken fences, burial rows, a central aisle, a memorial clearing, and an outer woodland trail. | [forgottenGraveyard.tmx](../../assets/maps/forgottenGraveyard.tmx) | [Screenshot](forgottenGraveyard.png) |

The remaining frontier destinations also have their own local maps:

| Destination | Layout | Map | In-game capture |
| --- | --- | --- | --- |
| Mercy Crossing | Fenced caravan trading camp, market shelters, stores and a braced water tower. | [mercyCrossing.tmx](../../assets/maps/mercyCrossing.tmx) | [World](in-game-mercy.png) |
| Saint's Relay | Communications mast, service shelter, supply yard and rocky western boundary. | [saintsRelay.tmx](../../assets/maps/saintsRelay.tmx) | [World](in-game-relay.png) |
| The Salt Graves | Pale salt terrain, dark burial crosses, intersecting paths and a memorial. | [saltGraves.tmx](../../assets/maps/saltGraves.tmx) | [World](in-game-salt.png) |

Start a journey to enter Mercy Crossing immediately. The existing roads from
Blackwire Pass lead to Saint's Relay and the Salt Graves. Travel controls and
road costs are unchanged. Every destination now offers **Enter local area**.

On the battle map, **left-click a scout to select it, then left-click the ground
to move**. A green ring identifies selection; an amber ring marks the destination.
Routes go around blocked terrain/structures and do not cross cliffs or cut
through diagonal corners. An unreachable click leaves the current route intact.
WASD overrides the selected scout's route, Tab cycles selection, and +/- zooms.
HUD clicks never issue world commands. Leaving gameplay cancels movement.
The first scout remains the saved campaign player; additional test scouts reset
when an area session is recreated.

Open any `.tmx` directly in Tiled. The screenshots show the complete maps rendered
by libGDX's `IsometricTiledMapRenderer`.

Each map has a matching `.world.json` descriptor with three spawns in Tiled's
top-down column/row coordinates. Ground and cutout layers carry the properties
required by `WorldMapImporter`. Trees, cliffs, fences, tents, and gravestones
occupy solid cells; smaller scattered details are decorative. The quarry shelf
is an inaccessible backdrop at elevation 2, with the routes on the floor below.
Camp supplies, graves, and road exits are scenery/layout features; they have no
loot, encounter, or map-transition triggers attached.

All three locations are integrated into the game's travel map. Begin a journey
at Mercy Crossing, travel to Blackwire Pass, then select one of the new locations,
confirm travel, and choose **Enter local area**. Press **M / Travel** to leave.
Each new location has a return road to Blackwire Pass.

| Road from Blackwire Pass | Hours | Supplies (one way) | In-game capture |
| --- | --- | --- | --- |
| Forest Crossroads | 4 | 1 | [World](in-game-forest.png) |
| Abandoned Quarry | 6 | 2 | [World](in-game-quarry.png) |
| Forgotten Graveyard | 5 | 1 | [World](in-game-graveyard.png) |

`assets/data/frontier.json` associates stable destination IDs with `worldMap`
asset paths. The catalog loader declares these WorldMaps as dependencies, so
startup loads all seven maps asynchronously through `WorldMapLoader`, including
shared tileset textures. Adding another location requires content and route
entries, with no switch statement or screen-specific loader.

`BattleScreen` creates the selected area's `WorldScene` through an injected
`WorldSceneFactory`. Switching areas disposes the old renderer and clears its
ECS session; the AssetManager retains shared maps and character models. Every
area uses the same single orthographic camera/rendering architecture and shows
its own name in the HUD.

Leaving gameplay records the player's position by destination ID in the journey.
Re-entry and save/load restore that area's position, while a new journey creates
a fresh session. Legacy saves migrate their single position to Blackwire Pass,
the only local scene available when they were written. Positions made unwalkable
by a later map edit fall back to the area's authored spawn. NPCs currently reset
to their authored spawns when a new area session is created.

To regenerate the authored layouts from the repository root:

```sh
python3 tools/generate_locations.py
```

This uses only the Python standard library and fixed seeds. It overwrites these
three maps and descriptors, so save hand-edited variants under different names.
It does not regenerate the screenshots.

Validation: `./gradlew test build` passes, including destination/dependency,
route reachability, independent area positions, save round trips and legacy-save
migration checks. The repeatable desktop integration harness runs from `assets/`
after building the JAR:

```sh
java --class-path ../lwjgl3/build/libs/Drop-1.0.0.jar ../tools/MapSmoke.java
```

It uses isolated preferences under `/tmp`, drives the actual travel controls,
enters all maps, checks terrain/actor creation and movement, exercises pause,
save/load, revisits, new journeys, invalid-position fallback and window resize,
and verifies replaced ECS sessions are cleared. It exits with
`MAP_SMOKE_PASSED` and refreshes the in-game/travel captures in this directory.
The full-map previews above remain Tiled-renderer authoring references; the
`in-game-*.png` captures use the unified world pipeline.

Regenerate only the three destination-specific maps and their structure data:

```sh
python3 tools/generate_frontier_locations.py
```

This imports the common location generator without regenerating the original
three wilderness maps. Structures in `.world.json` use Tiled `column`/`row`
anchors, a blocked footprint in world units, and local box/cylinder parts with
position, size, color and optional pitch/yaw/roll. `WorldStructures` validates
and blocks footprints during asynchronous preparation; mesh creation/upload runs
on the GL thread with the rest of the map. Meshes share the world renderer and
map ownership. Ground layers can specify `worldTint` and `worldBrightness`.
These are static prototype landmarks, without building interiors or interactions.

Mouse controls and the destination maps have a repeatable desktop smoke test
(run from `assets/` after building the JAR):

```sh
java --class-path ../lwjgl3/build/libs/Drop-1.0.0.jar ../tools/BattleInputSmoke.java
```

It uses isolated preferences, clicks through the actual world input multiplexer,
verifies selection and arrival of the commanded scout, checks that other scouts
stay still, blocks HUD clicks, reaches each central landmark, checks menu route
cancellation, then repeats picking after zoom/resize. It writes the captures
above and exits with `BATTLE_INPUT_SMOKE_PASSED`. Unit tests cover wall detours,
waypoint arrival, unreachable targets, cliff avoidance and diagonal clearance.
