package com.tangwenjun.dragonbarrelroll;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;

@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer(ModContainer container) {
        DoABarrelRoll.init();

        // 注册客户端配置文件（服务端不会报错，NeoForge 会忽略客户端配置）
        container.registerConfig(ModConfig.Type.CLIENT, com.tangwenjun.dragonbarrelroll.config.ModConfig.SPEC);

        if (FMLLoader.getDist().isClient()) {
            DoABarrelRollClient.init(container);
        }
    }
}
