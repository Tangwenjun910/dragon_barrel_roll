package com.tangwenjun.dragonbarrelroll;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import com.bawnorton.mixinsquared.tools.MixinAnnotationReader;

import java.util.List;

/**
 * Cancels Equipment Compare's overly invasive KeyMapping mixin.
 * <p>
 * This intentionally does not cancel DABR's mixins. Dragon Barrel Roll prevents
 * DABR from taking over dragon flight by disabling DragonSurvival's native
 * DABR dragon compatibility ({@code DoABarrelRollCompat#shouldEnableDragonFlight}),
 * not by removing DABR's mixins. DABR remains fully functional for human/elytra flight.
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
