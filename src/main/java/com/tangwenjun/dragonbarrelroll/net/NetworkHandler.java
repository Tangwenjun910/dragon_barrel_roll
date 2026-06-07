package com.tangwenjun.dragonbarrelroll.net;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

/**
 * Registers network packets for Dragon Barrel Roll.
 * Follows DragonSurvival's pattern: EventBusSubscriber listening to RegisterPayloadHandlersEvent.
 */
@EventBusSubscriber
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL_VERSION);

        // Bidirectional: client→server (store+broadcast), server→client (store for rendering)
        // Marked optional: clients/servers without this mod can still connect
        registrar.optional().playBidirectional(
                SyncDragonRoll.TYPE,
                SyncDragonRoll.STREAM_CODEC,
                new DirectionalPayloadHandler<>(SyncDragonRoll::handleClient, SyncDragonRoll::handleServer)
        );
    }
}
