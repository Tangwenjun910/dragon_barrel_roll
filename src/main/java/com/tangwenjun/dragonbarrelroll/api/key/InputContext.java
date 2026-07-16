package com.tangwenjun.dragonbarrelroll.api.key;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import com.tangwenjun.dragonbarrelroll.impl.key.InputContextImpl;

import java.util.List;
import java.util.function.Supplier;

public interface InputContext {
    static InputContext of(Identifier id, Supplier<Boolean> activeCondition) {
        return new InputContextImpl(id, activeCondition);
    }

    Identifier getId();

    boolean isActive();

    void addKeyBinding(KeyMapping KeyMapping);

    List<KeyMapping> getKeyBindings();

    KeyMapping getKeyBinding(InputConstants.Key key);

    void updateKeysByCode();
}
