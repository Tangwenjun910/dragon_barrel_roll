package com.tangwenjun.dragonbarrelroll;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import com.tangwenjun.dragonbarrelroll.config.ModConfigScreen;

@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer(ModContainer container) {
        DoABarrelRoll.init();

        if (FMLLoader.getDist().isClient()) {
            DoABarrelRollClient.init();
            // 注册自定义配置屏幕（仅客户端可用）
            container.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (modContainer, screen) -> new ModConfigScreen(screen)
            );
        }

        // 注册客户端配置文件
        container.registerConfig(ModConfig.Type.CLIENT, com.tangwenjun.dragonbarrelroll.config.ModConfig.SPEC);
    }
}
