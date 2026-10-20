# Atmosphere and visual direction

**Development reference.** Read [the original vision](vision.md) first.
This document contains collaborator interpretation and proposals; it does not edit the original.

## Requirements derived from the creator's vision

The landscape carries the game's visual ambition. The player should notice a place
and want to discover what is there. A country-sized map hosts the main action;
characters and battles are substantially less detailed. The world is largely
static and the camera is fixed. Sudden appearance of scenery and visible building
construction conflict with that intent.

Particles, shaders, and other advanced effects are suggested means of improving the
map, not a prescribed technology list. 2D, 3D, or a combination remain open.
Fixed-camera behavior beyond that phrase also needs definition: the current
prototype has a fixed orientation with tracking and zoom, but that is not yet the
final country-map camera contract.

## Earlier creator direction carried forward

Earlier project discussions established an arid, worn, post-apocalyptic frontier
with caravan people, repaired equipment, and Fallout-inspired environmental art.
People should show hardship and varied occupations rather than uniformly polished,
idealized appearances. These notes preserve earlier direction; the new verbatim
vision is kept in its own file.

The latest UI direction favors **rusty metal, weathered planks, and vintage lights**.
The creator rejected overly sci-fi buttons. Modest salvaged technology can appear
as an accent, but it should not dominate the controls. Existing terminal-style art
must be interpreted in light of this more recent preference. See the
[reference index](references.md) for repository sources and their scope.

## Proposed emotional and visual vocabulary

All details in this section are proposals for review.

The desired feeling is a difficult country that is still worth crossing: long
distances, traces of human effort, moments of shelter, and places that suggest
stories before a quest marker appears. Beauty can come from composition, light,
color, and the relationship between settlements and terrain; it need not mean a
clean or comfortable world.

| Element | Proposed treatment | Intended effect |
| --- | --- | --- |
| Terrain | Broad landforms, readable roads, strong landmark silhouettes, quieter areas between destinations. | A place the player can mentally map and wants to traverse. |
| Palette | Sun-bleached earth, stone and faded vegetation; rust and charcoal for human traces; restrained amber lights. | Regional coherence with visible signs of habitation. Exact colors remain open. |
| Composition | Routes lead the eye toward partially understood sites; settlements sit plausibly near access, shelter, or water. | Curiosity arises from the landscape itself. |
| Surface detail | Concentrate craftsmanship at landmarks and travel junctions; vary terrain in large patches. | Avoid uniform visual noise and repetitive tile patterns. |
| Motion | Sparse drifting dust, localized smoke, subtle heat shimmer, occasional cloth or vegetation movement. | A living atmosphere within stable geography. These are candidates, not a mandatory effects list. |
| Sound | Wind, distant activity, material sounds and restrained location ambience. | Space and identity without requiring constant visible animation. Audio is a proposal, not an implemented feature. |
| People and battle | Clear silhouettes, readable movement and actions, economical detail. | Support play without competing with the map for production effort. |

## Proposed map and camera rules

- Author compositions at the actual play angle and scale. A beautiful close-up alone does not prove that the country map works.
- Preserve recognizable terrain and settlement forms when ownership or politics change. Consider routes, banners, map overlays, or existing-site status to express strategy; do not assume a construction mechanic.
- Distinguish stable scenery from changing gameplay state. Actors, caravans, interaction feedback, and political state may move or change while the landscape stays stable.
- Keep routes, selections, and encounter information readable with effects enabled and reduced. Never let haze or glare hide information the player needs.
- If loading or level-of-detail techniques are needed, evaluate them for visible popping at the intended camera scale. A large map does not require every asset to be resident simultaneously.

## Proposed UI treatment

Frames and controls should feel assembled and maintained: dented plates, repaired
wood, brackets, bolts, and warm vintage lamps. Keep text on quiet areas with clear
contrast. Use decoration to establish material character without covering the map
or making every button visually loud.

For stretchable controls, keep corners and hardware at stable proportions; stretch
quiet panel centers and straight edges. Keep labels, focus indication, and status
separate from background art. A lamp can reinforce a state but must not be its
only signal. The existing UI branch documents a useful implementation approach;
it does not freeze the final aesthetic.

## Review questions

Can a viewer name a place they want to visit from the map image alone? Is the road
readable? Does each area have a memorable identity? Does the map remain the visual
focus with the HUD present? Do effects enrich stable scenery? Are characters and
combat legible without demanding the highest-detail assets? These questions guide
reviews; final performance and art budgets are still open.
