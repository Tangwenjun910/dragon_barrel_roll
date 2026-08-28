package com.tangwenjun.dragonbarrelroll.net;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Registers network packets for Dragon Barrel Roll.
 */
public class NetworkHandler {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(NetworkHandler::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Bidirectional: client→server (store+broadcast), server→client (store for rendering)
        // NeoForge 26.1 order: server handler first, client handler second.
        registrar.playBidirectional(
                SyncDragonRoll.TYPE,
                SyncDragonRoll.STREAM_CODEC,
                SyncDragonRoll::handleServer,
                SyncDragonRoll::handleClient
        );
    }
}
