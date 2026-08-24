package com.tangwenjun.dragonbarrelroll.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.compat.do_a_barrel_roll.DoABarrelRollCompat;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

    /**
     * DragonSurvival only applies the DABR-style pitch/roll while gliding.
     * Since Dragon Barrel Roll also supports hover, allow the same branch to run
     * whenever our roll system is active.
     */
    @ModifyExpressionValue(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lby/dragonsurvivalteam/dragonsurvival/server/handlers/ServerFlightHandler;isGliding(Lnet/minecraft/world/entity/player/Player;)Z",
                    remap = false
            ),
            remap = false
    )
    private boolean dragon_barrel_roll$allowHoverRoll(boolean original, @Local(argsOnly = true) Player player) {
        return original || DoABarrelRollCompat.isActive(player);
    }

    /**
     * Respect the "sync pitch to dragon model" option and feed the correct pitch
     * into DragonSurvival's DABR-style branch.
     * <p>
     * setupRender does '-player.getViewXRot(partialTick)', so:
     * - local player: original view pitch is already the barrel-roll pitch
     * - remote player: use the synced pitch so other players see the same tilt
     * - when syncPitch is disabled: keep DragonSurvival's own pitch value
     */
    @ModifyExpressionValue(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getViewXRot(F)F"
            ),
            remap = false
    )
    private float dragon_barrel_roll$respectSyncPitch(float original,
                                                      @Local(argsOnly = true) DragonEntity dragon,
                                                      @Local(argsOnly = true) Player player) {
        if (!ModConfig.INSTANCE.syncPitch.get()) {
            return -dragon.prevXRot;
        }

        if (player instanceof LocalPlayer) {
            return original;
        }

        return SyncDragonRoll.getSyncedPitch(player.getId());
    }

    /**
     * Respect the "sync roll to dragon model" option. When disabled, keep using
     * DragonSurvival's own roll value instead of the barrel-roll roll.
     */
    @ModifyExpressionValue(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lby/dragonsurvivalteam/dragonsurvival/compat/do_a_barrel_roll/DoABarrelRollCompat;getRollRadians(Lnet/minecraft/world/entity/player/Player;F)F",
                    remap = false
            ),
            remap = false
    )
    private float dragon_barrel_roll$respectSyncRoll(float original,
                                                     @Local(argsOnly = true) DragonEntity dragon) {
        if (!ModConfig.INSTANCE.syncRoll.get()) {
            return dragon.prevZRot;
        }
        return original;
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
