# Coffee-enamel UI study and display rules

**Creator preferences, collaborator proposals, and asset facts.** Recorded
2026-10-25 from the UI concept discussion. Read [the main guide](README.md) and
[the vision](vision.md) for authority and scope. The main-menu implementation
is recorded below; the other screen studies remain scoped art proposals.

## Latest creator preferences

- The earlier buttons looked too sci-fi. Use worn metal and vintage hardware.
- Wooden planks were then rejected as too cowboy. This supersedes the earlier
  plank direction in the design guide.
- The creator preferred the coffee-colored metal buttons in
  [the existing modal reference](../../assets/MainModal.png). In this study,
  those become rounded coffee-cream enamel faces with aged metal rims.
- Increase the variety of UI forms: recessed displays, counters, utility keys,
  dials, slots, vents, hinges and distinct panel shapes. Keep their functions clear.
- Button lamps should be round. A hover example shows one lit lamp, while other
  action buttons have none. Keep the lamp separate from the button surface.

## Creator decision: 1920 x 1080, centered menu and painted margins

Clarified by the creator on 2026-10-25:

> the idea is that we aim for 1920 x 1080 and what goes in terms of width we just fill with some paint. Everything else that is smaller we just scale down. And the main menu picture just stays rectangular. And we can keep the graphical menu centered.

The resulting design requirements are:

- Compose the UI for a **1920 x 1080 reference canvas**.
- Keep the graphical main menu centered, with its relative placement and
  illustrated proportions intact.
- Fill additional horizontal space with painted decoration. Extra width does
  not require wider buttons, a stretched menu or rearranged controls.
- On smaller displays, uniformly scale the composition down until it fits
  both dimensions. Artwork, controls and their interactive bounds stay aligned.
- Keep the main-menu picture rectangular and preserve its aspect ratio. The
  side painting is separate decoration, not a reason to stretch or crop it.

Concrete consequences of these rules:

| Display | Menu composition | Surrounding space |
| --- | --- | --- |
| 1920 x 1080 | Reference size, centered. | No additional side area. |
| 2560 x 1080 | Same 1920 x 1080 composition, centered. | 320 pixels of painted decoration on each side. |
| 1280 x 720 | Entire composition at 2/3 scale. | No additional side area. |

For a smaller drawable area of width W and height H, the fitting scale is
`min(W / 1920, H / 1080)`. Center the resulting rectangle in that area. This is
a derived fitting rule. The main-menu implementation now uses this reference
canvas, with the rectangular picture behind the separate panel and perimeter rim.

This clarification supersedes the earlier open scaling proposal and the suggestion
to use extra horizontal space for a different menu layout. It does not settle
the gameplay map's camera or visible world area.

**Implementation choice, 2026-10-25:** the first screen uses uniform enlargement
as proposed during the discussion: 1.33x at 2560 x 1440 and 2x at 3840 x 2160,
with painted margins for surplus width or height. This is the current engineering
choice for the requested screen, rather than an additional quoted creator rule.
The minimum readable size and an independent user scale slider remain open.
LibGDX handles the backend's logical-window/framebuffer distinction; platform-specific
display-density behavior still needs verification on target systems.

Keep labels, icons and moving hardware separate from painted surfaces. Reusable
elements can be assembled at fixed sizes within the reference composition. The
included nine-patch kit predates this decision and remains an optional authoring
experiment; it does not require stretching illustrated elements to fit a display.
Higher-resolution production masters remain a proposal, not a statement about
the resolution of the current exported PNGs.

## Creator decision: full-display 3D main map

The creator clarified the scope on 2026-10-25:

> yes, the main map would be 3d and occupy the whole of the monitor, no matter the resolution.

- The main map is **3D**. This resolves the earlier open choice of map presentation.
- Its view fills the available game window, or the whole monitor in fullscreen,
  at every supported resolution and aspect ratio.
- The centered 1920 x 1080 menu composition and painted side margins do not
  constrain the map's viewport. In gameplay, the map extends across the display
  behind the UI overlays.
- Keep map projection and screen-space UI sizing independent. A resolution or
  aspect-ratio change must preserve world proportions.

Full-display coverage does not decide the camera projection, zoom, or how much
additional landscape a wider aspect ratio reveals. Those remain camera-design
questions. Keeping the vertical view consistent and revealing more landscape at
the sides is a collaborator proposal for evaluation. This display requirement
also does not fix internal rendering resolution or a performance budget.

These map requirements remain separate from the implemented main menu. The terminal
work does not modify the world renderer or implement the final country map.

## Included work and exact scope

| Work | Location | Status |
| --- | --- | --- |
| 33 component sprites, 12 SVG icon originals, 9 nine-patch inputs, one atlas | [Coffee-enamel kit](../../assets/ui-architecture/coffee-enamel/README.txt) | Text-free component study; not wired into game screens. |
| Visual component inventory | [Component sheet](../../assets/ui-architecture/coffee-enamel/component-sheet.png) | Shows the exported assets and example button states. |
| Offline component preview | [HTML sampler](../../assets/ui-architecture/coffee-enamel/preview.html) | Embedded assets and sample interactions; see validation limits. |
| Main menu, character screen, travel screen | [Concept scope](reference/coffee-enamel/README.md) | Material and control-variety studies; layouts and sample content are proposals. |
| Java helper and validation notes | [Integration](../../assets/ui-architecture/coffee-enamel/docs/INTEGRATION.txt), [validation](../../assets/ui-architecture/coffee-enamel/docs/VALIDATION.txt) | Example compiled against LibGDX 1.13.1 / Java 11; not an in-game smoke test. |
| Separate terminal paintings, reusable button and hover lamp | [1920 x 1080 terminal pack](../../assets/ui-architecture/terminal-layers-v1/README.md) | Used by the implemented main menu, with live text and hover/focus lamps. The tagline and riveted nameplate remain blank. [Runtime behavior and checks](../menus/README.md#terminal-main-menu). |

The concept PNGs are 1672 x 941 and are not 4K production masters. The component
PNGs have individually documented sizes in the kit manifest. They are clean
redraws of the visual direction, not lossless screenshot slices. Labels and
sample values are baked into the concept screens but absent from reusable sprites.

## Relationship to the map vision

The map remains the visual centerpiece. The creator rejected the old map material
and travel interface as design baselines. The included travel composition was
developed during the material iteration using an earlier layout; it is retained
only to show the coffee-enamel controls in context. Its terrain, layout, route
graph, interactions and sample mechanics are not a baseline or quality target.
The final country map and travel experience must be developed from the vision.

Heavy frames and hardware can compete with map visibility. Final panel sizes,
decoration density and placement remain review questions; this study must not
silently settle them. Generated numbers, names and item details are illustrative.

## Future acceptance checks

The terminal-specific scale, centering, input and label checks are covered by the
[runtime smoke harness](../menus/README.md#runtime-verification). The following
remain ongoing acceptance criteria for the wider UI and future map implementation:

- Keep artwork proportions and circular lamps intact at supported UI scales.
- Compare the 1920 x 1080 reference, 2560 x 1080 painted side margins and
  1280 x 720 downscaled menu; keep the rectangular picture and menu centered.
- Check hit areas after scaling and resizing; decorative margins contain no
  required controls.
- Check that the 3D main map covers the full display across aspect ratios,
  independently of the menu's painted margins and UI scale.
- Keep text live, legible and able to accommodate localization.
- Reserve lamp space so labels do not move on hover; retain keyboard focus feedback.
- Compare wide and tall displays without covering the map's important landmarks.
- Use existing asset ownership, asynchronous loading and screen-space UI rules.
- Distinguish a compiled helper or parsed atlas from a tested playable screen.

Technical background: [LibGDX viewports](https://libgdx.com/wiki/graphics/viewports),
[texture atlas / nine-patches](https://libgdx.com/wiki/tools/texture-packer).
