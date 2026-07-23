package com.tangwenjun.dragonbarrelroll;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;

@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer(ModContainer container) {
        DoABarrelRoll.init();

        if (FMLLoader.getDist().isClient()) {
            DoABarrelRoll.LOGGER.info("Dragon Barrel Roll initializing (NeoForge 1.21.1)...");
            DoABarrelRollClient.init(container);
        }

        // 注册客户端配置文件
        container.registerConfig(ModConfig.Type.CLIENT, com.tangwenjun.dragonbarrelroll.config.ModConfig.SPEC);
    }
}
