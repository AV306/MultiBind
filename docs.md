# MultiBind Docs

## "Illegal" keys

These are keys where a pie menu would obstruct basic function, and as such are (currently) hardcoded not to open a pie menu, even if they are bound to multiple actions.

These keys are currently:

- Tab (the player list doesn't toggle, so it woukd only show for a frame or two if you multibound Tab and selected the "List Plaayers" option)
- WASD (these are movement keys, and a pie menu would absolutely wreck their behaviour)
- Shift/Control (pretty much everyone I've met uses these as sprint/sneak, and like WASD, a pie menu would break them)
- Space (technically, this one is fine, but I haven't met anyone who uses Space for anything other than jump)

HVB007 also disabled pie menus for CapsLock and left Alt, but I chose to allow pie menus for them for the following reasons:

- Most people don't use them for anything important, and it was nice to have the extra keys for pie menus.
- I use about 90 mods, and they have enough actions to fill my entire keyboard and then some, so I needed all the keys I could get; Left Alt is a neat key because it's so close to my thumb, and I was mildly annoyed that I couldn't put a pie menu on it.

<br>

## Pie Menu Labels

[TODO: pictures]

Previously, KeybindsGalore would label each sector of the pie menu with the name (but not the category) of the action it represented. For example, if you bound `G` to `Fullbright` under `Xenon Features`, `Some Action` from `Some Mod`, and `Pick Block` from vanilla, the pie menu labels would be `Fullbright`, `Some Action` and `Pick Block`. This is fine for most things.

However, most mods have a key to open their settings menu, and this is often just named `Settings`, `Configurations` or something along those lines. If you're like me and you bound a single key to all the config menus, the entire pie menu would just be `Settings`, `Configurations`, `Open Settings`, etc and it would be really hard to tell which mod's menu corresponds to which sector.

In this fork, the *category* of the action (The "heading" of the section in the keybinds menu, like `Gameplay` or `Xenon Features`) is also displayed along with the name. So, using the settings menu example from before, the pie menu is now `Some mod: Settings`, `Some other mod: Configurations`, etc, and now you can actually tell which menu you're opening!

<sup>(full disclaimer: The "[Xenon](https://github.com/AV306/xenon)" mentioned here is another mod of mine and it's got [a bunch of neat things you might like](https://github.com/AV306/xenon/FEATURES.MD)!)</sup>
