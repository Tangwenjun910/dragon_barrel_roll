package com.tangwenjun.dragonbarrelroll.compat;

import com.tangwenjun.dragonbarrelroll.DoABarrelRoll;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * MixinPlugin that detects whether the original Do A Barrel Roll mod is installed.
 * <p>
 * When DABR is present, Dragon Barrel Roll keeps full control over dragon flight.
 * DABR itself is still allowed to manage human/elytra flight. DragonSurvival's
 * native DABR dragon compatibility is disabled by {@code DoABarrelRollCompatMixin},
 * so DABR is never told that a dragon is elytra-flying.
 * <p>
 * The only Dragon Barrel Roll mixins disabled in DABR mode are the KeyBinding
 * mixins, because both mods would otherwise try to implement the same
 * {@code ContextualKeyBinding} interface on {@code KeyMapping}. Dragon Barrel Roll's
 * movement keys are read directly via {@code KeyMapping.isDown()}, so they still
 * work for dragon flight.
 */
public class DABRCompatPlugin implements IMixinConfigPlugin {

    /** True if the original DABR mod is loaded. */
    public static boolean DABR_LOADED = false;

    @Override
    public void onLoad(String mixinPackage) {
        // IMPORTANT: Do NOT use Class.forName() — use FMLLoader metadata instead
        var modList = net.neoforged.fml.loading.FMLLoader.getLoadingModList();
        var modFile = modList.getModFileById("do_a_barrel_roll");
        DABR_LOADED = modFile != null;

        if (DABR_LOADED) {
            DoABarrelRoll.LOGGER.info("Do A Barrel Roll detected! Dragon Barrel Roll will manage dragon flight; DABR remains for human/elytra flight.");
        } else {
            DoABarrelRoll.LOGGER.info("Do A Barrel Roll not detected. Dragon Barrel Roll running in standalone mode.");
        }
    }

    // KeyBinding mixins that should be disabled in DABR mode
    // because DABR's KeyBinding system provides the same ContextualKeyBinding interface.
    private static final Set<String> COMPAT_DISABLED_KEY_MIXINS = Set.of(
            "KeyBindingMixin",
            "KeyBindingAccessor",
            "KeyBindingEntryMixin"
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!DABR_LOADED) {
            return true;
        }

        // In DABR mode, disable Dragon Barrel Roll's KeyBinding mixins.
        // DABR's KeyBindingMixin provides the ContextualKeyBinding interface.
        // Dragon Barrel Roll's KeyBindingMixin would conflict (same WrapOperation targets,
        // same @Unique fields). Dragon Barrel Roll's movement keys still work because
        // buttonControls reads their isDown() directly.
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        if (COMPAT_DISABLED_KEY_MIXINS.contains(simpleName)) {
            DoABarrelRoll.LOGGER.debug("DABR compat: Skipping Dragon Barrel Roll KeyBinding mixin '{}'", simpleName);
            return false;
        }

        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
