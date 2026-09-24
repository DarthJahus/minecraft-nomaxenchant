package net.jahus.nomaxenchant.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.jahus.nomaxenchant.EffectiveMaxLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class ServerNetworking {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            // Client is optional: nomaxenchant works standalone server-side.
            // Only send to clients that have registered this channel.
            if (!ServerPlayNetworking.canSend(player, EffectiveMaxLevelsPayload.ID)) {
                return;
            }

            Map<Identifier, Integer> maxLevels = new HashMap<>();
            var registry = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

            registry.listElements().forEach(entry -> {
                Identifier id = entry.key().identifier();
                int vanillaMax = entry.value().getMaxLevel();
                maxLevels.put(id, EffectiveMaxLevel.resolve(id, vanillaMax));
            });

            ServerPlayNetworking.send(player,
                    new EffectiveMaxLevelsPayload(EffectiveMaxLevelsPayload.PROTOCOL_VERSION, maxLevels));
        });
    }
}
