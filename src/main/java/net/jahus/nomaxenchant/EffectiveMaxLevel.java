package net.jahus.nomaxenchant;

import net.minecraft.util.Identifier;

/**
 * Single source of truth for the effective max level nomaxenchant would
 * apply for a given enchantment. Used by both the anvil mixin
 * (AnvilMaxLevelMixin) and the network broadcast (EffectiveMaxLevelsPayload
 * / ServerNetworking), so the two never drift apart.
 */
public final class EffectiveMaxLevel {

    // MC-231508: enchant levels are capped at 255 regardless of config.
    public static final int HARD_CAP = 255;

    private EffectiveMaxLevel() {
    }

    /**
     * @param id         the enchantment's registry id, or null if it could
     *                   not be resolved (falls back to vanillaMax)
     * @param vanillaMax the enchantment's own Enchantment#getMaxLevel()
     */
    public static int resolve(Identifier id, int vanillaMax) {
        // Single-level enchantments (max == 1) are intentionally never
        // boosted, matching the existing anvil mixin behavior.
        if (vanillaMax == 1) {
            return 1;
        }

        String key = id != null ? id.toString() : null;

        if (key != null && NoMaxEnchant.CONFIG.perEnchantment.containsKey(key)) {
            return Math.min(NoMaxEnchant.CONFIG.perEnchantment.get(key), HARD_CAP);
        }

        int globalCap = NoMaxEnchant.CONFIG.globalCap;
        if (globalCap > 0) {
            return Math.min(globalCap, HARD_CAP);
        }

        return vanillaMax;
    }
}
