package net.jahus.nomaxenchant.mixin;

import net.jahus.nomaxenchant.EffectiveMaxLevel;
import net.jahus.nomaxenchant.NoMaxEnchant;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Redirects Enchantment#getMaxLevel() inside AnvilMenu#createResult so the
 * anvil no longer clamps combined enchantment levels to the vanilla maximum.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMaxLevelMixin {

    @Redirect(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"
        )
    )
    private int nomaxenchant$removeMaxLevelClamp(Enchantment enchantment) {
        int vanillaMax = enchantment.getMaxLevel();
        Identifier id = nomaxenchant$resolveId(enchantment);
        return EffectiveMaxLevel.resolve(id, vanillaMax);
    }

    private Identifier nomaxenchant$resolveId(Enchantment enchantment) {
        MinecraftServer server = NoMaxEnchant.server;
        if (server == null) {
            return null;
        }
        try {
            return server.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getKey(enchantment);
        } catch (Exception e) {
            return null;
        }
    }
}
