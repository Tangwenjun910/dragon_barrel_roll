package com.tangwenjun.dragonbarrelroll.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tangwenjun.dragonbarrelroll.DoABarrelRollClient;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonRenderer.class)
public class DragonRendererMixin {

    /**
     * HEAD injection: set barrel roll values into DragonRenderData
     * BEFORE DS reads them. DS 26.1.2 applies prevZRot (radians via ZP.rotation)
     * and prevXRot (degrees via XN.rotationDegrees) when gliding.
     *
     * This mirrors the old 1.21.1 approach: set DragonEntity.prevZRot/prevXRot
     * before setupRender, so DS reads our values.
     */
    @Inject(method = "setupRender", at = @At("HEAD"), remap = false)
    private void applyBarrelRollHead(DragonRenderer.DragonRenderData renderData, PoseStack pose, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableMod.get()) return;
        if (renderData == null) return;

        Player player = renderData.player();
        if (player == null) return;
        if (ModConfig.INSTANCE.getUseVanillaVisuals() && !player.getPassengers().isEmpty()) return;

        float pitchDeg;
        float rollRad;

        if (player instanceof LocalPlayer) {
            if (!DoABarrelRollClient.isFallFlying()) return;

            var rollEntity = (RollEntity) player;
            rollRad = ModConfig.INSTANCE.syncRoll.get()
                    ? (float) Math.toRadians(rollEntity.doABarrelRoll$getRoll()) : 0;
            pitchDeg = ModConfig.INSTANCE.syncPitch.get() ? -player.getXRot() : 0;
        } else {
            int pid = player.getId();
            rollRad = (float) Math.toRadians(SyncDragonRoll.getSyncedRollDeg(pid));
            pitchDeg = -SyncDragonRoll.getSyncedPitch(pid);
            if (rollRad == 0f && pitchDeg == 0f) return;
        }

        if (!ModConfig.INSTANCE.syncRoll.get()) rollRad = 0;
        if (!ModConfig.INSTANCE.syncPitch.get()) pitchDeg = 0;

        // Set directly — DS will read these in the gliding branch
        ((DragonRenderDataAccessor) (Object) renderData).setPrevZRot(rollRad);
        ((DragonRenderDataAccessor) (Object) renderData).setPrevXRot(pitchDeg);
    }

    /**
     * TAIL injection: DS skipped pitch/roll for hovering (isGliding=false).
     * Apply our own rotation so hovering barrel roll also tilts the model.
     */
    @Inject(method = "setupRender", at = @At("TAIL"), remap = false)
    private void applyBarrelRollTail(DragonRenderer.DragonRenderData renderData, PoseStack pose, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableMod.get()) return;
        if (renderData == null) return;

        Player player = renderData.player();
        if (player == null) return;

        // Only apply for hovering — DS already handled gliding via HEAD values
        if (ServerFlightHandler.isGliding(player)) return;
        if (player.isPassenger() && player.getVehicle() instanceof Player vehiclePlayer
                && ServerFlightHandler.isGliding(vehiclePlayer)) return;

        if (!(player instanceof LocalPlayer)) return;
        if (!DoABarrelRollClient.isFallFlying()) return;

        float rollDeg = ((RollEntity) player).doABarrelRoll$getRoll();
        float pitchDeg = -player.getXRot();

        if (rollDeg == 0 && pitchDeg == 0) return;

        if (ModConfig.INSTANCE.syncPitch.get()) {
            pose.mulPose(Axis.XN.rotationDegrees(pitchDeg));
        }
        if (ModConfig.INSTANCE.syncRoll.get()) {
            pose.mulPose(Axis.ZP.rotationDegrees(rollDeg));
        }
    }
}
