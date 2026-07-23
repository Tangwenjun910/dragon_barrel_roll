package com.tangwenjun.dragonbarrelroll.mixin.client.roll;

import com.tangwenjun.dragonbarrelroll.api.DragonRoll;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private Entity entity;
    @Shadow private float roll;

    @Unique
    private boolean dragon_barrel_roll$isRolling;
    @Unique
    private float dragon_barrel_roll$lastRollBack;
    @Unique
    private float dragon_barrel_roll$rollBack;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void dragon_barrel_roll$interpolateRollnt(CallbackInfo ci) {
        if (entity != null && !((DragonRoll) entity).dragon_barrel_roll$isRolling()) {
            dragon_barrel_roll$lastRollBack = dragon_barrel_roll$rollBack;
            dragon_barrel_roll$rollBack -= dragon_barrel_roll$rollBack * 0.5f;
        }
    }

    @Inject(
            method = "setup",
            at = @At("HEAD")
    )
    private void dragon_barrel_roll$captureTickDeltaAndUpdate(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci, @Share("tickDelta") LocalFloatRef tickDeltaRef) {
        tickDeltaRef.set(tickDelta);
        dragon_barrel_roll$isRolling = entity != null && ((DragonRoll) entity).dragon_barrel_roll$isRolling();
    }

    @Inject(
            method = "setup",
            at = @At("TAIL")
    )
    private void dragon_barrel_roll$updateRollBack(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (dragon_barrel_roll$isRolling) {
            dragon_barrel_roll$rollBack = roll;
            dragon_barrel_roll$lastRollBack = roll;
        }
    }

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FFF)V",
                    ordinal = 0
            ),
            index = 2
    )
    private float dragon_barrel_roll$addRoll2(float original, @Share("tickDelta") LocalFloatRef tickDelta) {
        if (dragon_barrel_roll$isRolling && entity != null) {
            return original + ((DragonRoll) entity).dragon_barrel_roll$getRoll(tickDelta.get());
        } else {
            return original + Mth.lerp(tickDelta.get(), dragon_barrel_roll$lastRollBack, dragon_barrel_roll$rollBack);
        }
    }

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FFF)V",
                    ordinal = 1
            ),
            index = 2
    )
    private float dragon_barrel_roll$addRoll3(float original, @Share("tickDelta") LocalFloatRef tickDelta) {
        if (dragon_barrel_roll$isRolling && entity != null) {
            return original - ((DragonRoll) entity).dragon_barrel_roll$getRoll(tickDelta.get());
        } else {
            return original - Mth.lerp(tickDelta.get(), dragon_barrel_roll$lastRollBack, dragon_barrel_roll$rollBack);
        }
    }
}
