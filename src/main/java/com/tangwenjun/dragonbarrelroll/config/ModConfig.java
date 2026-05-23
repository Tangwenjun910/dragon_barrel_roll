package com.tangwenjun.dragonbarrelroll.config;

import com.tangwenjun.dragonbarrelroll.api.event.RollContext;
import com.tangwenjun.dragonbarrelroll.api.rotation.RotationInstant;
import com.tangwenjun.dragonbarrelroll.math.ExpressionParser;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static final ModConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    // === Dragon-related configs (NEW) ===
    public final ModConfigSpec.BooleanValue enableMod;
    public final ModConfigSpec.BooleanValue enableHoverRoll;
    public final ModConfigSpec.BooleanValue enableGlideRoll;
    public final ModConfigSpec.BooleanValue syncRoll;
    public final ModConfigSpec.BooleanValue syncPitch;
    public final ModConfigSpec.BooleanValue syncYaw;
    public final ModConfigSpec.BooleanValue useVanillaVisuals;

    // === Controls ===
    public final ModConfigSpec.BooleanValue switchRollAndYaw;
    public final ModConfigSpec.BooleanValue invertPitch;
    public final ModConfigSpec.BooleanValue momentumBasedMouse;
    public final ModConfigSpec.DoubleValue momentumMouseDeadzone;
    public final ModConfigSpec.BooleanValue showMomentumWidget;
    public final ModConfigSpec.BooleanValue disableWhenSubmerged;

    // === HUD ===
    public final ModConfigSpec.BooleanValue showHorizon;

    // === Banking ===
    public final ModConfigSpec.BooleanValue enableBanking;
    public final ModConfigSpec.DoubleValue bankingStrength;
    public final ModConfigSpec.BooleanValue simulateControlSurfaceEfficacy;
    public final ModConfigSpec.BooleanValue automaticRighting;
    public final ModConfigSpec.DoubleValue rightingStrength;

    // === Sensitivity / Smoothing ===
    public final ModConfigSpec.DoubleValue smoothingPitch;
    public final ModConfigSpec.DoubleValue smoothingYaw;
    public final ModConfigSpec.DoubleValue smoothingRoll;
    public final ModConfigSpec.DoubleValue desktopPitch;
    public final ModConfigSpec.DoubleValue desktopYaw;
    public final ModConfigSpec.DoubleValue desktopRoll;
    public final ModConfigSpec.DoubleValue controllerPitch;
    public final ModConfigSpec.DoubleValue controllerYaw;
    public final ModConfigSpec.DoubleValue controllerRoll;

    // === Advanced formulas (stored as strings, compiled lazily) ===
    public final ModConfigSpec.ConfigValue<String> bankingXFormulaStr;
    public final ModConfigSpec.ConfigValue<String> bankingYFormulaStr;
    public final ModConfigSpec.ConfigValue<String> elevatorEfficacyFormulaStr;
    public final ModConfigSpec.ConfigValue<String> aileronEfficacyFormulaStr;
    public final ModConfigSpec.ConfigValue<String> rudderEfficacyFormulaStr;

    // Lazy compiled expression caches
    private ExpressionParser cachedBankingX;
    private ExpressionParser cachedBankingY;
    private ExpressionParser cachedElevator;
    private ExpressionParser cachedAileron;
    private ExpressionParser cachedRudder;
    private String lastBankingXStr;
    private String lastBankingYStr;
    private String lastElevatorStr;
    private String lastAileronStr;
    private String lastRudderStr;

    static {
        Pair<ModConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ModConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private ModConfig(ModConfigSpec.Builder builder) {
        builder.comment("Dragon Barrel Roll Configuration").push("dragon");

        enableMod = builder
                .comment("Enable/Disable the entire mod.")
                .translation("config.dragon_barrel_roll.enableMod")
                .define("enableMod", true);

        enableHoverRoll = builder
                .comment("Allow barrel roll while hovering (not sprint-flying).")
                .translation("config.dragon_barrel_roll.enableHoverRoll")
                .define("enableHoverRoll", false);

        enableGlideRoll = builder
                .comment("Allow barrel roll while gliding (sprint-flying).")
                .translation("config.dragon_barrel_roll.enableGlideRoll")
                .define("enableGlideRoll", true);

        builder.pop();

        builder.comment("Sync settings - synchronize DaBR rotation to dragon model").push("sync");

        syncRoll = builder
                .comment("Sync barrel roll rotation to dragon model.")
                .translation("config.dragon_barrel_roll.syncRoll")
                .define("syncRoll", true);

        syncPitch = builder
                .comment("Sync pitch to dragon model.")
                .translation("config.dragon_barrel_roll.syncPitch")
                .define("syncPitch", true);

        syncYaw = builder
                .comment("Sync yaw to dragon model.")
                .translation("config.dragon_barrel_roll.syncYaw")
                .define("syncYaw", true);

        useVanillaVisuals = builder
                .comment("Disable barrel roll visual effects on dragon model, rider model, and mounting point. Only first-person camera roll is preserved.")
                .translation("config.dragon_barrel_roll.useVanillaVisuals")
                .define("useVanillaVisuals", true);

        builder.pop();

        builder.comment("Control settings").push("controls");

        switchRollAndYaw = builder
                .comment("Switch roll and yaw axes.")
                .translation("config.dragon_barrel_roll.switchRollAndYaw")
                .define("switchRollAndYaw", false);

        invertPitch = builder
                .comment("Invert pitch axis.")
                .translation("config.dragon_barrel_roll.invertPitch")
                .define("invertPitch", false);

        momentumBasedMouse = builder
                .comment("Use momentum-based mouse input.")
                .translation("config.dragon_barrel_roll.momentumBasedMouse")
                .define("momentumBasedMouse", false);

        momentumMouseDeadzone = builder
                .comment("Deadzone for momentum mouse input.")
                .translation("config.dragon_barrel_roll.momentumMouseDeadzone")
                .defineInRange("momentumMouseDeadzone", 0.2, 0.0, 1.0);

        showMomentumWidget = builder
                .comment("Show momentum crosshair widget.")
                .translation("config.dragon_barrel_roll.showMomentumWidget")
                .define("showMomentumWidget", true);

        disableWhenSubmerged = builder
                .comment("Disable mod effects when submerged in water.")
                .translation("config.dragon_barrel_roll.disableWhenSubmerged")
                .define("disableWhenSubmerged", true);

        builder.pop();

        builder.comment("HUD settings").push("hud");

        showHorizon = builder
                .comment("Show horizon line indicator.")
                .translation("config.dragon_barrel_roll.showHorizon")
                .define("showHorizon", false);

        builder.pop();

        builder.comment("Banking settings").push("banking");

        enableBanking = builder
                .comment("Enable banking effect.")
                .translation("config.dragon_barrel_roll.enableBanking")
                .define("enableBanking", true);

        bankingStrength = builder
                .comment("Banking strength multiplier.")
                .translation("config.dragon_barrel_roll.bankingStrength")
                .defineInRange("bankingStrength", 20.0, 0.0, 100.0);

        simulateControlSurfaceEfficacy = builder
                .comment("Simulate control surface efficacy based on velocity.")
                .translation("config.dragon_barrel_roll.simulateControlSurfaceEfficacy")
                .define("simulateControlSurfaceEfficacy", false);

        automaticRighting = builder
                .comment("Automatically right the roll when not actively rolling.")
                .translation("config.dragon_barrel_roll.automaticRighting")
                .define("automaticRighting", false);

        rightingStrength = builder
                .comment("Strength of automatic righting.")
                .translation("config.dragon_barrel_roll.rightingStrength")
                .defineInRange("rightingStrength", 50.0, 0.0, 200.0);

        builder.pop();

        builder.comment("Sensitivity settings").push("sensitivity");

        smoothingPitch = builder
                .comment("Pitch smoothing factor (0 = no smoothing).")
                .translation("config.dragon_barrel_roll.smoothingPitch")
                .defineInRange("smoothingPitch", 1.0, 0.0, 100.0);

        smoothingYaw = builder
                .comment("Yaw smoothing factor (0 = no smoothing).")
                .translation("config.dragon_barrel_roll.smoothingYaw")
                .defineInRange("smoothingYaw", 2.5, 0.0, 100.0);

        smoothingRoll = builder
                .comment("Roll smoothing factor (0 = no smoothing).")
                .translation("config.dragon_barrel_roll.smoothingRoll")
                .defineInRange("smoothingRoll", 1.0, 0.0, 100.0);

        desktopPitch = builder
                .comment("Desktop keyboard pitch sensitivity (degrees/sec).")
                .translation("config.dragon_barrel_roll.desktopPitch")
                .defineInRange("desktopPitch", 1.0, 0.0, 10.0);

        desktopYaw = builder
                .comment("Desktop keyboard yaw sensitivity (degrees/sec).")
                .translation("config.dragon_barrel_roll.desktopYaw")
                .defineInRange("desktopYaw", 0.4, 0.0, 10.0);

        desktopRoll = builder
                .comment("Desktop keyboard roll sensitivity (degrees/sec).")
                .translation("config.dragon_barrel_roll.desktopRoll")
                .defineInRange("desktopRoll", 1.0, 0.0, 10.0);

        controllerPitch = builder
                .comment("Controller pitch sensitivity (degrees/sec).")
                .translation("config.dragon_barrel_roll.controllerPitch")
                .defineInRange("controllerPitch", 1.0, 0.0, 10.0);

        controllerYaw = builder
                .comment("Controller yaw sensitivity (degrees/sec).")
                .translation("config.dragon_barrel_roll.controllerYaw")
                .defineInRange("controllerYaw", 0.4, 0.0, 10.0);

        controllerRoll = builder
                .comment("Controller roll sensitivity (degrees/sec).")
                .translation("config.dragon_barrel_roll.controllerRoll")
                .defineInRange("controllerRoll", 1.0, 0.0, 10.0);

        builder.pop();

        builder.comment("Advanced formula settings (expert only)").push("advanced");

        bankingXFormulaStr = builder
                .comment("Formula for banking X component.")
                .translation("config.dragon_barrel_roll.bankingXFormula")
                .define("bankingXFormula", "sin($roll * TO_RAD) * cos($pitch * TO_RAD) * 10 * $banking_strength");

        bankingYFormulaStr = builder
                .comment("Formula for banking Y component.")
                .translation("config.dragon_barrel_roll.bankingYFormula")
                .define("bankingYFormula", "(-1 + cos($roll * TO_RAD)) * cos($pitch * TO_RAD) * 10 * $banking_strength");

        elevatorEfficacyFormulaStr = builder
                .comment("Formula for elevator control surface efficacy.")
                .translation("config.dragon_barrel_roll.elevatorEfficacyFormula")
                .define("elevatorEfficacyFormula", "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");

        aileronEfficacyFormulaStr = builder
                .comment("Formula for aileron control surface efficacy.")
                .translation("config.dragon_barrel_roll.aileronEfficacyFormula")
                .define("aileronEfficacyFormula", "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");

        rudderEfficacyFormulaStr = builder
                .comment("Formula for rudder control surface efficacy.")
                .translation("config.dragon_barrel_roll.rudderEfficacyFormula")
                .define("rudderEfficacyFormula", "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");

        builder.pop();
    }

    // ===== Compatibility: no-op touch =====
    public static void touch() {
    }

    // ===== Getter methods =====

    public boolean getModEnabled() {
        return enableMod.get();
    }

    public boolean getSwitchRollAndYaw() {
        return switchRollAndYaw.get();
    }

    public boolean getMomentumBasedMouse() {
        return momentumBasedMouse.get();
    }

    public double getMomentumMouseDeadzone() {
        return momentumMouseDeadzone.get();
    }

    public boolean getShowMomentumWidget() {
        return showMomentumWidget.get();
    }

    public boolean getInvertPitch() {
        return invertPitch.get();
    }

    public boolean getDisableWhenSubmerged() {
        return disableWhenSubmerged.get();
    }

    public boolean getUseVanillaVisuals() {
        return useVanillaVisuals.get();
    }

    public boolean getShowHorizon() {
        return showHorizon.get();
    }

    public boolean getEnableBanking() {
        return enableBanking.get();
    }

    public double getBankingStrength() {
        return bankingStrength.get();
    }

    public boolean getSimulateControlSurfaceEfficacy() {
        return simulateControlSurfaceEfficacy.get();
    }

    public boolean getAutomaticRighting() {
        return automaticRighting.get();
    }

    public double getRightingStrength() {
        return rightingStrength.get();
    }

    public double getSmoothingPitch() {
        return smoothingPitch.get();
    }

    public double getSmoothingYaw() {
        return smoothingYaw.get();
    }

    public double getSmoothingRoll() {
        return smoothingRoll.get();
    }

    public Sensitivity getSmoothing() {
        return new Sensitivity(smoothingPitch.get(), smoothingYaw.get(), smoothingRoll.get());
    }

    public Sensitivity getDesktopSensitivity() {
        return new Sensitivity(desktopPitch.get(), desktopYaw.get(), desktopRoll.get());
    }

    public double getDesktopPitch() {
        return desktopPitch.get();
    }

    public double getDesktopYaw() {
        return desktopYaw.get();
    }

    public double getDesktopRoll() {
        return desktopRoll.get();
    }

    public Sensitivity getControllerSensitivity() {
        return new Sensitivity(controllerPitch.get(), controllerYaw.get(), controllerRoll.get());
    }

    public double getControllerPitch() {
        return controllerPitch.get();
    }

    public double getControllerYaw() {
        return controllerYaw.get();
    }

    public double getControllerRoll() {
        return controllerRoll.get();
    }

    // ===== Formula getters (lazy compilation) =====

    public ExpressionParser getBankingXFormula() {
        String current = bankingXFormulaStr.get();
        if (cachedBankingX == null || !current.equals(lastBankingXStr)) {
            cachedBankingX = new ExpressionParser(current);
            lastBankingXStr = current;
        }
        return cachedBankingX;
    }

    public ExpressionParser getBankingYFormula() {
        String current = bankingYFormulaStr.get();
        if (cachedBankingY == null || !current.equals(lastBankingYStr)) {
            cachedBankingY = new ExpressionParser(current);
            lastBankingYStr = current;
        }
        return cachedBankingY;
    }

    public ExpressionParser getElevatorEfficacyFormula() {
        String current = elevatorEfficacyFormulaStr.get();
        if (cachedElevator == null || !current.equals(lastElevatorStr)) {
            cachedElevator = new ExpressionParser(current);
            lastElevatorStr = current;
        }
        return cachedElevator;
    }

    public ExpressionParser getAileronEfficacyFormula() {
        String current = aileronEfficacyFormulaStr.get();
        if (cachedAileron == null || !current.equals(lastAileronStr)) {
            cachedAileron = new ExpressionParser(current);
            lastAileronStr = current;
        }
        return cachedAileron;
    }

    public ExpressionParser getRudderEfficacyFormula() {
        String current = rudderEfficacyFormulaStr.get();
        if (cachedRudder == null || !current.equals(lastRudderStr)) {
            cachedRudder = new ExpressionParser(current);
            lastRudderStr = current;
        }
        return cachedRudder;
    }

    // ===== Setter methods (used by config screen) =====

    public void setModEnabled(boolean enabled) {
        enableMod.set(enabled);
    }

    public void setSwitchRollAndYaw(boolean enabled) {
        switchRollAndYaw.set(enabled);
    }

    public void setMomentumBasedMouse(boolean enabled) {
        momentumBasedMouse.set(enabled);
    }

    public void setMomentumMouseDeadzone(double deadzone) {
        momentumMouseDeadzone.set(deadzone);
    }

    public void setShowMomentumWidget(boolean enabled) {
        showMomentumWidget.set(enabled);
    }

    public void setInvertPitch(boolean enabled) {
        invertPitch.set(enabled);
    }

    public void setDisableWhenSubmerged(boolean enabled) {
        disableWhenSubmerged.set(enabled);
    }

    public void setShowHorizon(boolean enabled) {
        showHorizon.set(enabled);
    }

    public void setEnableBanking(boolean enabled) {
        enableBanking.set(enabled);
    }

    public void setBankingStrength(double strength) {
        bankingStrength.set(strength);
    }

    public void setSimulateControlSurfaceEfficacy(boolean enabled) {
        simulateControlSurfaceEfficacy.set(enabled);
    }

    public void setAutomaticRighting(boolean enabled) {
        automaticRighting.set(enabled);
    }

    public void setRightingStrength(double strength) {
        rightingStrength.set(strength);
    }

    public void setSmoothingPitch(double pitch) {
        smoothingPitch.set(pitch);
    }

    public void setSmoothingYaw(double yaw) {
        smoothingYaw.set(yaw);
    }

    public void setSmoothingRoll(double roll) {
        smoothingRoll.set(roll);
    }

    public void setDesktopSensitivity(Sensitivity sensitivity) {
        desktopPitch.set(sensitivity.pitch);
        desktopYaw.set(sensitivity.yaw);
        desktopRoll.set(sensitivity.roll);
    }

    public void setDesktopPitch(double pitch) {
        desktopPitch.set(pitch);
    }

    public void setDesktopYaw(double yaw) {
        desktopYaw.set(yaw);
    }

    public void setDesktopRoll(double roll) {
        desktopRoll.set(roll);
    }

    public void setControllerSensitivity(Sensitivity sensitivity) {
        controllerPitch.set(sensitivity.pitch);
        controllerYaw.set(sensitivity.yaw);
        controllerRoll.set(sensitivity.roll);
    }

    public void setControllerPitch(double pitch) {
        controllerPitch.set(pitch);
    }

    public void setControllerYaw(double yaw) {
        controllerYaw.set(yaw);
    }

    public void setControllerRoll(double roll) {
        controllerRoll.set(roll);
    }

    public void setBankingXFormula(ExpressionParser formula) {
        bankingXFormulaStr.set(formula.toString());
        cachedBankingX = formula;
        lastBankingXStr = bankingXFormulaStr.get();
    }

    public void setBankingYFormula(ExpressionParser formula) {
        bankingYFormulaStr.set(formula.toString());
        cachedBankingY = formula;
        lastBankingYStr = bankingYFormulaStr.get();
    }

    public void setElevatorEfficacyFormula(ExpressionParser formula) {
        elevatorEfficacyFormulaStr.set(formula.toString());
        cachedElevator = formula;
        lastElevatorStr = elevatorEfficacyFormulaStr.get();
    }

    public void setAileronEfficacyFormula(ExpressionParser formula) {
        aileronEfficacyFormulaStr.set(formula.toString());
        cachedAileron = formula;
        lastAileronStr = aileronEfficacyFormulaStr.get();
    }

    public void setRudderEfficacyFormula(ExpressionParser formula) {
        rudderEfficacyFormulaStr.set(formula.toString());
        cachedRudder = formula;
        lastRudderStr = rudderEfficacyFormulaStr.get();
    }

    // ===== Rotation configuration =====

    public RotationInstant configureRotation(RotationInstant rotationInstant, RollContext context) {
        var pitch = rotationInstant.pitch();
        var yaw = rotationInstant.yaw();
        var roll = rotationInstant.roll();

        if (!getSwitchRollAndYaw()) {
            var temp = yaw;
            yaw = roll;
            roll = temp;
        }
        if (getInvertPitch()) {
            pitch = -pitch;
        }

        return RotationInstant.of(pitch, yaw, roll);
    }
}
