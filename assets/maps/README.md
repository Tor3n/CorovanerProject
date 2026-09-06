# Map screenshots

Each `.tmx` has a matching full-map screenshot below, rendered at 2400 × 1600
through the game's `WorldMapLoader` and single-camera `WorldRenderer`. These
captures include terrain, cutout props, static structures and the three test
scouts. They are reference images, not textures used to render the battle map.

| Location | Tiled map | Screenshot |
| --- | --- | --- |
| Mercy Crossing | [mercyCrossing.tmx](mercyCrossing.tmx) | [Overview](mercyCrossing.png) · [Detail](mercyCrossing-detail.png) |
| Blackwire Pass | [mountinPass.tmx](mountinPass.tmx) | [Overview](mountinPass.png) |
| Forest Crossroads | [forestCrossroads.tmx](forestCrossroads.tmx) | [Overview](forestCrossroads.png) |
| Abandoned Quarry | [abandonedQuarry.tmx](abandonedQuarry.tmx) | [Overview](abandonedQuarry.png) |
| Forgotten Graveyard | [forgottenGraveyard.tmx](forgottenGraveyard.tmx) | [Overview](forgottenGraveyard.png) |
| Saint's Relay | [saintsRelay.tmx](saintsRelay.tmx) | [Overview](saintsRelay.png) |
| The Salt Graves | [saltGraves.tmx](saltGraves.tmx) | [Overview](saltGraves.png) |
| Wasteland Pass tileset sample | [wastelandPass.tmx](wastelandPass.tmx) | [Overview](wastelandPass.png) |

Mercy Crossing uses the new wasteland sheets. The supplied Wasteland Pass sample
also has world layer metadata and a `.world.json` descriptor so the same loader
can render it; it remains a standalone sample, outside the travel catalog.

To regenerate the screenshots, build the desktop JAR, then run from `assets/`:

```sh
java --class-path ../lwjgl3/build/libs/Drop-1.0.0.jar ../tools/MapScreenshots.java
```

The tool opens a temporary GL window, loads every descriptor asynchronously,
renders full-map overviews into a framebuffer, checks spawn clearance and Mercy's
main routes, writes the images beside the maps, and exits. It uses an overview
framing of the existing camera projection; normal gameplay camera settings are
unchanged. Refresh screenshots after editing layouts, structures or tilesets.
