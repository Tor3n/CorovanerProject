# Corovaner — main concept and design guide

**Developer reference.** Established 2026-10-21.

Start here for development, art, writing, UI, and technical planning. This is the
shared creative reference for the creator and collaborators, including AI coworkers.
It describes the intended game; it does not claim that the prototype implements it.

## Read in this order

| Document | Purpose | Authority |
| --- | --- | --- |
| [Original vision](vision.md) | The creator's entire supplied paragraph, unchanged, including spelling and spacing. | Primary creative source. |
| [Atmosphere and visual direction](atmosphere.md) | Map priorities, mood, materials, camera, effects, and UI. | Requirements and earlier creator direction are identified separately from proposals. |
| [Exploration and campaign progression](gameplay.md) | How the RPG opening and later strategic game connect. | Derived requirements plus explicitly labeled proposals. |
| [Development guide and open decisions](development-guide.md) | How to apply the vision, evaluate work, and avoid premature commitments. | Coworker guidance; unresolved choices remain unresolved. |
| [Visual reference index](references.md) | Direction for future concept art, selected UI references, and criteria for new visual targets. | Creator direction and explicitly scoped references. |
| [Coffee-enamel UI study](ui-art-direction.md) | Metal-button preferences, reusable components, and the centered 1920 x 1080 composition with painted side margins and uniform downscaling. | Creator display rules are distinguished from remaining implementation proposals. |

## Creative foundation

The following is a collaborator's summary of [the original](vision.md), not a replacement:

1. **The map is the visual centerpiece.** Seeing it should make the player want to travel into it. Characters and battles have lower visual-detail priority.
2. **Exploration is the game.** Roads, sites, quests, and encounters should reward curiosity; the map hosts the main action across a country-sized setting.
3. **The landscape is largely static, with a fixed camera.** Atmospheric effects can bring it to life without buildings popping into existence or construction reshaping the scenery.
4. **The journey begins personally.** One character travels, visits places, completes quests, and establishes relationships with peoples and factions.
5. **The game changes genre suddenly.** It becomes a 4X game about a caravan empire and a chosen form of leadership. The creator's aim is for the player not to anticipate this expansion.
6. **Early choices matter later.** The strategic situation grows from relationships and decisions made during the RPG stage.

On 2026-10-25 the creator confirmed that the main map will be **3D and fill the
entire monitor at every resolution**. This resolves the earlier 2D-versus-3D
question preserved in the original vision. The centered illustrated menu and
its painted margins have a separate display policy; see the
[map and menu display rules](ui-art-direction.md). Camera projection, framing
across aspect ratios, and production rendering details remain open. The existing
implementation is engineering evidence, not the final visual target.

## Authority and maintenance

- Explicit creator decisions govern creative direction. Preserve the original paragraph as a historical source; record later decisions separately, with their date and scope.
- Everything authored by a collaborator must identify itself as a requirement interpretation, a proposal, a current implementation fact, or an open decision. A proposal does not become approved merely because it was committed.
- Images support specific aspects of a design. Record which aspects the creator approves before treating a new image as a target.
- The creator rejected the existing map material and travel interface as design references on 2026-10-21. Develop the future map and travel experience from the vision; do not use those prototypes as visual or interaction baselines, quality targets, or limits on ambition.
- Existing engineering constraints remain in force for implementation. If a desired design requires changing them, describe the conflict and propose the migration explicitly.
- Keep durable decisions in this guide rather than relying on chat memory. Update the relevant document and the decision table when the creator resolves an open question. Preserve superseded decisions with a short explanation.

## Protecting the reveal

To preserve the intended player experience, keep the genre change,
late-game labels, screenshots, and explanatory diagrams out of ordinary player-facing
menus, tutorials, loading tips, achievements, store copy, and early promotional art
unless the creator explicitly chooses otherwise. Teach strategic controls when the
change occurs. Accessibility and control help must remain honest and usable.
