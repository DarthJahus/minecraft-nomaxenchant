package net.jahus.nomaxenchant.mixin;

import net.jahus.nomaxenchant.NoMaxEnchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilMaxLevelMixin extends ForgingScreenHandler {

    protected AnvilMaxLevelMixin(@Nullable ScreenHandlerType<?> type, int syncId,
                                  PlayerInventory playerInventory, ScreenHandlerContext context,
                                  ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    private static final int HARD_CAP = 255; // MC-231508: enchant levels are capped at 255

    @Redirect(
        method = "updateResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"
        )
    )
    private int nomaxenchant$removeMaxLevelClamp(Enchantment enchantment) {
        int vanillaMax = enchantment.getMaxLevel();

        if (vanillaMax == 1) {
            return 1;
        }

        Identifier id = nomaxenchant$resolveId(enchantment);
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

    private Identifier nomaxenchant$resolveId(Enchantment enchantment) {
        if (this.player == null || this.player.getWorld() == null) {
            return null;
        }

        DynamicRegistryManager registryManager = this.player.getWorld().getRegistryManager();
        if (registryManager == null) {
            return null;
        }

        return registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getId(enchantment);
    }
}
