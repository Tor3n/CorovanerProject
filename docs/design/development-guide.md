# Development guide and open decisions

**Coworker guidance and proposals.** The creative source
is [vision.md](vision.md); use [the index](README.md) for authority and status rules.

## Before implementing a task

Read the vision and the relevant design page. Identify the player experience the
task supports and distinguish requirements from suggested solutions. Explain any
conflict with the guide or current architecture before making a substantial change.
Do not silently settle an open creative choice through an implementation shortcut.
Routine reversible work can continue within the established direction.

For a substantial feature, keep a short task note or PR explanation with:

- The relevant design pillar and intended player benefit.
- Requirements being fulfilled and any additional assumptions or proposals.
- How the work affects map prominence, exploration, persistent consequences, and the reveal, where relevant.
- The evidence used to judge the result: a playable scenario, useful capture, performance measurement, or targeted check.
- Remaining decisions or limitations, without presenting future work as implemented.

## Current implementation facts

Baseline inspected: `main` at `58a32a3e65dfa4a66192b90e52bfe71b1b77ba2b`,
2026-10-21. This is an engineering inventory for planning changes. The creator
rejected the existing map material and travel interface as design references;
their appearance, layout, and interactions must not guide the future creative
target. Technical reuse should serve the vision, without setting its quality bar.

| Area | Observed baseline | Design implication |
| --- | --- | --- |
| Platform | Java 11, libGDX, desktop LWJGL3, Ashley ECS. | Work within the existing project unless a separate migration is agreed. |
| Local world rendering | One orthographic camera and depth buffer; ground meshes, anchored cutouts, elevation risers, static structure meshes, and 3D actors. Fixed 45-degree azimuth / 30-degree elevation. | Existing engineering evidence. The later creator decision requires a full-display 3D main map; the prototype's art and camera framing do not define its final presentation. |
| Travel | A route catalog and travel screen connect local destinations; supplies and campaign time are tracked. | Useful exploration scaffolding. The country-scale visual centerpiece remains a design goal. |
| Persistence | Journey snapshots retain character/travel state and per-area player positions. | Extend existing persistence deliberately; do not assume faction history or the genre transition is already saved. |
| UI | Shared Scene2D menus; a separate UI-theme branch contains stretchable art and reference material. | Preserve real text, input, scaling, and readability while refining visual materials. |
| Factions and strategy | Existing menu documentation lists faction reputation outside its implemented scope. | The envisioned 4X phase and its transition must be designed and implemented explicitly. |

Sources: [repository engineering rules](../../AGENTS.md),
[world architecture](../characters/architecture.md), [local maps](../maps/README.md),
[menus and journey state](../menus/README.md), and [reference index](references.md).

The existing architecture forbids reintroducing a separate 2D map renderer,
per-character cameras, or depth clears between world objects. Keep those rules
for production changes. Comparing a different future art/rendering approach is a
separate prototype and migration discussion, not permission to break the current
renderer. A concept document does not change runtime behavior.

## Proposed next work, in order

1. **Prove the map's appeal in a small representative region.** Show a road, a memorable destination, contrasting terrain, and a simple traveler at the intended camera angle. Include one meaningful detour and enough interaction to test curiosity. Judge both the view and the act of exploring it.
2. **Prove the full-display 3D map presentation.** Use the same region and art intent when comparing asset, lighting and rendering approaches within the chosen 3D direction. Evaluate camera framing at different aspect ratios separately from the illustrated menu canvas. Record authoring effort, visual clarity, frame time, memory use, and visible popping. Choose target hardware and budgets before calling a result production-ready.
3. **Prove one consequence through the transition.** In a deliberately small prototype, carry one meaningful RPG choice and one faction relationship into a sudden strategic scenario. Use temporary UI and content; test whether the connection is understood without spoiling the transition beforehand.
4. **Expand content after those proofs.** Grow regions, factions, and quest networks once the visual center and persistent progression work together. Keep battle and character scope consistent with their lower visual-detail priority.

These steps are a proposal, not a release plan or authorization to start an engine
rewrite. Country scale describes the intended experience; exact dimensions,
travel speed, content density, and loading strategy require evidence.

## Decision register

| ID | Question | Current status / next useful evidence |
| --- | --- | --- |
| D-01 | Final map presentation: 2D, 3D, or mixed? | Creator decision, 2026-10-25: the main map is 3D. This resolves the earlier open choice; projection, asset technique and rendering budgets remain open. See the creator's wording in [display rules](ui-art-direction.md), also reflected in [the main guide](README.md) and [atmosphere](atmosphere.md). |
| D-02 | What does fixed camera permit? | Fixed camera required; final pan, follow, zoom range, and projection unresolved. Existing local view is an implementation reference. |
| D-03 | How is the small-country scale represented? | Large country-scale map required; scale compression, travel pace, geography size, and the role of local scenes unresolved. |
| D-04 | What triggers the genre change, and when? | Sudden, unexpected RPG-to-4X change required. Trigger, stage length, and narrative cause open. |
| D-05 | What happens to direct character control afterward? | Character remains central to the vision; degree of direct travel, questing, or delegation in stage two open. |
| D-06 | How does the strategic game advance? | Turn-based, real-time, or another model; economic rules, victory, setbacks, and faction behavior open. |
| D-07 | How do leadership identities differ? | Ruler, caravan master, and faith leader are creator-provided possibilities; exclusivity, progression, and alternatives open. |
| D-08 | What are the production constraints? | Target hardware, frame-time and memory budgets, art pipeline, staffing, and content volume open. Do not invent numerical targets as requirements. |
| D-09 | How does the illustrated UI adapt to display size? | Creator decision, 2026-10-25: target 1920 x 1080, keep the graphical menu centered and its picture rectangular, paint extra horizontal space, and uniformly scale down on smaller displays. The terminal main menu now implements these rules. Implementation choices, 2026-10-25: uniformly enlarge at 1440p/4K and paint top/bottom surplus in taller windows; these are not additional quoted creator requirements. Minimum readable size and a user-controlled UI scale remain open. See [display rules and the creator's wording](ui-art-direction.md), [runtime checks](../menus/README.md#terminal-main-menu), [atmosphere](atmosphere.md), [references](references.md) and the [kit notes](../../assets/ui-architecture/coffee-enamel/README.txt). |
| D-10 | Does the main map use the illustrated menu's display bounds? | Creator decision, 2026-10-25: the 3D main map occupies the whole monitor regardless of resolution. Its viewport fills the available game window independently of the centered menu composition and painted margins. Exact camera framing at different aspect ratios remains open. See [display rules](ui-art-direction.md) and [atmosphere](atmosphere.md). |

When a decision is made, record its date, creator instruction or reviewed evidence,
chosen scope, and affected documents here. Keep unresolved details visibly open.

## Review standard

For art and map changes, inspect the actual gameplay framing and readability with
the HUD. For systems, verify the relevant state and consequences rather than
building tests that repeat implementation details. For rendering, measure the
representative scene on agreed hardware before committing to expensive effects.
Follow repository build and testing rules for code changes. Documentation-only
changes need text, source, and link checks; do not report unrun game tests as passed.
