# Wasteland tileset

`wasteland_tiles.png` is a 1024 x 1344 isometric atlas designed to match the
layout and proportions of `grassland_tiles.png`. It contains irradiated ground,
blasted cliffs, scrap shelters, ruined buildings, bunker entrances, debris,
dead vegetation, and wasteland trees.

The `wasteland_*.png` sheets are atlas slices matching the dimensions of the
existing Tiled assets. Their definitions live in `assets/maps` and can replace
the original tilesets without changing tile GIDs or map geometry.

For the Java background test, load `wasteland_tiles.png` instead of
`grassland_tiles.png`. The sliced ground sheet contains two rows of 15 isometric 64 x 32 tiles.

The original tileset remains unchanged.

## World integration and analysis

The six sheet definitions retain the existing tile sizes, counts and first GIDs:

| Sheet | Tile size | GIDs | World role / anchor |
| --- | --- | --- | --- |
| `wasteland_background` | 64 × 32 | 1–30 | Ground diamonds |
| `wasteland_mountain` | 64 × 96 | 31–62 | Solid cutouts; anchor (32, 16) |
| `wasteland_items1` | 64 × 64 | 63–94 | Props/details; anchor (32, 16) |
| `wasteland_items2` | 64 × 96 | 95–110 | Props/details; anchor (32, 16) |
| `wasteland_trees1` | 126 × 126 | 111–114 | Solid trees; anchor (63, 6) |
| `wasteland_trees2` | 128 × 190 | 115–118 | Solid trees; anchor (64, 8) |

The new artwork replaces green vegetation and old stone/wood props with dry
vegetation, scrap shelters, metal fencing, barrels, debris and ruined masonry.
Compatibility is geometric: matching GIDs do **not** always represent matching
ground materials. In particular, old grass variants can become concrete or
hazard stripes. A direct random palette swap creates a patchwork of warning
markings and toxic pools across otherwise safe ground.

Mercy Crossing therefore uses a curated ground palette: earth (GIDs 2, 9, 13)
outside the camp, worn paving (18, 19, 22) on the existing roads/court, worn edge
tiles (20), and a short hazard stripe (6) across the entrance. Trees and props
use their corresponding replacement sheets. Prop placement, blocked cells,
structure footprints, player spawns, travel links and saved world coordinates
remain unchanged. The generator preserves these choices on regeneration.

The TSX files now carry the same world anchor/collision properties as their
original counterparts. Layer-level solid flags still distinguish decorative
items from obstacles. The original PNG pixels and the new PNG pixels are
unchanged; this integration edits Tiled references, metadata and ground choices.

[Mercy overview](maps/mercyCrossing.png) · [Mercy detail](maps/mercyCrossing-detail.png)
· [All map screenshots](maps/README.md)
