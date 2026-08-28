package com.tangwenjun.dragonbarrelroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import com.tangwenjun.dragonbarrelroll.api.key.InputContext;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@EventBusSubscriber(modid = DoABarrelRoll.MODID, value = Dist.CLIENT)
public class ModKeybindings {

    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(net.minecraft.resources.Identifier.fromNamespaceAndPath(DoABarrelRoll.MODID, "category"));
    private static final KeyMapping.Category CATEGORY_MOVEMENT = new KeyMapping.Category(net.minecraft.resources.Identifier.fromNamespaceAndPath(DoABarrelRoll.MODID, "category_movement"));

    public static final KeyMapping TOGGLE_ENABLED = new KeyMapping(
            "key.dragon_barrel_roll.toggle_enabled",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            CATEGORY
    );
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.dragon_barrel_roll.open_config",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );

    public static final KeyMapping PITCH_UP = new KeyMapping(
            "key.dragon_barrel_roll.pitch_up",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY_MOVEMENT
    );
    public static final KeyMapping PITCH_DOWN = new KeyMapping(
            "key.dragon_barrel_roll.pitch_down",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY_MOVEMENT
    );
    public static final KeyMapping YAW_LEFT = new KeyMapping(
            "key.dragon_barrel_roll.yaw_left",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A,
            CATEGORY_MOVEMENT
    );
    public static final KeyMapping YAW_RIGHT = new KeyMapping(
            "key.dragon_barrel_roll.yaw_right",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_D,
            CATEGORY_MOVEMENT
    );
    public static final KeyMapping ROLL_LEFT = new KeyMapping(
            "key.dragon_barrel_roll.roll_left",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY_MOVEMENT
    );
    public static final KeyMapping ROLL_RIGHT = new KeyMapping(
            "key.dragon_barrel_roll.roll_right",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY_MOVEMENT
    );

    public static final List<KeyMapping> ALL = List.of(
            TOGGLE_ENABLED,
            OPEN_CONFIG,
            PITCH_UP,
            PITCH_DOWN,
            YAW_LEFT,
            YAW_RIGHT,
            ROLL_LEFT,
            ROLL_RIGHT
    );

    public static final InputContext CONTEXT = InputContext.of(
            DoABarrelRoll.id("fall_flying"),
            DoABarrelRollClient.FALL_FLYING_GROUP
    );

    static {
        CONTEXT.addKeyBinding(PITCH_UP);
        CONTEXT.addKeyBinding(PITCH_DOWN);
        CONTEXT.addKeyBinding(YAW_LEFT);
        CONTEXT.addKeyBinding(YAW_RIGHT);
        CONTEXT.addKeyBinding(ROLL_LEFT);
        CONTEXT.addKeyBinding(ROLL_RIGHT);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.registerCategory(CATEGORY_MOVEMENT);

        for (KeyMapping keyMapping : ALL) {
            event.register(keyMapping);
        }
    }

    public static void clientTick(Minecraft client) {
        while (TOGGLE_ENABLED.consumeClick()) {
            ModConfig.INSTANCE.setModEnabled(!ModConfig.INSTANCE.getModEnabled());

            if (client.player != null) {
                client.gui.setOverlayMessage(
                        Component.translatable(
                                "key.dragon_barrel_roll." +
                                        (ModConfig.INSTANCE.getModEnabled() ? "toggle_enabled.enable" : "toggle_enabled.disable")
                        ),
                        false
                );
            }
        }
        while (OPEN_CONFIG.consumeClick()) {
            client.setScreen(new com.tangwenjun.dragonbarrelroll.config.ModConfigScreen(client.screen));
        }
    }
}

