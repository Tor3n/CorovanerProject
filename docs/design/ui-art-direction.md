# Coffee-enamel UI study and scaling discussion

**Creator preferences, collaborator proposals, and asset facts.** Recorded
2026-10-25 from the UI concept discussion. Read [the main guide](README.md) and
[the vision](vision.md) for authority and scope. Adding these files to a branch
does not approve a final screen layout or adopt a new runtime theme.

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

## Fixed-proportion scaling: under consideration

The creator proposed designing for modern monitors with fixed illustrated UI,
then making it bigger or smaller at other resolutions. This was an exploratory
discussion, not a final resolution or viewport decision.

The collaborator's proposed approach is to assemble fixed-proportion painted
modules, scale each module uniformly, and keep live labels, icons and moving
hardware separate. Reusability does not require stretching every texture.

| Proposal | Rationale / unresolved detail |
| --- | --- |
| A 1920 x 1080 logical design canvas, with production art authored at 2x | A candidate workflow, not a confirmed target or a claim about the current PNG resolution. |
| 1x at 1920 x 1080, 1.33x at 2560 x 1440, 2x at 3840 x 2160 | Preserves proportions at the same aspect ratio. Physical display size still affects readability. |
| Anchor modules at edges or center; allow the map to use extra ultrawide space | A candidate aspect-ratio policy. Letterboxing is another option; exact camera/view behavior remains open. |
| A user-adjustable UI scale | Supports different viewing distances and physical monitor sizes. Final supported range needs layout evidence. |
| Restrict stretching to content-driven elements when useful | Tooltips and variable-length lists may use it; illustrated panels need not. |

The included nine-patch kit predates that discussion and is preserved as an
optional experiment. Its existence does not mandate a stretchable UI. Before
runtime adoption, compare uniform scaling and optional nine-patch use at actual
play size, with readable text and the intended map visible.

## Included work and exact scope

| Work | Location | Status |
| --- | --- | --- |
| 33 component sprites, 12 SVG icon originals, 9 nine-patch inputs, one atlas | [Coffee-enamel kit](../../assets/ui-architecture/coffee-enamel/README.txt) | Text-free component study; not wired into game screens. |
| Visual component inventory | [Component sheet](../../assets/ui-architecture/coffee-enamel/component-sheet.png) | Shows the exported assets and example button states. |
| Offline component preview | [HTML sampler](../../assets/ui-architecture/coffee-enamel/preview.html) | Embedded assets and sample interactions; see validation limits. |
| Main menu, character screen, travel screen | [Concept scope](reference/coffee-enamel/README.md) | Material and control-variety studies; layouts and sample content are proposals. |
| Java helper and validation notes | [Integration](../../assets/ui-architecture/coffee-enamel/docs/INTEGRATION.txt), [validation](../../assets/ui-architecture/coffee-enamel/docs/VALIDATION.txt) | Example compiled against LibGDX 1.13.1 / Java 11; not an in-game smoke test. |

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

These are proposed checks for future implementation, not completed game tests:

- Keep artwork proportions and circular lamps intact at supported UI scales.
- Keep text live, legible and able to accommodate localization.
- Reserve lamp space so labels do not move on hover; retain keyboard focus feedback.
- Compare wide and tall displays without covering the map's important landmarks.
- Use existing asset ownership, asynchronous loading and screen-space UI rules.
- Distinguish a compiled helper or parsed atlas from a tested playable screen.

Technical background: [LibGDX viewports](https://libgdx.com/wiki/graphics/viewports),
[texture atlas / nine-patches](https://libgdx.com/wiki/tools/texture-packer).
