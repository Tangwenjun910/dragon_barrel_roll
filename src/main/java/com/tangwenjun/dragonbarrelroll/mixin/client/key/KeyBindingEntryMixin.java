package com.tangwenjun.dragonbarrelroll.mixin.client.key;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.KeyMapping;
import com.tangwenjun.dragonbarrelroll.util.key.ContextualKeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow @Final private KeyMapping key;

    @ModifyExpressionValue(
            method = "refreshEntry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"
            )
    )
    private boolean doABarrelRoll$ignoreCertainKeyBindingConflicts(boolean original, @Local KeyMapping otherBinding) {
        var firstContexts = ((ContextualKeyBinding) key).doABarrelRoll$getContexts();
        var secondContexts = ((ContextualKeyBinding) otherBinding).doABarrelRoll$getContexts();

        // none + none -> original
        // none + has -> false
        // has + none -> false
        // has + has ->
        //   same context -> original
        //   different context -> false

        if (firstContexts.isEmpty() && secondContexts.isEmpty()) return original;
        if (firstContexts.isEmpty() || secondContexts.isEmpty()) return false;
        if (firstContexts.stream().anyMatch(secondContexts::contains)) return original;
        return false;
    }
}
