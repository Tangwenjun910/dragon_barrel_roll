package com.tangwenjun.dragonbarrelroll.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangwenjun.dragonbarrelroll.EventCallbacksClient;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "HEAD"
            )
    )
    private void doABarrelRoll$captureTickDelta(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        context.pose().pushPose();
        EventCallbacksClient.onRenderCrosshair(context, tickCounter, context.guiWidth(), context.guiHeight());
    }

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "RETURN"
            )
    )
    private void doABarrelRoll$renderCrosshairReturn(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        context.pose().popPose();
    }
}
