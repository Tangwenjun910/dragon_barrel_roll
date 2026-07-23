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
 * When DABR is present, Dragon BR's {@link com.tangwenjun.dragonbarrelroll.DABRMixinCanceller}
 * cancels DABR's conflicting mixins so that Dragon BR's full barrel roll pipeline
 * handles ALL flight (both dragon and elytra) without field/method/interface collisions.
 * <p>
 * This plugin always applies all Dragon BR mixins. The conflict resolution
 * is done via MixinSquared's MixinCanceller, not by disabling our own mixins.
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
            DoABarrelRoll.LOGGER.info("Do A Barrel Roll detected! Dragon Barrel Roll will run in compatibility mode.");
            DoABarrelRoll.LOGGER.info("DABR conflicting mixins cancelled — Dragon BR handles all barrel roll mechanics.");
        } else {
            DoABarrelRoll.LOGGER.info("Do A Barrel Roll not detected. Dragon Barrel Roll running in standalone mode.");
        }
    }

    // KeyBinding mixins that should be disabled in compat mode
    // because DABR's KeyBinding system handles context-sensitive keys for both mods
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

        // In compat mode, disable Dragon BR's KeyBinding mixins.
        // DABR's KeyBindingMixin provides the ContextualKeyBinding interface.
        // Dragon BR's KeyBindingMixin would conflict (same WrapOperation targets,
        // same @Unique fields). DABR's system handles context-sensitive keys
        // for both mods — Dragon BR's movement keys work without our own
        // KeyBinding mixins because buttonControls reads their isDown() directly.
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        if (COMPAT_DISABLED_KEY_MIXINS.contains(simpleName)) {
            DoABarrelRoll.LOGGER.debug("DABR compat: Skipping Dragon BR KeyBinding mixin '{}'", simpleName);
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
