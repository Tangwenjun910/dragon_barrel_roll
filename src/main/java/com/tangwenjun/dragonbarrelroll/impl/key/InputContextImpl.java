package com.tangwenjun.dragonbarrelroll.impl.key;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import com.tangwenjun.dragonbarrelroll.api.key.InputContext;
import com.tangwenjun.dragonbarrelroll.mixin.client.key.KeyBindingAccessor;
import com.tangwenjun.dragonbarrelroll.util.key.ContextualKeyBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class InputContextImpl implements InputContext {
    private static final List<InputContextImpl> CONTEXTS = new ReferenceArrayList<>();

    public static List<InputContextImpl> getContexts() {
        return CONTEXTS;
    }

    public static boolean contextsContain(KeyMapping binding) {
        for (var context : InputContextImpl.getContexts()) {
            if (context.getKeyBindings().contains(binding)) {
                return true;
            }
        }

        return false;
    }

    private final ResourceLocation id;
    private final Supplier<Boolean> activeCondition;
    private final List<KeyMapping> keyBindings = new ReferenceArrayList<>();
    private final Map<InputConstants.Key, KeyMapping> bindingsByKey = new HashMap<>();
    private boolean active;

    public InputContextImpl(ResourceLocation id, Supplier<Boolean> activeCondition) {
        this.id = id;
        this.activeCondition = activeCondition;
        CONTEXTS.add(this);
    }

    public void tick() {
        boolean active = activeCondition.get();
        if (active != this.active) {
            this.active = active;
            // KeyMapping.tick() not available in Mojang - reset states manually
            keyBindings.forEach(kb -> kb.setDown(false));
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void addKeyBinding(KeyMapping KeyMapping) {
        Objects.requireNonNull(KeyMapping);
        keyBindings.add(KeyMapping);
        ((ContextualKeyBinding) KeyMapping).doABarrelRoll$addToContext(this);
    }

    @Override
    public List<KeyMapping> getKeyBindings() {
        return keyBindings;
    }

    @Override
    public KeyMapping getKeyBinding(InputConstants.Key key) {
        return bindingsByKey.get(key);
    }

    @Override
    public void updateKeysByCode() {
        bindingsByKey.clear();
        for (KeyMapping KeyMapping : keyBindings) {
            bindingsByKey.put(((KeyBindingAccessor) KeyMapping).getBoundKey(), KeyMapping);
        }
    }
}
