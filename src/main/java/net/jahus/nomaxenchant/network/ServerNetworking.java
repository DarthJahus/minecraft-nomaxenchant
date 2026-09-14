package net.jahus.nomaxenchant.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.jahus.nomaxenchant.EffectiveMaxLevel;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ServerNetworking {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            // Client is optional: nomaxenchant works standalone server-side.
            // Only send to clients that have registered this channel.
            if (!ServerPlayNetworking.canSend(player, EffectiveMaxLevelsPayload.ID)) {
                return;
            }

            Map<Identifier, Integer> maxLevels = new HashMap<>();
            // .getOrThrow(...), matching the working call already used in
            // AnvilMaxLevelMixin - DynamicRegistryManager does not expose
            // .get(RegistryKey) under these mappings.
            var registry = server.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

            registry.streamEntries().forEach(entry -> {
                Identifier id = entry.registryKey().getValue();
                int vanillaMax = entry.value().getMaxLevel();
                maxLevels.put(id, EffectiveMaxLevel.resolve(id, vanillaMax));
            });

            ServerPlayNetworking.send(player,
                    new EffectiveMaxLevelsPayload(EffectiveMaxLevelsPayload.PROTOCOL_VERSION, maxLevels));
        });
    }
}
