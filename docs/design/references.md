# Visual reference index

**Development reference.** Images communicate selected qualities; they do not
override [the vision](vision.md) or turn a prototype into final art direction.

This guide collects references and briefs for the game we intend to create.
The future country map and travel experience need their own concept work, judged
against [the creator's vision](vision.md).

## Creator direction — 2026-10-21

The creator rejected the existing map material and travel interface as references
for this guide. Do not use their appearance, composition, layout, or interactions
as a design baseline, a quality target, or a limit on ambition. Their screenshots
are intentionally excluded from this reference index.

Develop the future vision on its own terms: a beautiful, compelling country map
that makes the player want to explore, with the main action taking place on it.
The final map art and travel-interface design remain to be developed. New concepts
must earn approval through their contribution to that experience; resemblance to
the existing implementation is not an acceptance criterion.

The creator's later world-setting passage on the same date adds constraints for
new references: the End-day is distant and unexplained at the opening; most
society is poor and agricultural, with small states, advanced exceptions,
semi-sentient and hive-mind robots, and diverse beliefs. There is no magic and
there are no mutants. Caravans and trade connect deserts, scarce forests,
industrial wastelands, and great city ruins. Rural ox- and horse-drawn wagons have
frontier-western character; advanced transport and air defences exist unevenly.
Use [world.md](world.md) to scope those references. An image of a robot does not
establish an AI war, and Fallout inspiration does not establish a nuclear history.

## Earlier UI concept and stretchable components

The `codex/postapocalyptic-ui-theme` branch contains earlier UI artwork and a
documented component approach that are absent from the inspected `main` tree.
Use these pinned links so the references do not silently change with the branch:

- [Earlier main-menu reference image](https://github.com/Tor3n/CorovanerProject/blob/41b222e2d9a80536a7c7816acf1d44c4e8f042b0/assets/ui-architecture/reference/main-menu.jpg).
- [UI architecture and reference status](https://github.com/Tor3n/CorovanerProject/blob/41b222e2d9a80536a7c7816acf1d44c4e8f042b0/assets/ui-architecture/README.md).
- [Stretchable element-set contracts](https://github.com/Tor3n/CorovanerProject/blob/41b222e2d9a80536a7c7816acf1d44c4e8f042b0/assets/ui-architecture/element-set/README.md).

That branch's documentation identifies its image as an approved reference for an
earlier frontier-terminal direction. It describes separate frames, panels,
controls, live text, and fixed-size hardware. Reuse those ideas where appropriate.
The creator's later request for rusty metal, planks, and vintage lights refines
the aesthetic and takes precedence over overly sci-fi treatment. This guide does
not merge that branch or claim its runtime assets are present on main.

## Proposed next concept-art briefs

These are coworker proposals for future images. Their generation and acceptance
are separate tasks; none of them is a finished or approved asset.

| Brief | What the image should explain | What it must leave open |
| --- | --- | --- |
| Country-map mood frame | A fixed-angle arid region with a compelling trade road, working farmland, weathered city or industrial ruins, and a small traveler or rural caravan; atmosphere concentrated around terrain and light. | Exact renderer, world dimensions, faction borders, final geography, and the cause of the End-day. |
| Exploration detail frame | The same visual language closer to a road junction, farming settlement, or caravan stop; show why a player would investigate a side route and how people make a living. | Combat rules, named quest outcomes, final building functions, and economic mechanics. |
| Caravan and transport contrasts | Memorable ox- and horse-drawn wagons alongside separate studies of advanced cars, scarce rail, electric propulsion, and drones; show terrain and support context. | Final designs, vehicle access, capacities, speed, fuel, and progression systems. |
| Communities, beliefs, and robots | Distinct farming and advanced communities, varied religious material culture, and separate semi-sentient and hive-mind robot studies; body-modification ideas clearly labeled as proposals. | Named factions, doctrines, symbols, robot origins and autonomy, and modification capabilities. No magic or mutants. |
| Material and UI sheet | Rusty metal, repaired planks, vintage amber lights, brackets and fasteners; text-free button/frame surfaces plus separate hardware. | Exact UI dimensions, final color values, and interaction behavior. |
| Development-only continuity pair | The same geography and camera framing before and after the reveal; altered routes, relationships, and strategic overlays show changed responsibility while terrain stays recognizable. | The narrative trigger, final political systems, and approved late-game interface. Never use this pair as early promotional art. |

For a new image, record its repository path, creator/source, date, status
(`proposal`, `approved for specified aspects`, or `superseded`), the qualities to
carry forward, and the details to ignore. Keep source artwork and implementation
assets distinguishable. Do not infer a game mechanic merely because it appears in
generated art. Store new design-only references under `docs/design/reference/`
when created; the directory is not required until it contains a real asset.
