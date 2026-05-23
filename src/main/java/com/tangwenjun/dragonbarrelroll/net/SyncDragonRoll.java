package com.tangwenjun.dragonbarrelroll.net;

import com.tangwenjun.dragonbarrelroll.DoABarrelRoll;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Client→Server: stores roll/pitch in independent maps, broadcasts to tracking clients.
 * Server→Client: stores in independent maps for DragonRenderer/PassengerAttachmentMixin.
 */
public record SyncDragonRoll(int playerId, float roll, float pitch, float yaw) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncDragonRoll> TYPE =
            new CustomPacketPayload.Type<>(DoABarrelRoll.id("sync_dragon_roll"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDragonRoll> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncDragonRoll::playerId,
            ByteBufCodecs.FLOAT, SyncDragonRoll::roll,
            ByteBufCodecs.FLOAT, SyncDragonRoll::pitch,
            ByteBufCodecs.FLOAT, SyncDragonRoll::yaw,
            SyncDragonRoll::new
    );

    /** Independent storage for synced barrel roll data, isolated from DragonSurvival's MovementData. */
    private static final ConcurrentHashMap<Integer, Float> syncedRollDeg = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Float> syncedPitch = new ConcurrentHashMap<>();

    public static float getSyncedRollDeg(int playerId) {
        return syncedRollDeg.getOrDefault(playerId, 0f);
    }

    public static float getSyncedPitch(int playerId) {
        return syncedPitch.getOrDefault(playerId, 0f);
    }

    /**
     * Client receiving from server broadcast → store roll/pitch for DragonRenderer use.
     * Does NOT write to MovementData to avoid corrupting DragonSurvival's bodyYaw/model-offset.
     */
    public static void handleClient(final SyncDragonRoll packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            syncedRollDeg.put(packet.playerId(), packet.roll());
            syncedPitch.put(packet.playerId(), packet.pitch());
        });
    }

    /**
     * Server receiving from a flying client:
     * 1. Store in independent maps (used by PassengerAttachmentMixin on server).
     * 2. Broadcast to tracking clients.
     * Does NOT write to MovementData — PassengerAttachmentMixin handles position independently.
     */
    public static void handleServer(final SyncDragonRoll packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // Store in independent maps for server-side PassengerAttachmentMixin
            syncedRollDeg.put(packet.playerId(), packet.roll());
            syncedPitch.put(packet.playerId(), packet.pitch());
            // Broadcast to all players tracking this entity (and the player himself)
            Entity tracked = context.player().level().getEntity(packet.playerId());
            if (tracked != null) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(tracked, packet);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
