package com.tangwenjun.dragonbarrelroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import com.tangwenjun.dragonbarrelroll.api.DragonRoll;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.impl.key.InputContextImpl;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import com.tangwenjun.dragonbarrelroll.render.HorizonLineWidget;
import net.neoforged.neoforge.network.PacketDistributor;

public class EventCallbacksClient {
    private static int syncTickCounter;
    private static boolean wasFlying;
    private static float lastSyncedRoll;
    private static float lastSyncedPitch;

    public static void clientTick(Minecraft client) {
        InputContextImpl.getContexts().forEach(InputContextImpl::tick);

        if (!DoABarrelRollClient.isFallFlying()) {
            DoABarrelRollClient.clearValues();
        }

        ModKeybindings.clientTick(client);

        // Send roll/pitch/yaw to server every 2 ticks for multiplayer sync
        syncBarrelRollData(client);
    }

    /** Sends the local player's barrel roll data to the server so other players can see the dragon tilt.
     *  Also writes to local MovementData so getPassengerAttachmentPoint uses correct values client-side. */
    private static void syncBarrelRollData(Minecraft client) {
        boolean flying = DoABarrelRollClient.isFallFlying();

        if (!flying) {
            // Just stopped flying — send a final cleanup packet to reset tilt on remote clients
            if (wasFlying && (lastSyncedRoll != 0 || lastSyncedPitch != 0)) {
                var player = client.player;
                if (player != null && client.getConnection() != null && client.getConnection().hasChannel(SyncDragonRoll.TYPE)) {
                    PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), 0, 0, 0));
                }
                lastSyncedRoll = 0;
                lastSyncedPitch = 0;
            }
            wasFlying = false;
            syncTickCounter = 0;
            return;
        }
        wasFlying = true;

        var player = client.player;
        if (player == null) return;

        syncTickCounter++;
        if (syncTickCounter < 2) return;
        syncTickCounter = 0;

        float roll = ((DragonRoll) player).dragon_barrel_roll$getRoll();
        float pitch = player.getXRot();
        float yaw = player.getYRot();

        lastSyncedRoll = roll;
        lastSyncedPitch = pitch;
        if (client.getConnection() != null && client.getConnection().hasChannel(SyncDragonRoll.TYPE)) {
            PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), roll, pitch, yaw));
        }
    }

    public static void onRenderCrosshair(GuiGraphics context, DeltaTracker tickCounter, int scaledWidth, int scaledHeight) {
        if (!DoABarrelRollClient.isFallFlying()) return;
        var tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);

        var matrices = context.pose();
        var entity = Minecraft.getInstance().getCameraEntity();
        var rollEntity = (DragonRoll) entity;
        if (entity != null) {
            if (ModConfig.INSTANCE.getShowHorizon()) {
                HorizonLineWidget.render(matrices, scaledWidth, scaledHeight,
                        rollEntity.dragon_barrel_roll$getRoll(tickDelta), entity.getViewVector(tickDelta).x);
            }

            if (ModConfig.INSTANCE.getMomentumBasedMouse() && ModConfig.INSTANCE.getShowMomentumWidget()) {
                // Momentum crosshair disabled in this version due to RollMouse interface removal.
                // The turn vector is private to MouseMixin and inaccessible from here.
            }
        }
    }
}
