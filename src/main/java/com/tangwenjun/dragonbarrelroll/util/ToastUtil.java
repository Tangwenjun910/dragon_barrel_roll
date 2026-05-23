package com.tangwenjun.dragonbarrelroll.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class ToastUtil {
    public static void toasty(String key) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.translatable("toast.dragon_barrel_roll"),
                Component.translatable("toast.dragon_barrel_roll." + key)
        ));
    }
}
