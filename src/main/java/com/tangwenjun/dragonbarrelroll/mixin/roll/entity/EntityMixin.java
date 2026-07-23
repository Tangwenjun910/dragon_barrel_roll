package com.tangwenjun.dragonbarrelroll.mixin.roll.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import com.tangwenjun.dragonbarrelroll.api.DragonRoll;
import com.tangwenjun.dragonbarrelroll.config.Sensitivity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Provides default (no-op) implementations of {@link DragonRoll} for all entities.
 * Real implementations are in {@link PlayerEntityMixin} and {@link ClientPlayerEntityMixin}.
 * <p>
 * Does NOT implement DABR's RollEntity — DABR's own EntityMixin handles that interface.
 * Using separate method names (dragon_barrel_roll$ prefix instead of doABarrelRoll$)
 * avoids method override conflicts when both mods are installed.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements DragonRoll {
    @Shadow public abstract float getXRot();
    @Shadow public abstract float getYRot();
    @Shadow public abstract void setXRot(float pitch);
    @Shadow public abstract void setYRot(float yaw);
    @Shadow public abstract void turn(double cursorDeltaX, double cursorDeltaY);
    @Shadow public abstract Vec3 getLookAngle();

    @Override
    public void dragon_barrel_roll$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta) {
    }

    @Override
    public void dragon_barrel_roll$changeElytraLook(float pitch, float yaw, float roll) {
    }

    @Override
    public boolean dragon_barrel_roll$isRolling() {
        return false;
    }

    @Override
    public void dragon_barrel_roll$setRolling(boolean rolling) {
    }

    @Override
    public float dragon_barrel_roll$getRoll() {
        return 0;
    }

    @Override
    public float dragon_barrel_roll$getRoll(float tickDelta) {
        return 0;
    }

    @Override
    public void dragon_barrel_roll$setRoll(float roll) {
    }
}
