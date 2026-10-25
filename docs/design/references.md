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
The creator subsequently rejected planks as too cowboy and preferred coffee-cream
enamel metal buttons with round vintage lamps. That preference supersedes the
intermediate plank treatment. This guide does not merge the earlier branch.

## Coffee-enamel controls and fixed-proportion scaling discussion

See [the current UI study](ui-art-direction.md) for creator preferences and the
still-open proposal to scale fixed illustrated modules uniformly.

- [Modal material reference](../../assets/MainModal.png): creator-preferred button
  material and rounded metal form, not a final screen-layout contract.
- [Component inventory](../../assets/ui-architecture/coffee-enamel/component-sheet.png):
  33 reusable surfaces, hardware parts and icons; optional nine-patch exports.
- [Scoped screen studies](reference/coffee-enamel/README.md): generated material
  and control-variety examples. The travel composition is retained only for those
  qualities and does not reinstate the rejected map or travel design baseline.

## Proposed next concept-art briefs

These are coworker proposals for future images. Their generation and acceptance
are separate tasks; none of them is a finished or approved asset.

| Brief | What the image should explain | What it must leave open |
| --- | --- | --- |
| Country-map mood frame | A fixed-angle view of an arid region, a compelling road, distinctive distant sites, a small traveler, and atmosphere concentrated around terrain and light. | Exact renderer, world dimensions, faction borders, and final geography. |
| Exploration detail frame | The same visual language closer to a road junction or settlement; show why a player would investigate a side route. | Combat rules, named quest outcomes, and final building functions. |
| Further material and UI refinement | Coffee-cream enamel metal, worn steel, round vintage amber lights, brackets and fasteners; fixed-proportion illustrated modules plus separate hardware and text. | Exact UI dimensions, final color values, scaling policy, and full interaction behavior. |
| Development-only continuity pair | The same geography and camera framing before and after the reveal; altered routes, relationships, and strategic overlays show changed responsibility while terrain stays recognizable. | The narrative trigger, final political systems, and approved late-game interface. Never use this pair as early promotional art. |

For a new image, record its repository path, creator/source, date, status
(`proposal`, `approved for specified aspects`, or `superseded`), the qualities to
carry forward, and the details to ignore. Keep source artwork and implementation
assets distinguishable. Do not infer a game mechanic merely because it appears in
generated art. Store new design-only references under `docs/design/reference/`
when created; the directory is not required until it contains a real asset.
