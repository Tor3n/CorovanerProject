# Modelling workflow and lessons

Read this guide before creating, editing, simplifying, posing, or exporting a 3D
model for this project. It records lessons from the protagonist work and defines
the checks to use on future assets. Adapt budgets and tolerances to the asset;
the protagonist's coordinates, triangle target, and texture sizes are examples,
not universal defaults.

## Start with an inspectable baseline

1. Identify the actual input file, objects, transforms, units, axes, materials,
   UV layers, packed textures, modifiers, shape keys, and rig, if present.
2. Count both stored mesh triangles and evaluated triangles after modifiers.
   Count exported triangles again later. Polygon and vertex counts alone do
   not establish the triangle budget.
3. Preserve the source. Make a working copy and keep compact delivery files
   separate from the editable master. Record which version the user accepted.
4. Capture front, back, top, underside, side, and isometric views before editing.
   Keep camera framing, projection, lighting, and scale consistent between
   before/after captures. Fit the entire model, including extended fingertips.
5. Inspect textured renders and neutral clay close-ups. Texture patches can
   look like holes; textures can also conceal actual cracks or poor topology.

Use anatomical left/right and state how that maps to the current view. The
character's right hand appears on image left in a front view; the mapping can
reverse in a top view. Do not transfer screen-space directions between cameras.

## Measure symmetry around the body, not the file origin

The protagonist's torso was centered near `X = -0.04682`, while its original
fingertip bounds were near `X = -0.5` and `X = +0.5`. Mirroring around `X = 0`
made the endpoints appear balanced numerically while leaving one arm too short
relative to the torso. This was the main failure in the earlier repair.

- Estimate the body's sagittal plane from several torso/pelvis sections and
  cross-check it against the neck, sternum, and head. Do not derive it from the
  fingertips being corrected. For a tilted character, fit a plane in an agreed
  coordinate frame rather than assuming a world-axis plane.
- For an axis-aligned plane at `x = c`, reflect positions with `x' = 2c - x`.
  Transform positions and directions appropriately if working between local
  mesh coordinates, object transforms, and world coordinates.
- Mark shoulder, elbow, wrist, and longest fingertip on both sides. Prefer rig
  joints when they are trustworthy. With an unrigged clothed mesh, label joint
  centers as fitted estimates; do not imply that hidden bones were measured.
- Measure upper arm, forearm, and wrist-to-fingertip separately in 3D. Also
  compare their summed length, wrist height/depth, palm direction, and silhouette.
  Equal fingertip heights alone do not establish equal arm lengths.
- Use triangle/plane intersections for surface sections where practical.
  Averaging arbitrary nearby vertices can bias a center toward denser topology.

For the protagonist, the unchanged left arm was the bilateral reference. The
working target was less than 3% difference in corresponding segment lengths,
with natural-looking shoulder/wrist transitions. Compute a documented relative
difference, such as `100 * abs(right_length / left_length - 1)`. This tolerance
is an artistic acceptance criterion, not a medical or anatomical standard.

Symmetry is only one check against reality. If believable human anatomy is the
goal, also use suitable anatomical or photographic references for joint placement,
limb proportions, palm orientation, and finger articulation. Matching an already
incorrect opposite limb can reproduce its errors. Preserve deliberate asymmetry
and stylization when they are part of the design.

## Deform the limb as a connected structure

Moving a palm into place and blending the displacement through a short forearm
region produced an unnatural bend. Moving the entire arm rigidly improved that
bend but did not correct the original unequal segment lengths.

- Solve shoulder, elbow, wrist, and hand placement together. Distinguish a pose
  change from a proportion change before choosing a transform.
- Prefer an existing armature or create a simple temporary rig for substantial
  pose edits. Rotate around plausible joints; check skin weights and volumes.
- For an unrigged repair, use a smooth mapping between measured limb stations
  or another controlled deformation. Preserve cross-section thickness, blend
  attachment changes into the shoulder, and inspect the elbow and wrist closely.
- If hand length must change, adjust its longitudinal dimension deliberately.
  Uniformly scaling the entire arm can unintentionally enlarge palm width,
  finger thickness, or the shoulder.
- Check for stretching, compression, folds, self-intersections, and sharp changes
  in direction. Positive deformation Jacobians can help detect local foldovers,
  but do not establish good anatomy or rule out global intersections.
- Transport authored custom normals with the deformation, or recompute them
  deliberately and review the result. For a differentiable deformation, normal
  transport uses the inverse transpose of its local Jacobian. Position edits
  alone can leave authored normals pointing in inappropriate directions.

A shape key makes a change reversible, but does not make it visually correct.
Verify shading at the key values that will actually be used. A static repair can
instead be saved as a separate mesh while retaining the original working file.

## Repair topology before simplifying

The imported protagonist contained coincident vertices split at UV borders.
Decimation simplified disconnected borders independently and opened visible
cracks around the fingers, wrists, and clothing. Joining coincident geometry
before decimation removed those cracks while retaining per-corner UVs.

1. Inspect connected components, duplicate positions, boundaries, degenerate
   faces, non-manifold edges, and intended separate surfaces.
2. Weld only vertices that should share geometry. Choose a small tolerance
   relative to the asset's scale and local feature size. The protagonist used
   `0.000001` model units; do not reuse that number without checking another asset.
3. Preserve UVs per face corner and intentional normal/material boundaries.
   Verify retained faces' UV coordinates rather than assuming welding is safe.
   Avoid joining adjacent fingers, clothing layers, or intentional openings.
4. Apply the reduction to the repaired topology and inspect the evaluated result
   again. For regions intended to be closed, require no unexpected boundary or
   non-manifold edges. Open cloth, hair cards, and separate shells need different
   checks; never blindly fill every detected opening.
5. Inspect finger webs, palms, cuffs, and armpits from underneath as well as above.
   An edge-on front view hides many of these problems.

Decimate is useful for a static prototype, but a triangle target alone is not a
quality target. For future animated characters, build deformation-friendly
topology around joints and fingers, then test bends. Preserve silhouette where
it matters and consider baking high-poly detail onto a deliberately retopologized
mesh instead of relying only on aggressive automatic decimation.

## Review the actual result, not just the intended transform

- Re-measure the evaluated output surface after deformation and simplification.
  Testing that a transform preserves distances does not prove that both limbs
  have equal lengths. Testing target coordinates does not prove the mesh follows
  those targets.
- Draw the body centerline and shoulder-elbow-wrist-fingertip chains on the renders.
  Distinguish actual left, actual right, and mirrored reference with separate
  colors; use dashed lines for the reference. Show the failure as well as the fix.
- Review front, back, top, underside, side, and isometric views, plus close-ups
  without textures. Check proportions at gameplay viewing size as well as close up.
- For substantial anatomy, proportion, or pose changes, use an independent critic
  agent when available. Give it the actual renders and measurements, ask it to
  challenge the result, and address concrete defects before delivery. If a critic
  is unavailable, perform a separate explicit review pass with the same criteria.
- State what the evidence establishes. Fitted landmarks and visual agreement
  establish bilateral consistency; they are not exact skeletal reconstruction.
  Avoid false precision when explaining results to the user.

The accepted protagonist had approximately 0.17% difference in summed
shoulder-to-fingertip length and 0.61% in hand length. The independent critic also
checked continuous shoulder/elbow/wrist transitions. Finger spread and sleeve
shape retained small variations. See the [annotated review](characters/Protoganist/hand_review/index.html)
and [measurements](characters/Protoganist/maleprot_balance_measurements.json).

## Separate the editable master from the compact asset

An unapplied Decimate modifier can display 10,000 triangles while the `.blend`
still stores roughly 193,000 source triangles. Reducing the displayed triangle
count does not shrink packed textures, and disk size is not runtime memory usage.

| Deliverable | Purpose | What to check |
| --- | --- | --- |
| Editable master | Further modelling and inspection | Preserve useful source topology and modifiers; disclose stored versus evaluated counts |
| Compact `.blend` | Small applied asset for handoff | Apply intended modifiers, remove unused source mesh data, pack resized textures, save with compression |
| Runtime/interchange export | Import into the target pipeline | Export only intended objects; verify format, axes, materials, textures, and importer support |

- Inventory packed image dimensions and byte sizes before optimizing. Choose
  texture resolution from the intended camera distance and detail requirements.
  Reducing each dimension from 4K to 2K gives one quarter of the texels, independent
  of mesh reduction. Actual runtime usage also depends on pixel format, mipmaps,
  compression, and whether resources are shared.
- Preserve texture roles and color spaces: base color uses sRGB; normal and
  roughness/metallic data use Non-Color. Prefer lossless encoding for normal maps
  and assess artifacts before using lossy encoding for other data maps.
- When resizing packed images in a script, explicitly replace the packed bytes
  with the resized encoding. Do not assume resized in-memory pixels removed the
  old packed image. Reopen the saved file and check dimensions.
- Native `.blend` compression reduces disk storage; it does not reduce the
  decoded geometry or texture footprint by the same percentage.
- Check runtime support before enabling specialized export compression. A GLB
  in `docs/` is an interchange asset, not proof of integration into libGDX.
- Reimport the export and compare triangle count, surface positions, bounds,
  UVs, texture dimensions, and materials. Exported vertex counts can increase
  at UV/normal seams without increasing the triangle count or changing shape.

Measured protagonist outputs, using decimal MB:

| File | Size | Geometry/textures |
| --- | --- | --- |
| `maleprot_lowpoly_balanced.blend` | 12.78 MB | Editable source mesh, 10,000 evaluated triangles, 4K images |
| `maleprot_lowpoly_balanced_2k.blend` | 2.61 MB | 10,000 stored triangles, 4,935 vertices, no remaining modifiers, three 2K images |
| `maleprot_lowpoly_balanced_2k.glb` | 2.53 MB | 10,000 triangles, 7,865 exported vertices, three embedded 2K images |

The compact copy retained the master's evaluated geometry and UVs. The GLB
reimport had zero measured position error in that validation. The closed right
hand/forearm retained zero boundary or non-manifold edges. These are recorded
results for this asset, not guarantees for future exports.

## Make future runs reproducible and easier to review

Keep scripts, parameters, measurements, and representative before/after renders
beside the source asset. Use descriptive accepted/output names and refuse silent
overwrites. Do not make a successful workflow depend unnecessarily on a rejected
intermediate file; extract reusable measurements and operations into helpers.

For scripted Blender runs, use `--background --disable-autoexec` and
`--python-exit-code 1` so script exceptions produce a failing exit status. Verify
expected outputs and reopen them; a log line or successful process alone is not
sufficient. Discover the installed executable rather than hard-coding a user's
machine path into reusable project instructions.

Useful examples from this work:

- [Surface-section measurements](characters/Protoganist/maleprot_arm_measure.py)
- [Torso-centered arm balancing and UV-preserving welding](characters/Protoganist/maleprot_balance_arms.py)
- [Measured/reference guide renders](characters/Protoganist/maleprot_draw_arm_guides.py)
- [Compact asset creation](characters/Protoganist/maleprot_make_compact.py)
- [Saved-file and export verification](characters/Protoganist/maleprot_verify_compact.py)
- [Compact size and validation report](characters/Protoganist/maleprot_compact_stats.json)

These are asset-specific examples, not generic anatomy solvers. Future tools
should accept input/output paths and budgets, derive or expose landmarks for
review, and separate measurement, deformation, topology repair, and packaging.

When integrating an asset into the game, follow the
[character/world architecture](characters/architecture.md) and repository asset
ownership rules. Verify the Blender-to-runtime axis conversion: Blender authoring
here used Z-up, while the game uses Y-up and X/Z as ground. Confirm scale, forward
direction, and foot placement in the existing shared world preview. Do not change
the world camera to disguise an asset problem. After runtime asset/reference
changes, run the appropriate project checks and desktop visual smoke test.
