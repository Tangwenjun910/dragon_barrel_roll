package com.tangwenjun.dragonbarrelroll.api.event;

import net.minecraft.resources.ResourceLocation;
import com.tangwenjun.dragonbarrelroll.impl.event.RollGroupImpl;

import java.util.function.Supplier;

/**
 * A group of conditions that determine whether the camera should be rolling and what effects should be applied.
 */
public interface RollGroup extends Supplier<Boolean>, Event<RollGroup.RollCondition> {
    static RollGroup of(ResourceLocation id) {
        return RollGroupImpl.instances.computeIfAbsent(id, id2 -> new RollGroupImpl());
    }

    void trueIf(Supplier<Boolean> condition, int priority);

    void trueIf(Supplier<Boolean> condition);

    void falseUnless(Supplier<Boolean> condition, int priority);

    void falseUnless(Supplier<Boolean> condition);

    interface RollCondition {
        TriState shouldRoll();
    }
}
