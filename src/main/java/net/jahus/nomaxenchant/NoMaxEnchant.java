package net.jahus.nomaxenchant;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.jahus.nomaxenchant.network.EffectiveMaxLevelsPayload;
import net.jahus.nomaxenchant.network.ServerNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoMaxEnchant implements ModInitializer {
    public static final String MOD_ID = "nomaxenchant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static NoMaxEnchantConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = NoMaxEnchantConfig.load();

        PayloadTypeRegistry.playS2C().register(EffectiveMaxLevelsPayload.ID, EffectiveMaxLevelsPayload.CODEC);
        ServerNetworking.init();

        LOGGER.info("Anvil enchantment level cap has been removed! (globalCap={}, {} per-enchantment override(s))", CONFIG.globalCap, CONFIG.perEnchantment.size());
    }
}
