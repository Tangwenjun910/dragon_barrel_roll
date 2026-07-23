package com.tangwenjun.dragonbarrelroll.mixin.roll.entity;

import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stores Dragon BR's roll state on Player with UNIQUE field names
 * that do NOT conflict with DABR's fields (isRolling/prevRoll/roll).
 * <p>
 * Implements {@link com.tangwenjun.dragonbarrelroll.api.DragonRoll} using
 * the renamed fields. DABR's RollEntity interface is completely separate.
 */
@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Unique
    protected boolean dragon_barrel_roll$isRolling;
    @Unique
    protected float dragon_barrel_roll$prevRoll;
    @Unique
    protected float dragon_barrel_roll$roll;

    private static final Logger LOGGER = LoggerFactory.getLogger("dragon_barrel_roll");

    @Override
    protected void dragon_barrel_roll$baseTickTail(CallbackInfo ci) {
        dragon_barrel_roll$baseTickTail2();

        dragon_barrel_roll$prevRoll = dragon_barrel_roll$getRoll();

        if (!dragon_barrel_roll$isRolling()) {
            dragon_barrel_roll$setRoll(0.0f);
        }
    }

    @Unique
    protected void dragon_barrel_roll$baseTickTail2() {
    }

    @Override
    public boolean dragon_barrel_roll$isRolling() {
        return dragon_barrel_roll$isRolling;
    }

    @Override
    public void dragon_barrel_roll$setRolling(boolean rolling) {
        dragon_barrel_roll$isRolling = rolling;
    }

    @Override
    public float dragon_barrel_roll$getRoll() {
        return dragon_barrel_roll$roll;
    }

    @Override
    public float dragon_barrel_roll$getRoll(float tickDelta) {
        if (tickDelta == 1.0f) {
            return dragon_barrel_roll$getRoll();
        }
        return Mth.lerp(tickDelta, dragon_barrel_roll$prevRoll, dragon_barrel_roll$getRoll());
    }

    @Override
    public void dragon_barrel_roll$setRoll(float roll) {
        if (!Float.isFinite(roll)) {
            LOGGER.error("Invalid entity rotation: " + roll + ", discarding.");
            return;
        }
        var lastRoll = dragon_barrel_roll$getRoll();
        this.dragon_barrel_roll$roll = roll;

        if (roll < -90 && lastRoll > 90) {
            dragon_barrel_roll$prevRoll -= 360;
        } else if (roll > 90 && lastRoll < -90) {
            dragon_barrel_roll$prevRoll += 360;
        }
    }
}
