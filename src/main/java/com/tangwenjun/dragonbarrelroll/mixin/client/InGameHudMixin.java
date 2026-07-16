package com.tangwenjun.dragonbarrelroll.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.tangwenjun.dragonbarrelroll.EventCallbacksClient;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Inject(
            method = "extractCrosshair",
            at = @At("HEAD")
    )
    private void doABarrelRoll$renderAdditionalCrosshairComponents(
            GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci,
            @Share("crosshair_offset") LocalRef<Vector2i> crosshairOffset) {
        crosshairOffset.set(EventCallbacksClient.onRenderCrosshair(
                context, tickCounter, context.guiWidth(), context.guiHeight()));
    }

    @ModifyArgs(
            method = "extractCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
            )
    )
    private void doABarrelRoll$moveCrosshair(Args args,
                                              @Share("crosshair_offset") LocalRef<Vector2i> crosshairOffset) {
        var offset = crosshairOffset.get();
        if (offset != null) {
            args.set(2, (int) args.get(2) + offset.x);
            args.set(3, (int) args.get(3) + offset.y);
        }
    }

    @ModifyArgs(
            method = "extractCrosshair",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    private void doABarrelRoll$moveCrosshair2(Args args,
                                               @Share("crosshair_offset") LocalRef<Vector2i> crosshairOffset) {
        var offset = crosshairOffset.get();
        if (offset != null) {
            args.set(2, (int) args.get(2) + offset.x);
            args.set(3, (int) args.get(3) + offset.y);
        }
    }
}
