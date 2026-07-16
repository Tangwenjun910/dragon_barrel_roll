package com.tangwenjun.dragonbarrelroll.mixin.client.roll;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.api.RollRenderState;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin {

    /**
     * Player actively barrel rolling: replace the elytra roll quaternion.
     */
    @ModifyArg(
            method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private Quaternionfc doABarrelRoll$modifyRoll(Quaternionfc original,
                                                   @Local(argsOnly = true) AvatarRenderState state) {
        var rollState = (RollRenderState) state;

        if (rollState.doABarrelRoll$isRolling()) {
            var roll = rollState.doABarrelRoll$getRoll();
            return Axis.YP.rotationDegrees(roll);
        }

        return original;
    }

    /**
     * Rider sitting on a barrel-rolling dragon: apply pitch/roll tilt.
     * Injects AFTER the LivingEntityRenderer.setupRotations super call
     * in the else branch (ordinal 2).
     */
    @Inject(
            method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void doABarrelRoll$applyRiderTilt(AvatarRenderState state,
                                               PoseStack poseStack,
                                               float bob, float yBodyRot,
                                               CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        var entity = minecraft.level.getEntity(state.id);
        if (!(entity instanceof Player player)) return;
        if (!player.isPassenger()) return;
        if (!(player.getVehicle() instanceof Player dragonPlayer)) return;
        if (ModConfig.INSTANCE.getUseVanillaVisuals()) return;

        // Get barrel roll state (with local-player fallback for zero latency)
        float syncedRoll = SyncDragonRoll.getSyncedRollDeg(dragonPlayer.getId());
        float syncedPitch = SyncDragonRoll.getSyncedPitch(dragonPlayer.getId());
        if (dragonPlayer instanceof LocalPlayer local) {
            var rollEntity = (RollEntity) local;
            if (rollEntity.doABarrelRoll$isRolling()) {
                syncedRoll = rollEntity.doABarrelRoll$getRoll();
                syncedPitch = local.getXRot();
            }
        }

        if (syncedRoll == 0 && syncedPitch == 0) return;

        // Step 1: Align rider body yaw to dragon body yaw
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float dragonBodyYaw = Mth.rotLerp(partialTick, dragonPlayer.yBodyRotO, dragonPlayer.yBodyRot);
        float yawCorrection = Mth.wrapDegrees(yBodyRot - dragonBodyYaw);
        poseStack.mulPose(Axis.YP.rotationDegrees(yawCorrection));

        // Step 2: Apply negated pitch/roll quaternion in dragon-aligned frame
        poseStack.mulPose(new Quaternionf()
                .rotateX(-(float) Math.toRadians(syncedPitch))
                .rotateZ(-(float) Math.toRadians(syncedRoll)));
    }
}
