package net.jahus.nomaxenchant.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * S2C payload broadcasting the effective max enchantment level nomaxenchant
 * would apply for each enchantment (see EffectiveMaxLevel). This is a
 * public, documented channel: any client-side mod may listen to it without
 * a compile-time dependency on nomaxenchant. Only one client-side global
 * receiver can be registered per CustomPayload.Id (Fabric API constraint -
 * ClientPlayNetworking.registerGlobalReceiver returns false and does
 * nothing if a receiver is already registered), so only one consumer mod
 * can listen at a time under this exact id.
 *
 * PROTOCOL_VERSION is included so a consumer can detect a future breaking
 * change to this payload's shape rather than fail on a silent
 * misinterpretation of the bytes. There is no official Fabric standard for
 * payload versioning; this is a defensive convention, not a Fabric API
 * feature. Consumers should check protocolVersion() before trusting
 * maxLevels().
 */
public record EffectiveMaxLevelsPayload(int protocolVersion, Map<Identifier, Integer> maxLevels)
        implements CustomPayload {

    /** Bump this if the wire format changes in a way older consumers can't parse. */
    public static final int PROTOCOL_VERSION = 1;

    public static final CustomPayload.Id<EffectiveMaxLevelsPayload> ID =
            new CustomPayload.Id<>(Identifier.of("nomaxenchant", "effective_max_levels"));

    // PacketCodec.of expects a ValueFirstEncoder: lambda signature is
    // (value, buf), confirmed against Yarn 1.21.4+build.8 javadoc.
    public static final PacketCodec<RegistryByteBuf, EffectiveMaxLevelsPayload> CODEC = PacketCodec.of(
            (EffectiveMaxLevelsPayload payload, RegistryByteBuf buf) -> {
                buf.writeVarInt(payload.protocolVersion());
                buf.writeVarInt(payload.maxLevels().size());
                for (Map.Entry<Identifier, Integer> entry : payload.maxLevels().entrySet()) {
                    buf.writeIdentifier(entry.getKey());
                    buf.writeVarInt(entry.getValue());
                }
            },
            (RegistryByteBuf buf) -> {
                int version = buf.readVarInt();
                int size = buf.readVarInt();
                Map<Identifier, Integer> map = new HashMap<>(size);
                for (int i = 0; i < size; i++) {
                    map.put(buf.readIdentifier(), buf.readVarInt());
                }
                return new EffectiveMaxLevelsPayload(version, map);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
