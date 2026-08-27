# Vanilla effective max levels
Source: [Minecraft Wiki / Enchantment § Maximum effective values for enchantments](https://minecraft.wiki/w/Enchantment#Maximum_effective_values_for_enchantments). Java Edition only.

Only enchantments where a level above vanilla max has an actual effect are listed. Enchantments whose effective range is `1–1` (Aqua Affinity, Channeling, Curse of Binding, Curse of Vanishing, Flame, Infinity, Mending, Silk Touch) and Multishot (effective range `1–255` on paper, but its only effect is binary and doesn't change past level 1) are excluded: there's nothing to gain from raising their cap.

| Enchantment              | Vanilla max | Effective max | Notes                                                 |
|--------------------------|-------------|---------------|-------------------------------------------------------|
| Sharpness                | V           | 255           | Scales without limit                                  |
| Smite                    | V           | 255           | Scales without limit                                  |
| Bane of Arthropods       | V           | 255           | Scales without limit                                  |
| Impaling                 | V           | 255           | Scales without limit                                  |
| Breach                   | IV          | 255           | Scales without limit                                  |
| Density                  | V           | 255           | Scales without limit                                  |
| Efficiency               | V           | 255           | Quadratic (`levels_squared`): steepest scaling of all |
| Fortune                  | III         | 255           | Scales without limit                                  |
| Looting                  | III         | 255           | Scales without limit                                  |
| Power                    | V           | 255           | Scales without limit                                  |
| Knockback                | II          | 255           | Scales without limit                                  |
| Punch                    | II          | 255           | Scales without limit                                  |
| Fire Aspect              | II          | 255           | Scales without limit                                  |
| Unbreaking               | III         | 255           | Scales without limit                                  |
| Respiration              | III         | 255           | Scales without limit                                  |
| Thorns                   | III         | 255           | Scales without limit                                  |
| Soul Speed               | III         | 255           | Scales without limit                                  |
| Sweeping Edge            | III         | 255           | Scales without limit (Java only)                      |
| Luck of the Sea          | III         | 255           | Scales without limit                                  |
| Riptide                  | III         | 255           | Scales without limit                                  |
| Wind Burst               | III         | 255           | Scales without limit                                  |
| Protection               | IV          | 20            | See explanation below                                 |
| Fire Protection          | IV          | 10            | See explanation below                                 |
| Blast Protection         | IV          | 10            | See explanation below                                 |
| Projectile Protection    | IV          | 10            | See explanation below                                 |
| Frost Walker             | II          | 14            |                                                       |
| Feather Falling          | IV          | 7             |                                                       |
| Loyalty                  | III         | 127           | Levels above 127 are treated as 0                     |
| Piercing                 | IV          | 127           | Levels above 127 are treated as 0                     |
| Lure                     | III         | 5             | Higher levels prevent fish from biting                |
| Quick Charge             | III         | 5             | Higher levels prevent charging the crossbow           |
| Swift Sneak              | III         | 5             |                                                       |
| Depth Strider            | III         | 3             | Doesn't scale past this                               |

## Why Protection caps at 20 (and its variants at 10)

Damage reduction from the Protection family does **not** use the enchantment level directly. It goes through an intermediate value called the **Enchantment Protection Factor (EPF)**:

1. Each armor piece contributes EPF according to its enchantment:
   - **Protection**: 1 EPF per level
   - **Fire / Blast / Projectile Protection**: 2 EPF per level (twice as potent per level, but only against their specific damage type)

2. When the player takes damage, the game sums the EPF from every applicable piece of armor, then **caps the total at 20**.

3. Final damage reduction is `4 % × capped EPF`: 20 EPF = exactly **80% damage reduction**, the hard ceiling of the system.

That's why the table lists:

- **Protection**: effective max 20  
  (level 20 on a single piece already produces 20 EPF: the sum-cap is reached alone)

- **Fire / Blast / Projectile Protection**: effective max 10  
  (level 10 on a single piece produces 20 EPF: same sum-cap)

Going higher still increases the raw EPF number stored on the item, but the engine clamps the *sum* to 20 before the damage formula ever sees it. In normal Survival play a full set of Protection IV only reaches 16 EPF, so the 20-cap is rarely hit without commands. With artificially high levels, a single piece can reach the ceiling by itself; which is exactly what "effective max" describes here.

## Notes on "255"
- 255 is not a NoMaxEnchant convention; it's a Minecraft engine limit ([MC-231508](https://bugs.mojang.com/browse/MC-231508)) — enchantment levels are stored as a `short` internally. Whatever `globalCap` or `perEnchantment` value you set, [NoMaxEnchant](/README.md) clamps to it regardless.
- For any enchantment listed with effective max 255, a higher `perEnchantment` value is technically accepted but produces no further change in-game; the formula keeps computing, but nothing reads a level past 255.
- For the ones with a lower effective max (20, 10, 14, 7, 127, 5, 3), setting a `perEnchantment` cap above that number is harmless but pointless: the extra levels apply on paper (visible in the tooltip, stored on the item) without changing the enchantment's actual behavior in-game.
