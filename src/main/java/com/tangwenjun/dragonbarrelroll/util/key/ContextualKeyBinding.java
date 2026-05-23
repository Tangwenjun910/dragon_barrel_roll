package com.tangwenjun.dragonbarrelroll.util.key;

import com.tangwenjun.dragonbarrelroll.api.key.InputContext;

import java.util.List;

public interface ContextualKeyBinding {
    void doABarrelRoll$addToContext(InputContext context);

    List<InputContext> doABarrelRoll$getContexts();
}
