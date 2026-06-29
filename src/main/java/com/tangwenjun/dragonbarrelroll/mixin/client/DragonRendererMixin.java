package com.tangwenjun.dragonbarrelroll.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.FlightData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import com.tangwenjun.dragonbarrelroll.util.MountingBoneTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@Mixin(DragonRenderer.class)
public class DragonRendererMixin {

    @Inject(method = "setupRender", at = @At("HEAD"), remap = false)
    private void applyDaBRFullFlight(DragonEntity dragon, Player player, PoseStack poseStack, float partialTick, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableMod.get()) return;
        if (ModConfig.INSTANCE.getUseVanillaVisuals() && !player.getPassengers().isEmpty()) return;

        if (player instanceof LocalPlayer localPlayer) {
            // === Local player: read live roll/pitch from RollEntity interface ===
            if (!DragonStateProvider.isDragon(localPlayer)) {
                return;
            }

            FlightData data = FlightData.getData(localPlayer);
            if (data == null || !data.isWingsSpread() || !data.hasFlight() || localPlayer.onGround()) {
                return;
            }

            if (ModConfig.INSTANCE.syncRoll.get()) {
                float rollDeg = ((RollEntity) localPlayer).doABarrelRoll$getRoll(partialTick);
                dragon.prevZRot = (float) Math.toRadians(rollDeg);
            }

            if (ModConfig.INSTANCE.syncPitch.get()) {
                dragon.prevXRot = -localPlayer.getXRot();
            }
        } else {
            // === Remote player: read from synced barrel roll data (stored independently from DS MovementData) ===
            int pid = player.getId();
            float syncedRoll = SyncDragonRoll.getSyncedRollDeg(pid);
            float syncedPitch = SyncDragonRoll.getSyncedPitch(pid);

            if (syncedRoll == 0f && syncedPitch == 0f) {
                return;  // No barrel roll data synced for this player
            }

            if (ModConfig.INSTANCE.syncRoll.get()) {
                dragon.prevZRot = (float) Math.toRadians(syncedRoll);  // syncedRoll is in degrees
            }
            if (ModConfig.INSTANCE.syncPitch.get()) {
                dragon.prevXRot = -syncedPitch;  // negate pitch to match DS convention
            }
        }
    }

    /**
     * Captures the MountingBone's entity-relative world position after rendering.
     * Uses DragonEntity.position() (not player.position()) to match GeckoLib's
     * rendering coordinate space exactly.
     */
    @Inject(method = "postRender", at = @At("TAIL"), remap = false)
    private void captureMountingBone(PoseStack poseStack, DragonEntity animatable, BakedGeoModel model,
                                     MultiBufferSource bufferSource, VertexConsumer buffer,
                                     boolean isReRender, float partialTick, int packedLight,
                                     int packedOverlay, int color, CallbackInfo ci) {
        model.getBone("MountingBone").ifPresent(bone -> {
            var worldPos = bone.getWorldPosition();
            // Use DragonEntity position (= render position) to stay in GeckoLib coordinate space
            Vec3 entityPos = animatable.position();
            Vec3 rel = new Vec3(worldPos.x() - entityPos.x, worldPos.y() - entityPos.y, worldPos.z() - entityPos.z);
            Player player = animatable.getPlayer();
            if (player != null) {
                MountingBoneTracker.store(player.getId(), rel);
            }
        });
    }

}
