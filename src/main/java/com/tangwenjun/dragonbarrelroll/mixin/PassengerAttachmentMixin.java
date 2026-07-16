package com.tangwenjun.dragonbarrelroll.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes the passenger attachment point during a barrel roll.
 * <p>
 * <b>HEAD phase</b>: resets {@code MovementData.prevZRot} to 0 so
 * DragonSurvival's {@code @ModifyReturnValue} applies {@code zRot(0)}
 * (identity — no tilt interference from the previous render frame).
 * <p>
 * <b>RETURN phase</b>: applies the correct barrel roll z-rotation
 * ({@code zRot(-rollRad)}) on top of DragonSurvival's computed mounting
 * offset, scale, pitch, and body-yaw position.
 */
@Mixin(Entity.class)
public abstract class PassengerAttachmentMixin {

    @Inject(
            method = "getPassengerAttachmentPoint(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityDimensions;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD")
    )
    private void doABarrelRoll$clearPrevZRot(Entity passenger, EntityDimensions dimensions,
                                             float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        Entity mount = (Entity) (Object) this;
        if (!(mount instanceof Player dragonPlayer)) return;
        if (!DragonStateProvider.isDragon(dragonPlayer)) return;
        if (ModConfig.INSTANCE.getUseVanillaVisuals()) return;

        float rollDeg;
        if (dragonPlayer.level().isClientSide()) {
            rollDeg = ((RollEntity) dragonPlayer).doABarrelRoll$getRoll();
        } else {
            rollDeg = SyncDragonRoll.getSyncedRollDeg(dragonPlayer.getId());
        }

        // Only zero prevZRot when we'll apply our own barrel roll.
        // Otherwise preserve DragonSurvival's normal tilt behaviour.
        if (rollDeg != 0f) {
            MovementData movement = MovementData.getData(dragonPlayer);
            movement.prevZRot = 0;
        }
    }

    @ModifyReturnValue(
            method = "getPassengerAttachmentPoint(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityDimensions;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN")
    )
    private Vec3 doABarrelRoll$applyBarrelRoll(Vec3 original,
                                               @Local(argsOnly = true, index = 0) Entity passenger) {
        Entity mount = (Entity) (Object) this;
        if (!(mount instanceof Player dragonPlayer)) return original;
        if (!DragonStateProvider.isDragon(dragonPlayer)) return original;
        if (ModConfig.INSTANCE.getUseVanillaVisuals()) return original;
        if (!(passenger instanceof Player)) return original;
        if (mount.getFirstPassenger() == null) return original;

        float rollDeg;
        if (dragonPlayer.level().isClientSide()) {
            rollDeg = ((RollEntity) dragonPlayer).doABarrelRoll$getRoll();
        } else {
            rollDeg = SyncDragonRoll.getSyncedRollDeg(dragonPlayer.getId());
        }

        if (rollDeg == 0f) return original;

        float rollRad = (float) Math.toRadians(rollDeg);
        return original.zRot(-rollRad);
    }
}
