### Added
- Rewrote shape matching to better match modded items.
- The mod's builtin shapes can now be overridden by overriding the `data/clutternomore/shape_map/clutternomore.json` file.
- The Clutter No More Resource pack has been made internal, fixing issues with it not being applied on first launch and the double reload seen in CNM 1.1+.
- Major improvements to recipe removal.
- Searching for a shape item in the creative menu or supported recipe viewers (EMI/JEI/RRV) now shows the parent item.
- By default, hovering over an item in the inventory now shows a tooltip is now shown explaining how to use Clutter No More.
- By default, hovering over an item in the creative inventory or supported recipe viewers (EMI/JEI/RRV) now shows all shapes without having to hold [Alt].
- Tag translations for CNM tags.
- Support for Sable tags.
- Support for shape switching via Controlify.
- Shape switcher config is now shown when clicking the mod settings in Mod Menu or the NeoForge mods screen.

### Changed
- Components are now preserved when switching shapes.

### Fixed
- Fixed Sodium options screen with non-default GUI scales.
- Fixed double vertical slabs not clearing waterlogged state.
- Fixed pick block handling on 1.21.1 and below.
- Fixed crash with Fractal Lib.
- Fixed broken Chiseled Bookshelf recipe on 26.1 and added CNM recipe for Chiseled Resin Bricks.
- Many, many minor fixes and optimizations.