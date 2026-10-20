# Development guide and open decisions

**Coworker guidance and proposals.** The creative source
is [vision.md](vision.md); use [the index](README.md) for authority and status rules.

## Before implementing a task

Read the vision and the relevant design page. Identify the player experience the
task supports and distinguish requirements from suggested solutions. Explain any
conflict with the guide or current architecture before making a substantial change.
Do not silently settle an open creative choice through an implementation shortcut.
Routine reversible work can continue within the established direction.

For setting-related tasks, also read [world.md](world.md). Preserve the distant,
unexplained End-day; the absence of magic and mutants; the mainly agricultural,
politically fragmented world; and its uneven technology, robots, varied faiths,
and caravan trade. Separate facts about the wider world from the playable region
and from mechanics that have actually been approved.

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
| Local world rendering | One orthographic camera and depth buffer; ground meshes, anchored cutouts, elevation risers, static structure meshes, and 3D actors. Fixed 45-degree azimuth / 30-degree elevation. | A mixed world pipeline already exists; it is not proof that the final country-map presentation is decided. |
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

1. **Prove the map's appeal in a small representative region.** Show a road, a memorable destination, contrasting terrain, and a simple traveler at the intended camera angle. A proposed setting sample could pair working farmland and a rural caravan with distant industrial or city ruins. Include one meaningful detour and enough interaction to test curiosity. Judge both the view and the act of exploring it; no single sample must contain every technology or belief.
2. **Compare presentation approaches only where needed.** Use the same region, framing, and art intent when comparing the current mixed pipeline with a richer 3D or painted approach. Record authoring effort, visual clarity, frame time, memory use, and visible popping. Choose target hardware and budgets before calling a result production-ready.
3. **Prove one consequence through the transition.** In a deliberately small prototype, carry one meaningful RPG choice and one faction relationship into a sudden strategic scenario. Use temporary UI and content; test whether the connection is understood without spoiling the transition beforehand.
4. **Expand content after those proofs.** Grow regions, factions, and quest networks once the visual center and persistent progression work together. Keep battle and character scope consistent with their lower visual-detail priority.

These steps are a proposal, not a release plan or authorization to start an engine
rewrite. Country scale describes the intended experience; exact dimensions,
travel speed, content density, and loading strategy require evidence.

## Open decisions

| ID | Question | Current status / next useful evidence |
| --- | --- | --- |
| D-01 | Final map presentation: 2D, 3D, or mixed? | Open. Creator leans toward 3D for beauty. Compare one matched scene before choosing. |
| D-02 | What does fixed camera permit? | Fixed camera required; final pan, follow, zoom range, and projection unresolved. Existing local view is an implementation reference. |
| D-03 | How is the small-country scale represented? | Large country-scale map required; scale compression, travel pace, geography size, and the role of local scenes unresolved. |
| D-04 | What triggers the genre change, and when? | Sudden, unexpected RPG-to-4X change required. Trigger, stage length, and narrative cause open. |
| D-05 | What happens to direct character control afterward? | Character remains central to the vision; degree of direct travel, questing, or delegation in stage two open. |
| D-06 | How does the strategic game advance? | Turn-based, real-time, or another model; economic rules, victory, setbacks, and faction behavior open. |
| D-07 | How do leadership identities differ? | Ruler, caravan master, and faith leader are creator-provided possibilities; exclusivity, progression, and alternatives open. |
| D-08 | What are the production constraints? | Target hardware, frame-time and memory budgets, art pipeline, staffing, and content volume open. Do not invent numerical targets as requirements. |
| D-09 | Will the End-day ever receive a reliable explanation? | Distant catastrophe and no explanation at the opening are required. Nuclear war and AI war are possibilities, not canon. Cause, date, surviving evidence, and any eventual revelation remain open. |
| D-10 | Which states and landscapes belong on the playable map? | Thousands of small nations exist in the wider setting; the playable scale remains a small country. Regional roster, borders, state count, and representation of trade, war, and intermarriage are open. |
| D-11 | How do unequal technologies affect travel and trade? | Rural animal caravans, advanced vehicles, scarce railways, drones, electric propulsion, and developed air defence are established. Player access, energy and maintenance needs, infrastructure, restrictions, and simulation depth are open. |
| D-12 | How are robots, religions, and body modification represented? | Semi-sentient and hive-mind robots, diverse faiths, and desires for metal modification are established; magic and mutants are excluded. Named groups, autonomy, doctrines, achievable modifications, and gameplay roles are open. |

When a decision is made, record its date, creator instruction or reviewed evidence,
chosen scope, and affected documents here. Keep unresolved details visibly open.

## Creator decision record

| Date | Source and scope | Recorded direction | Affected documents |
| --- | --- | --- | --- |
| 2026-10-21 | Creator's supplied world-setting passage; explicitly requested addition to the vision. | Append the passage verbatim. Establish a distant unexplained End-day, no magic or mutants, mostly poor agricultural societies, fragmented states, uneven technology and robots, caravan trade and varied transport, developed air defence, and diverse beliefs including metal body-modification aspirations. The earlier map priorities and sudden RPG-to-4X transition remain in force. | [vision](vision.md), [index](README.md), [world](world.md), [atmosphere](atmosphere.md), [gameplay](gameplay.md), [references](references.md), and this guide. |

This addition refines the earlier broad post-apocalyptic/frontier and Fallout
inspirations. It does not select a nuclear or AI-war origin, permit mutants or
magic, define a faction roster, or approve all proposed gameplay applications.

## Review standard

For art and map changes, inspect the actual gameplay framing and readability with
the HUD. For systems, verify the relevant state and consequences rather than
building tests that repeat implementation details. For rendering, measure the
representative scene on agreed hardware before committing to expensive effects.
Follow repository build and testing rules for code changes. Documentation-only
changes need text, source, and link checks; do not report unrun game tests as passed.
