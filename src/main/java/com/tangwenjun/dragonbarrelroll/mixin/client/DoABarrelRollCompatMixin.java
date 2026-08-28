package com.tangwenjun.dragonbarrelroll.mixin.client;

import com.tangwenjun.dragonbarrelroll.DoABarrelRollClient;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.net.SyncDragonRoll;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes DragonSurvival treat Dragon Barrel Roll as the barrel roll provider for dragons.
 * <p>
 * This is required even when the original Do A Barrel Roll mod is not installed:
 * DragonSurvival 26.1 gates {@code DoABarrelRollCompat#isActive} behind
 * {@code ModID.DO_A_BARREL_ROLL.isLoaded()}. Dragon Barrel Roll supplies its own
 * roll state, so it must answer for that compatibility point itself.
 * <p>
 * The target is referenced by name so the mod still compiles against older
 * DragonSurvival 26.1 jars that do not yet contain this compatibility class.
 */
@Mixin(targets = "by.dragonsurvivalteam.dragonsurvival.compat.do_a_barrel_roll.DoABarrelRollCompat")
public abstract class DoABarrelRollCompatMixin {

    @Inject(
            method = "shouldEnableDragonFlight",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void dragon_barrel_roll$blockDabrDragonFlight(Player player, CallbackInfoReturnable<Boolean> cir) {
        // Dragon Barrel Roll owns dragon flight. The original DABR must never be told
        // that a dragon is elytra-flying, otherwise it would try to take over.
        cir.setReturnValue(false);
    }

    @Inject(
            method = "isActive",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void dragon_barrel_roll$activeForDragonBR(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (isDragonBarrelRollActive(player)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "getRollRadians",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void dragon_barrel_roll$rollForDragonBR(Player player, float partialTick, CallbackInfoReturnable<Float> cir) {
        if (isDragonBarrelRollActive(player)) {
            cir.setReturnValue(getDragonBarrelRollRadians(player, partialTick));
        }
    }

    private static boolean isDragonBarrelRollActive(Player player) {
        if (player == null) {
            return false;
        }

        if (player instanceof LocalPlayer) {
            return DoABarrelRollClient.isFallFlying();
        }

        int id = player.getId();
        boolean hasRoll = ModConfig.INSTANCE.syncRoll.get() && SyncDragonRoll.getSyncedRollDeg(id) != 0f;
        boolean hasPitch = ModConfig.INSTANCE.syncPitch.get() && SyncDragonRoll.getSyncedPitch(id) != 0f;
        return hasRoll || hasPitch;
    }

    private static float getDragonBarrelRollRadians(Player player, float partialTick) {
        if (player instanceof LocalPlayer) {
            return ((RollEntity) player).doABarrelRoll$getRoll(partialTick) * Mth.DEG_TO_RAD;
        }

        return SyncDragonRoll.getSyncedRollDeg(player.getId()) * Mth.DEG_TO_RAD;
    }
}
