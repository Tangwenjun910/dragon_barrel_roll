package com.tangwenjun.dragonbarrelroll.mixin.client.roll;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import com.tangwenjun.dragonbarrelroll.api.RollEntity;
import com.tangwenjun.dragonbarrelroll.api.RollRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void updateRollState(Entity entity, EntityRenderState state, float tickDelta, CallbackInfo ci) {
        var rollEntity = (RollEntity) entity;
        var rollState = (RollRenderState) state;

        rollState.doABarrelRoll$setRolling(rollEntity.doABarrelRoll$isRolling());
        rollState.doABarrelRoll$setRoll(rollEntity.doABarrelRoll$getRoll(tickDelta));
    }
}
