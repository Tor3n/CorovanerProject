COROVANER — REUSABLE COFFEE-ENAMEL UI KIT v1.0

33 reusable, text-free sprites derived from the revised UI concepts:
9 stretchable surfaces; 12 independent hardware parts; 12 semantic icons.

START HERE
Open preview.html in a browser. It is self-contained and works offline.
Hover/focus/press the buttons, operate the toggle and move the gauge slider.
component-sheet.png is a quick visual inventory of the exported pieces.

PACKAGE
sprites/     33 transparent PNGs, ready to compose independently.
icons/       12 editable SVG originals plus matching PNGs in sprites/.
ninepatch/   9 .9.png files with stretch and content-padding marker borders.
atlas/       A 2048 x 1024 PNG page and LibGDX .atlas descriptor.
manifest.json  Region names, dimensions, split/padding values and pivots.
source/      Original generated sprite sheets and exact generation prompts.
docs/        Integration guide, Java helper and validation results.

DESIGN CONTRACT
Keep text and icons as separate actors. No screen backgrounds, portraits,
sample values or captions are baked into the reusable controls.
Primary buttons use the up, over and down surfaces. The lamp is a separate
fixed-size overlay, hidden at rest, visible on hover or keyboard focus, and
hidden when disabled. An optional lamp-off sprite exists for status widgets;
do not display it on idle action buttons if following the current concept.
Reserve lamp space in every state so the caption does not move on hover.
The disabled state is a runtime tint; it is not a separate sprite.

SCALING
PNG surfaces are authored at 2x. The Java helper scales nine-patch borders by
0.5, making a primary button 64 UI units high. Stretch capsule buttons
horizontally; do not enlarge their height independently. Larger UI scales
should uniformly scale the entire widget. Frames and rectangular panels can
stretch on both axes, with all corner fasteners kept outside the center.
Never nine-slice the lamps, dials, hinges, screws or needles. Scale them
uniformly, or retain their recommended sizes in manifest.json.
Keep textured-panel stretching moderate; very large panels may benefit from
a separate tiled fill in a later production pass. No seamless tiles claimed.

COMPOSITION
Main menu: panel-steel + panel-enamel + enamel buttons + hover lamp + grille.
Dialog: panel-steel or frame-steel + live title/body + separate action row.
Inventory: panel-steel + slot-recessed + live item icons/labels + scroll-thumb.
Attribute counter: tab-enamel + slot-recessed + live numeric labels.
Toggle: toggle-socket + movable toggle-handle + live label.
Gauge: gauge-face + gauge-needle rotated about its documented pivot.
Scrollbar: a narrow slot-recessed track + scroll-thumb + arrow icons.

The surfaces and hardware were redrawn as clean, text-free components using
built-in image generation, then extracted and normalized for reuse. They
match the concept direction but are not lossless cuts from the screenshots.
Semantic icons were authored as SVG. The kit is an opt-in study, not a replacement for the current runtime theme.
See ../../../docs/design/ui-art-direction.md for current preference and proposal
status. The nine-patch exports are optional; uniformly scaled fixed-proportion
modules remain under consideration. The pending Canva draft is separate.
