package net.jahus.nomaxenchant.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public class AnvilMaxLevelMixin {

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
        return 255;
    }
}
