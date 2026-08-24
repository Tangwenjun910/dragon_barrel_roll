package com.tangwenjun.dragonbarrelroll.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.compat.do_a_barrel_roll.DoABarrelRollCompat;
import com.tangwenjun.dragonbarrelroll.DoABarrelRollClient;
import com.tangwenjun.dragonbarrelroll.api.DragonRoll;
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
 * - {@link DoABarrelRollCompat#shouldEnableDragonFlight} is forced to {@code false} so the original
 *   Do a Barrel Roll mod never takes over dragon flight.
 * - {@link DoABarrelRollCompat#isActive} returns {@code true} when Dragon Barrel Roll is controlling
 *   the current dragon (local player or a remote player with synced roll data).
 * - {@link DoABarrelRollCompat#getRollRadians} returns Dragon Barrel Roll's roll value.
 */
@Mixin(DoABarrelRollCompat.class)
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

        // Remote players: only take over when we actually have synced roll/pitch data
        // and the corresponding model-sync option is enabled. Otherwise let
        // DragonSurvival use its own default flight visuals.
        int id = player.getId();
        boolean hasRoll = ModConfig.INSTANCE.syncRoll.get() && SyncDragonRoll.getSyncedRollDeg(id) != 0f;
        boolean hasPitch = ModConfig.INSTANCE.syncPitch.get() && SyncDragonRoll.getSyncedPitch(id) != 0f;
        return hasRoll || hasPitch;
    }

    private static float getDragonBarrelRollRadians(Player player, float partialTick) {
        if (player instanceof LocalPlayer) {
            return ((DragonRoll) player).dragon_barrel_roll$getRoll(partialTick) * Mth.DEG_TO_RAD;
        }

        return SyncDragonRoll.getSyncedRollDeg(player.getId()) * Mth.DEG_TO_RAD;
    }
}
