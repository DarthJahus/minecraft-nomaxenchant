package net.jahus.nomaxenchant.mixin;

import net.jahus.nomaxenchant.EffectiveMaxLevel;
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

    @Redirect(
        method = "updateResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"
        )
    )
    private int nomaxenchant$removeMaxLevelClamp(Enchantment enchantment) {
        int vanillaMax = enchantment.getMaxLevel();
        Identifier id = nomaxenchant$resolveId(enchantment);
        return EffectiveMaxLevel.resolve(id, vanillaMax);
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
