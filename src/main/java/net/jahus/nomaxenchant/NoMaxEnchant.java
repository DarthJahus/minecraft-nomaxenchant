package net.jahus.nomaxenchant;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.jahus.nomaxenchant.network.EffectiveMaxLevelsPayload;
import net.jahus.nomaxenchant.network.ServerNetworking;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoMaxEnchant implements ModInitializer {
    public static final String MOD_ID = "nomaxenchant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static NoMaxEnchantConfig CONFIG;

    /** Cached server instance for registry lookups from the anvil mixin. */
    public static volatile MinecraftServer server;

    @Override
    public void onInitialize() {
        CONFIG = NoMaxEnchantConfig.load();

        PayloadTypeRegistry.clientboundPlay().register(EffectiveMaxLevelsPayload.ID, EffectiveMaxLevelsPayload.CODEC);
        ServerNetworking.init();

        ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> {
            if (server == s) {
                server = null;
            }
        });

        LOGGER.info("Anvil enchantment level cap has been removed! (globalCap={}, {} per-enchantment override(s))",
                CONFIG.globalCap, CONFIG.perEnchantment.size());
    }
}
