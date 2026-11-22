# Menu UI assets

**Current implementation.** Runtime UI artwork, fonts, cursors, atlas files and editable
art sources live directly in this folder. There are no nested UI asset folders.
Paths used by the game are relative to `assets/` and begin with `ui/Menus/`.

| Files | Purpose |
| --- | --- |
| `01-caravan-painting.png`, `02-hinged-panel.png`, `03-metal-rim.png` | Independent 1920 × 1080 main-menu layers, drawn in this order. |
| `button-enamel.png` | Text-free terminal button; labels remain live Scene2D text. |
| `corovaner-ui.atlas`, `corovaner-ui.png` | Shared runtime atlas containing 33 controls, hardware parts and icons. |
| `Manrope-Regular.ttf`, `Manrope-SemiBold.ttf`, `Manrope-OFL.txt` | Current font sources and license. |
| `Splash.png`, `arrowCursor3.png` | Startup artwork and active cursor. |
| `MainModal.png`, `arrowCursor.png`, `frontier-*.ttf` | Retained earlier artwork/fonts; not queued by the current menu. |
| `manifest.json`, `layout.json` | Component metadata and terminal placement reference; file paths are relative to this folder. |
| Individual component PNGs, `.9.png` files and SVG icons | Editable inputs and standalone exports for the shared atlas. |
| `*-native.png`, `hardware.png`, `surfaces.png`, `generation-prompts.json`, `prompts.json` | Preserved generation sources and provenance. |
| `lamp-hover.png` | Earlier standalone lamp export; the runtime uses the atlas's matching lamp-on/lamp-off pair. |

`SplashScreenAssetLoader` calls `MenuAssets.queue()` for the UI and separately
queues the character model and world catalog. `MenuAssets.ROOT` defines the shared
folder, and `MenuAssets.font(id)` identifies a generated font size in AssetManager;
those IDs are not additional TTF files. `MenuTheme` builds styles from loaded assets.
The central AssetManager owns shared textures and fonts.

The menu remains centered on its reference canvas with uniform scaling and painted
margins. Labels use Manrope, amber on dark surfaces and espresso on enamel. Lamps
stay visible at rest and switch on for hover/focus. This folder organization changes
no creative requirements or gameplay decisions.

See [runtime behavior and checks](../../../docs/menus/README.md#terminal-main-menu),
[font provenance](../../../docs/menus/manrope.md),
[component kit notes](../../../docs/menus/ui-kit/README.txt), and
[optional integration example](../../../docs/menus/ui-kit/INTEGRATION.txt).
The Java example lives with the kit documentation. Screen concepts, component
sheets and the offline preview live in [UI design concepts](../../../docs/menus/concepts/README.md).
