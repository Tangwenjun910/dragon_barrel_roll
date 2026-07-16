package com.tangwenjun.dragonbarrelroll.mixin.client.roll;

import com.tangwenjun.dragonbarrelroll.api.RollCamera;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements RollCamera {
    @Shadow private Entity entity;
    @Shadow private float roll;

    @Unique
    private boolean isRolling;
    @Unique
    private float lastRollBack;
    @Unique
    private float rollBack;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void doABarrelRoll$interpolateRollnt(CallbackInfo ci) {
        if (entity != null && !((RollEntity) entity).doABarrelRoll$isRolling()) {
            lastRollBack = rollBack;
            rollBack -= rollBack * 0.5f;
        }
    }

    @Inject(
            method = "alignWithEntity",
            at = @At("HEAD")
    )
    private void doABarrelRoll$captureState(float tickDelta, CallbackInfo ci) {
        isRolling = entity != null && ((RollEntity) entity).doABarrelRoll$isRolling();
    }

    @Inject(
            method = "alignWithEntity",
            at = @At("TAIL")
    )
    private void doABarrelRoll$updateRollBack(float tickDelta, CallbackInfo ci) {
        if (isRolling) {
            rollBack = roll;
            lastRollBack = roll;
        }
    }

    @ModifyArg(
            method = "alignWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FFF)V",
                    ordinal = 0
            ),
            index = 2
    )
    private float doABarrelRoll$addRoll2(float original) {
        if (isRolling && entity != null) {
            return original + ((RollEntity) entity).doABarrelRoll$getRoll(1.0f);
        } else {
            return original + Mth.lerp(1.0f, lastRollBack, rollBack);
        }
    }

    @ModifyArg(
            method = "alignWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FFF)V",
                    ordinal = 1
            ),
            index = 2
    )
    private float doABarrelRoll$addRoll3(float original) {
        if (isRolling && entity != null) {
            return original - ((RollEntity) entity).doABarrelRoll$getRoll(1.0f);
        } else {
            return original - Mth.lerp(1.0f, lastRollBack, rollBack);
        }
    }

    @Override
    public float doABarrelRoll$getRoll() {
        return roll;
    }
}
