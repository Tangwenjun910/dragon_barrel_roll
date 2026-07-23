package com.tangwenjun.dragonbarrelroll.api;

import com.tangwenjun.dragonbarrelroll.config.Sensitivity;

/**
 * Dragon BR's own roll interface — completely separate from DABR's RollEntity.
 * <p>
 * When DABR is also installed, its RollEntity interface coexists on Entity
 * without conflict because DragonRoll uses different method names.
 * Dragon BR accesses dragon roll state through this interface instead of
 * casting to DABR's RollEntity.
 */
public interface DragonRoll {
    void dragon_barrel_roll$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta);

    void dragon_barrel_roll$changeElytraLook(float pitch, float yaw, float roll);

    boolean dragon_barrel_roll$isRolling();

    void dragon_barrel_roll$setRolling(boolean rolling);

    float dragon_barrel_roll$getRoll();

    float dragon_barrel_roll$getRoll(float tickDelta);

    void dragon_barrel_roll$setRoll(float roll);
}
