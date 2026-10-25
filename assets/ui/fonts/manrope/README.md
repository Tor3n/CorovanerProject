# Manrope UI fonts

Creator decision, 2026-10-26: use Manrope for the coffee-enamel UI.
The runtime uses Regular (400) for body/compact labels and SemiBold (600)
for headings and enamel button captions. The existing `mono` skin ID is retained
for compatibility; it now selects compact Manrope, not a monospaced face.

These static TTF instances were generated from the complete, unsubsetted
[Google Fonts Manrope variable font](https://github.com/google/fonts/blob/main/ofl/manrope/Manrope%5Bwght%5D.ttf).
Upstream project: https://github.com/googlefonts/manrope.

- Source Git blob: `75274da58537d6123b14f2cd0c355ad4681fc2b3`.
- Source SHA-256: `3ae11c49db0455a3cc33e37d380f20fdb8c7f8b41dc07625c177e3d87a9d6ae6`.
- Conversion: fontTools 4.61.1, `instantiateVariableFont(font, {"wght": 400}, updateFontNames=True)`
  and the same operation at weight 600. No glyph subsetting or drawing changes.
- Regular SHA-256: `414953887e9e4990bb8e207c060894bd8ed6341dabc60d07a7d33438746b2c04`.
- SemiBold SHA-256: `94feff7f48471f7d2f8b0b9d5de148de89189b986f37870f767301ec774b77df`.

Static instances make the requested weights explicit to the existing libGDX
FreeType loader without adding runtime variable-font configuration. Menu fonts
are generated asynchronously through the application's AssetManager.

Copyright and redistribution terms are in [OFL.txt](OFL.txt), copied unchanged
from Google Fonts. These bundled fonts remain under the SIL Open Font License 1.1.
The older `frontier-*.ttf` files and their separate license remain available for
legacy references; the current menu theme does not load them.
