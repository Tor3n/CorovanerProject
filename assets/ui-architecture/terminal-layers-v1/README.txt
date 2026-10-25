COROVANER TERMINAL — SEPARATE PAINTINGS v1

Three registered layers, each exactly 1920 x 1080 pixels:
  layers/01-caravan-painting.png  Opaque replaceable landscape.
  layers/02-hinged-panel.png     Transparent terminal panel; empty enamel body.
  layers/03-metal-rim.png        Transparent outer perimeter frame.

Draw them in this order, all at (0,0). Replace only the first file to change
the landscape. The complete landscape extends underneath the terminal panel.
The title COROVANER and the VOL marking remain in the panel painting.
The tagline area and small four-rivet nameplate are blank for custom labels.

CONTROLS
  controls/button-enamel.png  Text-free enamel button, 478 x 100 including shadow.
  controls/lamp-hover.png     Round lit amber lamp, 70 x 70 including halo.
Use the same button for each action and draw your labels separately.
Show the lamp on hover or keyboard focus only. Reserve its space in every state.
Coordinates and example button positions are in layout.json (top-left origin).
The example uses a 480 x 100 placement box for the 478 x 100 button PNG.
Convert top-left coordinates when integrating into a bottom-left coordinate system.
No nine-patch or display-dependent stretching is needed for this composition.

EDITING AND PREVIEWS
Corovaner-Terminal-1920x1080.ora is an OpenRaster document with the three
full-canvas paintings on separate layers. Open in an editor supporting ORA.
assembly-empty.svg / .png show the three paintings assembled.
assembly-buttons.svg / .png add example controls; the SVG keeps labels as text
and references the separate PNGs. Keep the folder together when opening SVGs.
The first example button has its hover lamp lit; the others have no lamp.
The PNG previews are flattened examples, not the editable source layers.

SOURCE AND QUALITY
Art was reconstructed and separated with built-in image_gen using the supplied
terminal reference. The final panel edit uses the exact user-selected panel.
source/ preserves the native outputs and all generation prompts.
Native main-layer outputs are 1672 x 941, resampled to exactly 1920 x 1080.
The export dimensions do not imply extra native detail or 4K production masters.
The original attachment has not been replaced. The painting can be replaced
later by a real artist's work without changing the panel or frame.

This is an asset pack, not a runtime implementation or a new map reference.
The gameplay map remains full-display 3D and independent of the menu canvas.
