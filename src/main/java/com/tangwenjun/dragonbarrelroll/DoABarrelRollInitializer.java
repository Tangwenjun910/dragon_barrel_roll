package com.tangwenjun.dragonbarrelroll;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import com.tangwenjun.dragonbarrelroll.net.NetworkHandler;

@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer(IEventBus modEventBus, ModContainer container) {
        DoABarrelRoll.init();

        // Register networking
        NetworkHandler.register(modEventBus);

        if (FMLEnvironment.getDist().isClient()) {
            DoABarrelRollClient.init(container);
        }

        // 注册客户端配置文件
        container.registerConfig(ModConfig.Type.CLIENT, com.tangwenjun.dragonbarrelroll.config.ModConfig.SPEC);
    }
}
