package com.tangwenjun.dragonbarrelroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.api.RollMouse;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.impl.key.InputContextImpl;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import com.tangwenjun.dragonbarrelroll.render.HorizonLineWidget;
import com.tangwenjun.dragonbarrelroll.render.MomentumCrosshairWidget;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.joml.Vector2d;
import org.joml.Vector2i;

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

    private static void syncBarrelRollData(Minecraft client) {
        boolean flying = DoABarrelRollClient.isFallFlying();

        if (!flying) {
            if (wasFlying && (lastSyncedRoll != 0 || lastSyncedPitch != 0)) {
                var player = client.player;
                if (player != null) {
                    ClientPacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), 0, 0, 0));
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

        float roll = ((RollEntity) player).doABarrelRoll$getRoll();
        float pitch = player.getXRot();
        float yaw = player.getYRot();

        lastSyncedRoll = roll;
        lastSyncedPitch = pitch;
        ClientPacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), roll, pitch, yaw));
    }

    public static Vector2i onRenderCrosshair(GuiGraphicsExtractor context, DeltaTracker tickCounter, int scaledWidth, int scaledHeight) {
        if (!DoABarrelRollClient.isFallFlying()) return new Vector2i(0, 0);
        var tickDelta = tickCounter.getRealtimeDeltaTicks();

        var entity = Minecraft.getInstance().getCameraEntity();
        if (entity != null) {
            var rollEntity = (RollEntity) entity;
            if (ModConfig.INSTANCE.getShowHorizon()) {
                HorizonLineWidget.render(context, scaledWidth, scaledHeight,
                        rollEntity.doABarrelRoll$getRoll(tickDelta), entity.getViewXRot(tickDelta));
            }

            if (ModConfig.INSTANCE.getMomentumBasedMouse() && ModConfig.INSTANCE.getShowMomentumWidget()) {
                var rollMouse = (RollMouse) Minecraft.getInstance().mouseHandler;

                return MomentumCrosshairWidget.render(context, scaledWidth, scaledHeight, new Vector2d(rollMouse.doABarrelRoll$getMouseTurnVec()));
            }
        }

        return new Vector2i(0, 0);
    }
}
