package com.tangwenjun.dragonbarrelroll.mixin.client.key;

import com.tangwenjun.dragonbarrelroll.api.key.InputContext;
import com.tangwenjun.dragonbarrelroll.impl.key.InputContextImpl;
import com.tangwenjun.dragonbarrelroll.util.key.ContextualKeyBinding;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(KeyMapping.class)
public abstract class KeyBindingMixin implements ContextualKeyBinding {
    @Unique
    private final ArrayList<InputContext> contexts = new ArrayList<>();

    @Override
    public List<InputContext> doABarrelRoll$getContexts() {
        return contexts;
    }

    @Override
    public void doABarrelRoll$addToContext(InputContext context) {
        contexts.add(context);
    }

    private static KeyMapping getContextKeyBinding(InputConstants.Key key) {
        for (var context : InputContextImpl.getContexts()) {
            var binding = context.getKeyBinding(key);
            if (binding != null) {
                if (context.isActive()) {
                    return binding;
                } else {
                    binding.setDown(false);
                }
            }
        }

        return null;
    }

    @WrapOperation(
            method = "onKeyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0 // We let all these mixins fail if they need to as a temporary workaround to be compatible with Connector.
    )
    private static Object doABarrelRoll$applyKeybindContext(Map<InputConstants.Key, KeyMapping> map, Object key, Operation<KeyMapping> original) {
        var binding = getContextKeyBinding((InputConstants.Key) key);
        if (binding != null) return binding;

        return original.call(map, key);
    }

    @WrapOperation(
            method = "setKeyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object doABarrelRoll$applyKeybindContext2(Map<InputConstants.Key, KeyMapping> map, Object key, Operation<KeyMapping> original) {
        var binding = getContextKeyBinding((InputConstants.Key) key);
        var originalBinding = original.call(map, key);
        if (binding != null) {
            if (originalBinding != null) {
                originalBinding.setDown(false);
            }
            return binding;
        }

        return originalBinding;
    }

    @Inject(
            method = "updateKeysByCode",
            at = @At("HEAD"),
            require = 0
    )
    private static void doABarrelRoll$updateContextualKeys(CallbackInfo ci) {
        for (var context : InputContextImpl.getContexts()) {
            context.updateKeysByCode();
        }
    }

    @WrapOperation(
            method = "updateKeysByCode",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object doABarrelRoll$skipAddingContextualKeys(Map<InputConstants.Key, KeyMapping> map, Object key, Object value, Operation<Object> original) {
        if (InputContextImpl.contextsContain((KeyMapping) value)) {
            return null;
        }
        return original.call(map, key, value);
    }
}
