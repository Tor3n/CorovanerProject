# Element set

These text-free primitives implement the field-terminal direction captured by
`../reference/main-menu.jpg`.

| File | Contract | Scene2D use |
| --- | --- | --- |
| `screen-frame.png` | 9-slice, 22 px on every edge; transparent center | Full menu-screen chassis overlay |
| `panel-dark.png` | 9-slice, 22 px on every edge | Content containers and dialogs |
| `control-dark.png` | 9-slice, 18 px on every edge | Buttons, fields, lists, and selectors |
| `control-enamel.png` | 9-slice, 18 px on every edge | Primary actions and selected tabs |
| `indicator-amber.png` | Fixed 64 x 64; never stretch | Optional status/decorative overlay |

The generated sources were trimmed, downscaled, and stripped of metadata for runtime use.
`MenuTheme` loads stretchable textures through the central `AssetManager` and applies
deterministic tints for focus, hover, pressed, checked, primary, and disabled states.

Keep labels, icons, indicators, vents, and other fixed hardware out of nine-slice centers.
Scene2D supplies live text and interaction state. Decorative fixed-size elements belong in
their own actors so they do not deform when a panel changes size.
