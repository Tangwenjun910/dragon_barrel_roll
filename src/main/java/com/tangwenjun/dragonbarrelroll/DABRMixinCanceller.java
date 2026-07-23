package com.tangwenjun.dragonbarrelroll;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import com.bawnorton.mixinsquared.tools.MixinAnnotationReader;

import java.util.List;

/**
 * Cancels Equipment Compare's overly invasive KeyMapping mixin.
 * <p>
 * No longer cancels DABR's mixins — Dragon BR uses its own DragonRoll interface
 * with unique method names (dragon_barrel_roll$ prefix), so both mods' mixins
 * can coexist on the same target classes without field/method conflicts.
 */
public class DABRMixinCanceller implements MixinCanceller {

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("com.anthonyhilyard.equipmentcompare.mixin.KeyMappingMixin") && MixinAnnotationReader.getPriority(mixinClassName) == 1000) {
            DoABarrelRoll.LOGGER.warn("Equipment Compare detected, disabling their overly invasive KeyMapping mixin. Report any relevant issues to them.");
            DoABarrelRoll.LOGGER.warn("If the author of Equipment Compare is reading this: see #31 on your github. Once the issue is resolved, you can set the priority of this mixin to anything other than 1000 to stop it being disabled.");
            return true;
        }
        return false;
    }
}
