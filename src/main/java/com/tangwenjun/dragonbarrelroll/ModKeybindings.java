package com.tangwenjun.dragonbarrelroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import com.tangwenjun.dragonbarrelroll.api.key.InputContext;
import com.tangwenjun.dragonbarrelroll.config.ModConfig;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModKeybindings {

    public static final KeyMapping TOGGLE_ENABLED = new KeyMapping(
            "key.do_a_barrel_roll.toggle_enabled",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "category.do_a_barrel_roll.do_a_barrel_roll"
    );
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.do_a_barrel_roll.open_config",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "category.do_a_barrel_roll.do_a_barrel_roll"
    );

    public static final KeyMapping PITCH_UP = new KeyMapping(
            "key.do_a_barrel_roll.pitch_up",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyMapping PITCH_DOWN = new KeyMapping(
            "key.do_a_barrel_roll.pitch_down",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyMapping YAW_LEFT = new KeyMapping(
            "key.do_a_barrel_roll.yaw_left",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A,
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyMapping YAW_RIGHT = new KeyMapping(
            "key.do_a_barrel_roll.yaw_right",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_D,
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyMapping ROLL_LEFT = new KeyMapping(
            "key.do_a_barrel_roll.roll_left",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyMapping ROLL_RIGHT = new KeyMapping(
            "key.do_a_barrel_roll.roll_right",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
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

    public static void clientTick(Minecraft client) {
        while (TOGGLE_ENABLED.consumeClick()) {
            ModConfig.INSTANCE.setModEnabled(!ModConfig.INSTANCE.getModEnabled());

            if (client.player != null) {
                client.player.displayClientMessage(
                        Component.translatable(
                                "key.do_a_barrel_roll." +
                                        (ModConfig.INSTANCE.getModEnabled() ? "toggle_enabled.enable" : "toggle_enabled.disable")
                        ),
                        true
                );
            }
        }
        while (OPEN_CONFIG.consumeClick()) {
            client.setScreen(new com.tangwenjun.dragonbarrelroll.config.ModConfigScreen(client.screen));
        }
    }
}
