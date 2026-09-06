# Post-apocalyptic UI primitives

These text-free raster primitives implement the frontier field-terminal direction used by `MenuTheme`:

- `panel-frame.png`: scalable container and dialog frame.
- `button-dark.png`: default, hover, pressed, disabled, text-field, and select-box base.
- `button-enamel.png`: primary action and selected-tab base.

The source artwork was generated for this project, then trimmed, downscaled, and stripped of metadata for runtime use. `MenuTheme` loads the textures through the central `AssetManager`, applies deterministic state tints, and uses fixed nine-patch splits so text and layout remain responsive.

Keep labels out of these images. Scene2D supplies live text, focus, disabled, hover, pressed, and checked states.
