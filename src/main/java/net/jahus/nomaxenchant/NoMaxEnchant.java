package net.jahus.nomaxenchant;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoMaxEnchant implements ModInitializer {
    public static final String MOD_ID = "nomaxenchant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static NoMaxEnchantConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = NoMaxEnchantConfig.load();
        LOGGER.info("Anvil enchantment level cap has been removed! (globalCap={}, {} per-enchantment override(s))", CONFIG.globalCap, CONFIG.perEnchantment.size());
    }
}
