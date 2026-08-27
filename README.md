# NoMaxEnchant
Removes the anvil's enchantment level clamp, so combining books above the vanilla cap (e.g. Sharpness VI, Protection V) applies the enchantment at its real level instead of silently dropping back to vanilla max.

## The problem this solves
Vanilla always clamps an enchantment's result on an anvil to its own `getMaxLevel()`, no matter what datapacks, loot table or other mods put into the book the user tries to apply. Two Sharpness V books combine into Sharpness V, even if the enchantment itself is able to go higher; the anvil throws the extra level away.

**NoMaxEnchant** removes that clamp. Whatever level the anvil calculation would naturally produce is what applies, up to a hard [ceiling of 255](https://bugs.mojang.com/browse/MC/issues/MC-252460).

## Environment
**Server-side only.** The mod only needs to run on the server; the anvil calculation it patches is server-authoritative.

## Known limitations
**Anvil only.** Any other way an enchantment's level could be set (commands, other mods' custom enchanting mechanics) isn't affected.

## Mechanics
- **Target**: only the anvil's enchantment level calculation (`updateResult`). Nothing else about anvil behavior (repair cost, item name, XP cost, slots) is touched.
- **Default behavior**: with no configuration (`"globalCap": -1`, empty `perEnchantment`), the mod does nothing observable. It returns `vanillaMax` unchanged, exactly what `getMaxLevel()` would have returned anyway. The clamp only starts doing something once `globalCap` or a `perEnchantment` entry is set.
- **Global cap**: an optional single level ceiling applied to every enchantment at once.
- **Per-enchantment cap**: an optional override for individual enchantments, by their id (e.g. `minecraft:sharpness`), taking priority over the global cap.
- **Hard ceiling**: no enchantment can ever exceed level 255, regardless of configuration. This isn't a design choice, it's a Minecraft engine limit ([MC-231508](https://bugs.mojang.com/browse/MC-231508)).
- **Level 1 enchantments**: enchantments whose max is already 1 are left untouched (nothing to uncap).

## Vanilla effective max levels

Minecraft has hard internal limits on how high most enchantments can usefully go. Raising a level past these points produces no additional effect in-game (or can even break the mechanic, as with Lure or Quick Charge).

The full reference is available in **[vanilla.md](vanilla.md)**; complete table of every vanilla enchantment that scales past its normal maximum, with explanations.

## Configuration
On first run, NoMaxEnchant writes `config/nomaxenchant.json`:

```json
{
  "globalCap": -1,
  "perEnchantment": {
    "dummy:dummy": 42
  }
}
```

- `globalCap`: any value `> 0` becomes the level ceiling for every enchantment that has no specific override. `-1` (default) means "no global cap, fall back to vanilla max".
- `perEnchantment`: maps an enchantment's id to its own cap. Takes priority over `globalCap`. The `dummy:dummy` entry is a placeholder showing the expected format; it doesn't match any real enchantment and can be removed.

If the config file is missing, empty or fails to parse, NoMaxEnchant falls back to vanilla behavior (no cap changes) and logs a warning; it never crashes the server over a bad config file.

### Example configuration

[`vanilla.json`](vanilla.json) is a ready-to-use config that sets every vanilla enchantment to its **true effective maximum**.

You can:
- Copy it directly as `./config/nomaxenchant.json`
- Use it as a base
- Add entries for modded enchantments

## Licence
[Apache 2.0 Licence](/LICENSE.md)
