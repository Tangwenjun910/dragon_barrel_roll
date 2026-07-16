package com.tangwenjun.dragonbarrelroll.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin accessor for DragonRenderer.DragonRenderData private fields.
 * Exposes prevZRot and prevXRot for barrel roll tilt injection.
 */
@Mixin(DragonRenderer.DragonRenderData.class)
public interface DragonRenderDataAccessor {

    @Accessor
    void setPrevZRot(float value);

    @Accessor
    void setPrevXRot(float value);

    @Accessor
    float getPrevZRot();

    @Accessor
    float getPrevXRot();
}
