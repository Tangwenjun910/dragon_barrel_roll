package com.tangwenjun.dragonbarrelroll;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.FlightData;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SmoothDouble;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import com.tangwenjun.dragonbarrelroll.api.event.RollEvents;
import com.tangwenjun.dragonbarrelroll.api.event.RollGroup;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import com.tangwenjun.dragonbarrelroll.config.ModConfigScreen;
import com.tangwenjun.dragonbarrelroll.flight.RotationModifiers;

public class DoABarrelRollClient {
    public static final SmoothDouble PITCH_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble YAW_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble ROLL_SMOOTHER = new SmoothDouble();
    public static final RollGroup FALL_FLYING_GROUP = RollGroup.of(DoABarrelRoll.id("fall_flying"));

    public static void init(ModContainer container) {
        FALL_FLYING_GROUP.trueIf(DoABarrelRollClient::isFallFlying);

        // Keyboard modifiers
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                .useModifier(RotationModifiers.buttonControls(1800)),
                2000, FALL_FLYING_GROUP);

        // Mouse modifiers, including swapping axes
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                .useModifier(ModConfig.INSTANCE::configureRotation),
                1000, FALL_FLYING_GROUP);

        // Generic movement modifiers, banking and such
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                .useModifier(RotationModifiers::applyControlSurfaceEfficacy, ModConfig.INSTANCE::getSimulateControlSurfaceEfficacy)
                .useModifier(RotationModifiers.smoothing(
                        PITCH_SMOOTHER, YAW_SMOOTHER, ROLL_SMOOTHER,
                        ModConfig.INSTANCE.getSmoothing()
                ))
                .useModifier(RotationModifiers::banking, ModConfig.INSTANCE::getEnableBanking)
                .useModifier(RotationModifiers::reorient, ModConfig.INSTANCE::getAutomaticRighting),
                1000, FALL_FLYING_GROUP);

        // Register client tick event on NeoForge
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> {
            EventCallbacksClient.clientTick(Minecraft.getInstance());
        });

        // Register custom config screen (client-only, safe from server classloading)
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (modContainer, screen) -> new ModConfigScreen(screen)
        );
    }

    public static void clearValues() {
        PITCH_SMOOTHER.reset();
        YAW_SMOOTHER.reset();
        ROLL_SMOOTHER.reset();
    }

    public static boolean isFallFlying() {
        if (!ModConfig.INSTANCE.getModEnabled()) {
            return false;
        }

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (ModConfig.INSTANCE.getDisableWhenSubmerged() && player.isUnderWater()) {
            return false;
        }

        // Dragon flight detection
        if (!DragonStateProvider.isDragon(player)) {
            return false;
        }

        FlightData flightData = FlightData.getData(player);
        boolean isDragonFlying = flightData != null && flightData.isWingsSpread() && flightData.hasFlight() && !player.onGround();
        if (!isDragonFlying) {
            return false;
        }

        boolean isGliding = ServerFlightHandler.isGliding(player);
        boolean isHovering = !isGliding;

        if ((isHovering && ModConfig.INSTANCE.enableHoverRoll.get()) ||
            (isGliding && ModConfig.INSTANCE.enableGlideRoll.get())) {
            return true;
        }
        return false;
    }

    public static boolean isConnectedToRealms() {
        return false; // We are not connected to realms.
    }
}
