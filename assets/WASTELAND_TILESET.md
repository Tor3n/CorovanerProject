# Wasteland tileset

`wasteland_tiles.png` is a 1024 x 1344 isometric atlas designed to match the
layout and proportions of `grassland_tiles.png`. It contains irradiated ground,
blasted cliffs, scrap shelters, ruined buildings, bunker entrances, debris,
dead vegetation, and wasteland trees.

The `wasteland_*.png` sheets are atlas slices matching the dimensions of the
existing Tiled assets. Their definitions live in `assets/maps` and can replace
the original tilesets without changing tile GIDs or map geometry.

For the Java background test, load `wasteland_tiles.png` instead of
`grassland_tiles.png`. Ground tiles remain in the first two 64-pixel-high rows,
with 15 isometric 64 x 32 variants per row.

The original tileset remains unchanged.
