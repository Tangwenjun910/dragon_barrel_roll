package com.tangwenjun.dragonbarrelroll.mixin.client.roll;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin {

    /**
     * Local player actively barrel rolling (elytra branch): replace the elytra
     * roll quaternion with our own.
     */
    @ModifyArg(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private Quaternionf doABarrelRoll$modifyRoll(Quaternionf original,
                                                 @Local(argsOnly = true) AbstractClientPlayer player,
                                                 @Local(argsOnly = true, ordinal = 2) float tickDelta) {
        var rollEntity = (RollEntity) player;

        if (rollEntity.doABarrelRoll$isRolling()) {
            var roll = rollEntity.doABarrelRoll$getRoll(tickDelta);
            return new Quaternionf().rotateY((float) Math.toRadians(roll));
        }

        return original;
    }

    /**
     * Rider sitting on a barrel-rolling dragon: inject AFTER {@code super.setupRotations}
     * in the else branch (ordinal 2).
     * <p>
     * Two-step correction:
     * 1. Align rider body yaw to dragon body yaw: {@code R_Y(riderBodyYaw − dragonBodyYaw)}
     * 2. Apply negated pitch/roll quaternion in the dragon's frame
     * <p>
     * Without step 1, the quaternion rotates around the rider's own axes (rotating
     * around "own facing direction") instead of the dragon's axes.
     */
    @Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
                     ordinal = 2,
                     shift = At.Shift.AFTER))
    private void doABarrelRoll$applyRiderTilt(AbstractClientPlayer player,
                                               PoseStack poseStack,
                                               float bob, float yBodyRot,
                                               float partialTick, float scale,
                                               CallbackInfo ci) {
        if (!player.isPassenger()) return;
        if (!(player.getVehicle() instanceof Player dragonPlayer)) return;
        if (ModConfig.INSTANCE.getUseVanillaVisuals()) return;

        // ── Get barrel roll state (with local-player fallback for zero latency) ──
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

        // ▶ Step 1: Align rider body yaw to dragon body yaw
        // yBodyRot param = rider's body yaw (since Player.shouldRiderSit()=false,
        // vanilla does NOT replace it with the dragon's). We must manually rotate
        // the pose stack so pitch/roll quaternion operates in the dragon's frame.
        float dragonBodyYaw = Mth.rotLerp(partialTick, dragonPlayer.yBodyRotO, dragonPlayer.yBodyRot);
        float yawCorrection = Mth.wrapDegrees(yBodyRot - dragonBodyYaw);
        poseStack.mulPose(Axis.YP.rotationDegrees(yawCorrection));

        // ▶ Step 2: Apply negated pitch/roll quaternion in dragon-aligned frame
        // q_xp(−pitch) × q_zp(−roll) — negated to cancel vanilla's R_Y(180°) sign flip
        poseStack.mulPose(new Quaternionf()
                .rotateX(-(float) Math.toRadians(syncedPitch))
                .rotateZ(-(float) Math.toRadians(syncedRoll)));

    }
}
