package com.tangwenjun.dragonbarrelroll.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import com.tangwenjun.dragonbarrelroll.DoABarrelRoll;

@EventBusSubscriber(modid = DoABarrelRoll.MODID, value = Dist.CLIENT)
public class RenderHelper {
    public static RenderPipeline INVERTED;

    @SubscribeEvent
    public static void registerPipelines(RegisterRenderPipelinesEvent event) {
        INVERTED = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(DoABarrelRoll.id("pipeline/crosshair"))
                .withColorTargetState(new ColorTargetState(new BlendFunction(
                        SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR,
                        SourceFactor.ONE, DestFactor.ZERO)))
                .build();
        event.registerPipeline(INVERTED);
    }

    public static BiConsumer<Integer, Integer> blankPixel(GuiGraphicsExtractor drawContext) {
        return (x, y) -> {
            if (INVERTED != null) {
                drawContext.fill(INVERTED, x, y, x + 1, y + 1, 0xffffffff);
            } else {
                drawContext.fill(x, y, x + 1, y + 1, 0xffffffff);
            }
        };
    }
}
