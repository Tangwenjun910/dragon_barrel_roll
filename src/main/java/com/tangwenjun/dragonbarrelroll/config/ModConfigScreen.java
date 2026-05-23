package com.tangwenjun.dragonbarrelroll.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ModConfigScreen extends Screen {

    private final Screen parent;
    private double scrollOffset = 0;
    private int contentHeight = 0;
    private boolean scrollbarDragging;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final DecimalFormat DF = new DecimalFormat("0.0#");
    private static final DecimalFormat DF3 = new DecimalFormat("0.0##");

    private static final int LEFT_MARGIN = 20;
    private static final int RIGHT_MARGIN = 20;
    private static final int ROW_HEIGHT = 24;
    private static final int SECTION_GAP = 12;
    private static final int BOOL_BUTTON_WIDTH = 50;
    private static final int FORMULA_FIELD_WIDTH = 260;

    private static final int CLIP_TOP = 32;
    private int clipBottom;

    private Button doneButton;
    private Button resetAllButton;
    private final List<ScrollableWidget> scrollWidgets = new ArrayList<>();

    /** Tracks each widget with its original Y so scrolling never accumulates. */
    private static class ScrollableWidget {
        final AbstractWidget widget;
        final int baseY;
        final String tooltipKey;

        ScrollableWidget(AbstractWidget widget, int baseY, String tooltipKey) {
            this.widget = widget;
            this.baseY = baseY;
            this.tooltipKey = tooltipKey;
        }

        void applyScroll(double offset) {
            widget.setY(baseY + (int) offset);
        }
    }

    public ModConfigScreen(Screen parent) {
        super(Component.translatable("config.dragon_barrel_roll.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clipBottom = this.height - 40;
        scrollWidgets.clear();
        int y = 35;

        // ===== Dragon Flight =====
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.dragon");
        y = addBoolRow(y, ModConfig.INSTANCE.enableMod, "enableMod");
        y = addBoolRow(y, ModConfig.INSTANCE.enableHoverRoll, "enableHoverRoll");
        y = addBoolRow(y, ModConfig.INSTANCE.enableGlideRoll, "enableGlideRoll");

        // ===== Model Sync =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.sync");
        y = addBoolRow(y, ModConfig.INSTANCE.syncRoll, "syncRoll");
        y = addBoolRow(y, ModConfig.INSTANCE.syncPitch, "syncPitch");
        y = addBoolRow(y, ModConfig.INSTANCE.syncYaw, "syncYaw");
        y = addBoolRow(y, ModConfig.INSTANCE.useVanillaVisuals, "useVanillaVisuals");

        // ===== Controls =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.controls");
        y = addBoolRow(y, ModConfig.INSTANCE.switchRollAndYaw, "switchRollAndYaw");
        y = addBoolRow(y, ModConfig.INSTANCE.invertPitch, "invertPitch");
        y = addBoolRow(y, ModConfig.INSTANCE.momentumBasedMouse, "momentumBasedMouse");
        y = addDoubleRow(y, ModConfig.INSTANCE.momentumMouseDeadzone, "momentumMouseDeadzone", 0.0, 1.0, 0.2);
        y = addBoolRow(y, ModConfig.INSTANCE.showMomentumWidget, "showMomentumWidget");
        y = addBoolRow(y, ModConfig.INSTANCE.disableWhenSubmerged, "disableWhenSubmerged");

        // ===== HUD =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.hud");
        y = addBoolRow(y, ModConfig.INSTANCE.showHorizon, "showHorizon");

        // ===== Banking =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.banking");
        y = addBoolRow(y, ModConfig.INSTANCE.enableBanking, "enableBanking");
        y = addDoubleRow(y, ModConfig.INSTANCE.bankingStrength, "bankingStrength", 0.0, 100.0, 20.0);
        y = addBoolRow(y, ModConfig.INSTANCE.simulateControlSurfaceEfficacy, "simulateControlSurfaceEfficacy");
        y = addBoolRow(y, ModConfig.INSTANCE.automaticRighting, "automaticRighting");
        y = addDoubleRow(y, ModConfig.INSTANCE.rightingStrength, "rightingStrength", 0.0, 200.0, 50.0);

        // ===== Smoothing =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.smoothing");
        y = addDoubleRow(y, ModConfig.INSTANCE.smoothingPitch, "smoothingPitch", 0.0, 100.0, 1.0);
        y = addDoubleRow(y, ModConfig.INSTANCE.smoothingYaw, "smoothingYaw", 0.0, 100.0, 2.5);
        y = addDoubleRow(y, ModConfig.INSTANCE.smoothingRoll, "smoothingRoll", 0.0, 100.0, 1.0);

        // ===== Desktop Sensitivity =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.desktopSensitivity");
        y = addDoubleRow(y, ModConfig.INSTANCE.desktopPitch, "desktopPitch", 0.0, 10.0, 1.0);
        y = addDoubleRow(y, ModConfig.INSTANCE.desktopYaw, "desktopYaw", 0.0, 10.0, 0.4);
        y = addDoubleRow(y, ModConfig.INSTANCE.desktopRoll, "desktopRoll", 0.0, 10.0, 1.0);

        // ===== Controller Sensitivity =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.controllerSensitivity");
        y = addDoubleRow(y, ModConfig.INSTANCE.controllerPitch, "controllerPitch", 0.0, 10.0, 1.0);
        y = addDoubleRow(y, ModConfig.INSTANCE.controllerYaw, "controllerYaw", 0.0, 10.0, 0.4);
        y = addDoubleRow(y, ModConfig.INSTANCE.controllerRoll, "controllerRoll", 0.0, 10.0, 1.0);

        // ===== Advanced Formulas =====
        y = addSectionGap(y);
        y = addSectionHeader(y, "config.dragon_barrel_roll.category.advanced");
        y = addFormulaRow(y, ModConfig.INSTANCE.bankingXFormulaStr, "bankingXFormula", "sin($roll * TO_RAD) * cos($pitch * TO_RAD) * 10 * $banking_strength");
        y = addFormulaRow(y, ModConfig.INSTANCE.bankingYFormulaStr, "bankingYFormula", "(-1 + cos($roll * TO_RAD)) * cos($pitch * TO_RAD) * 10 * $banking_strength");
        y = addFormulaRow(y, ModConfig.INSTANCE.elevatorEfficacyFormulaStr, "elevatorEfficacyFormula", "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        y = addFormulaRow(y, ModConfig.INSTANCE.aileronEfficacyFormulaStr, "aileronEfficacyFormula", "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        y = addFormulaRow(y, ModConfig.INSTANCE.rudderEfficacyFormulaStr, "rudderEfficacyFormula", "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");

        contentHeight = y + 10;

        // Done button — positioned outside the scrollable clip area
        doneButton = Button.builder(
                Component.translatable("config.dragon_barrel_roll.done"),
                btn -> {
                    ModConfig.SPEC.save();
                    this.minecraft.setScreen(parent);
                })
                .pos(this.width / 2 - 62, this.height - 30)
                .size(80, 20).build();
        addRenderableWidget(doneButton);

        // Reset All button
        resetAllButton = Button.builder(
                Component.translatable("config.dragon_barrel_roll.resetAll"),
                btn -> {
                    resetAllToDefaults();
                    rebuildScreen();
                })
                .pos(this.width / 2 + 22, this.height - 30)
                .size(40, 20).build();
        addRenderableWidget(resetAllButton);

        applyScroll();
    }

    // ── Row builders ──

    private int addSectionHeader(int y, String key) {
        var lbl = new StringWidget(LEFT_MARGIN, y, this.width - LEFT_MARGIN - RIGHT_MARGIN, 14,
                Component.translatable(key), this.font);
        lbl.setColor(0xFFAAAAAA);
        register(lbl, y, null);
        return y + 18;
    }

    private int addSectionGap(int y) {
        return y + SECTION_GAP;
    }

    private int addBoolRow(int y, ModConfigSpec.BooleanValue configValue, String key) {
        String tipKey = "config.dragon_barrel_roll." + key + ".tooltip";
        var label = new StringWidget(LEFT_MARGIN, y, 160, ROW_HEIGHT,
                Component.translatable("config.dragon_barrel_roll." + key), this.font);
        register(label, y, tipKey);

        var btn = Button.builder(
                boolText(configValue.get()),
                b -> {
                    boolean nv = !configValue.get();
                    configValue.set(nv);
                    b.setMessage(boolText(nv));
                })
                .pos(this.width - RIGHT_MARGIN - BOOL_BUTTON_WIDTH, y)
                .size(BOOL_BUTTON_WIDTH, ROW_HEIGHT - 2).build();
        register(btn, y, tipKey);

        return y + ROW_HEIGHT + 2;
    }

    private int addDoubleRow(int y, ModConfigSpec.DoubleValue configValue, String key, double min, double max, double defaultValue) {
        String tipKey = "config.dragon_barrel_roll." + key + ".tooltip";
        var label = new StringWidget(LEFT_MARGIN, y, 120, ROW_HEIGHT,
                Component.translatable("config.dragon_barrel_roll." + key), this.font);
        register(label, y, tipKey);

        double step = max < 10.0 ? 0.05 : 0.5;
        int arrowW = 18;
        int resetW = 16;
        int valW = 38;
        int sliderX = LEFT_MARGIN + 125;
        // > button | gap | reset | gap | value label | right margin
        int sliderEnd = this.width - RIGHT_MARGIN - valW - 2 - resetW - 2 - arrowW;

        // Value label
        var valueLabel = new ValueLabel(this.width - RIGHT_MARGIN - valW, y, valW, ROW_HEIGHT, configValue, max, this.font);
        register(valueLabel, y, tipKey);

        // < button
        var leftBtn = Button.builder(Component.literal("<"),
                b -> {
                    double nv = Math.max(min, configValue.get() - step);
                    configValue.set(nv);
                    rebuildScreen();
                }).pos(sliderX, y).size(arrowW, ROW_HEIGHT - 2).build();
        register(leftBtn, y, tipKey);

        // > button
        var rightBtn = Button.builder(Component.literal(">"),
                b -> {
                    double nv = Math.min(max, configValue.get() + step);
                    configValue.set(nv);
                    rebuildScreen();
                }).pos(sliderEnd, y).size(arrowW, ROW_HEIGHT - 2).build();
        register(rightBtn, y, tipKey);

        // Reset button (per-row)
        int resetX = sliderEnd + arrowW + 2;
        var resetBtn = Button.builder(Component.literal("\u21BA"),
                b -> {
                    configValue.set(defaultValue);
                    rebuildScreen();
                }).pos(resetX, y + 3).size(resetW, ROW_HEIGHT - 4).build();
        register(resetBtn, y, tipKey);

        // Draggable slider
        int barX = sliderX + 20;
        int barWidth = sliderEnd - barX - 2;
        int barY = y + (ROW_HEIGHT - 10) / 2;  // offset down from center
        var slider = new SliderWidget(barX, barY, barWidth, configValue, min, max, step);
        register(slider, barY, tipKey);  // use slider's actual Y, not row Y

        return y + ROW_HEIGHT + 2;
    }

    private int addFormulaRow(int y, ModConfigSpec.ConfigValue<String> configValue, String key, String defaultVal) {
        String tipKey = "config.dragon_barrel_roll." + key + ".tooltip";
        var label = new StringWidget(LEFT_MARGIN, y, this.width - LEFT_MARGIN - RIGHT_MARGIN, 12,
                Component.translatable("config.dragon_barrel_roll." + key), this.font);
        register(label, y, tipKey);

        var editBox = new EditBox(this.font, LEFT_MARGIN, y + 14,
                FORMULA_FIELD_WIDTH, 18,
                Component.translatable("config.dragon_barrel_roll." + key));
        editBox.setMaxLength(256);
        editBox.setValue(configValue.get());
        editBox.setResponder(configValue::set);
        register(editBox, y + 14, tipKey);

        var resetBtn = Button.builder(
                Component.translatable("config.dragon_barrel_roll.reset"),
                btn -> {
                    configValue.set(defaultVal);
                    editBox.setValue(defaultVal);
                })
                .pos(LEFT_MARGIN + FORMULA_FIELD_WIDTH + 6, y + 14)
                .size(40, 18).build();
        register(resetBtn, y + 14, tipKey);

        return y + 36;
    }

    // ── Registry ──

    private void register(AbstractWidget widget, int baseY, String tooltipKey) {
        addRenderableWidget(widget);
        scrollWidgets.add(new ScrollableWidget(widget, baseY, tooltipKey));
    }

    private void rebuildScreen() {
        double saved = scrollOffset;
        clearWidgets();
        init();
        scrollOffset = Math.max(-Math.max(0, contentHeight - (clipBottom - CLIP_TOP)),
                Math.min(0, saved));
        applyScroll();
    }

    // ── Scroll ──

    private void applyScroll() {
        for (var sw : scrollWidgets) {
            sw.applyScroll(scrollOffset);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseY >= CLIP_TOP && mouseY <= clipBottom) {
            int visibleHeight = clipBottom - CLIP_TOP;
            double maxUp = contentHeight - visibleHeight;
            if (maxUp > 0) {
                scrollOffset += scrollY * 15;
                scrollOffset = Math.max(-maxUp, Math.min(0, scrollOffset));
                applyScroll();
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // ── Render ──

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Draw background
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        // Separator lines
        graphics.fill(0, CLIP_TOP - 1, this.width, CLIP_TOP, 0xAAFFFFFF);
        graphics.fill(0, clipBottom, this.width, clipBottom + 1, 0xAAFFFFFF);

        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

        // ── Scrollable area ──
        graphics.enableScissor(0, CLIP_TOP, this.width, clipBottom);

        for (var sw : scrollWidgets) {
            int wy = sw.widget.getY();
            int wh = sw.widget.getHeight();
            // Only render if visible
            if (wy + wh > CLIP_TOP && wy < clipBottom) {
                sw.widget.render(graphics, mouseX, mouseY, partialTick);
            }
        }

        graphics.disableScissor();

        // ── Scrollbar ──
        int visibleHeight = clipBottom - CLIP_TOP;
        if (contentHeight > visibleHeight) {
            int trackX = this.width - 6;
            int trackH = visibleHeight;
            int handleH = Math.max(20, (int)((double) visibleHeight / contentHeight * trackH));
            double maxUp = contentHeight - visibleHeight;
            int handleY = CLIP_TOP + (int)(-scrollOffset / maxUp * (trackH - handleH));

            // Track
            graphics.fill(trackX, CLIP_TOP, trackX + SCROLLBAR_WIDTH, clipBottom, 0x33FFFFFF);
            // Handle
            graphics.fill(trackX, handleY, trackX + SCROLLBAR_WIDTH, handleY + handleH,
                    scrollbarDragging ? 0xFFAAAAAA : 0x88FFFFFF);
        }

        // ── Tooltip ──
        for (var sw : scrollWidgets) {
            if (sw.tooltipKey != null && sw.widget.isMouseOver(mouseX, mouseY)) {
                graphics.renderTooltip(this.font, Component.translatable(sw.tooltipKey), mouseX, mouseY);
                break;
            }
        }

        // ── Pinned buttons ──
        doneButton.render(graphics, mouseX, mouseY, partialTick);
        resetAllButton.render(graphics, mouseX, mouseY, partialTick);
    }

    // ── Scrollbar helper ──

    private boolean isOverScrollbarHandle(double mouseX, double mouseY) {
        int visibleHeight = clipBottom - CLIP_TOP;
        if (contentHeight <= visibleHeight) return false;
        int trackX = this.width - 6;
        int trackH = visibleHeight;
        int handleH = Math.max(20, (int)((double) visibleHeight / contentHeight * trackH));
        double maxUp = contentHeight - visibleHeight;
        int handleY = CLIP_TOP + (int)(-scrollOffset / maxUp * (trackH - handleH));
        return mouseX >= trackX && mouseX <= trackX + SCROLLBAR_WIDTH
                && mouseY >= handleY && mouseY <= handleY + handleH;
    }

    private void scrollbarDragTo(double mouseY) {
        int visibleHeight = clipBottom - CLIP_TOP;
        if (contentHeight <= visibleHeight) return;
        int trackH = visibleHeight;
        int handleH = Math.max(20, (int)((double) visibleHeight / contentHeight * trackH));
        double ratio = (mouseY - CLIP_TOP - handleH / 2.0) / (trackH - handleH);
        ratio = Math.max(0, Math.min(1, ratio));
        double maxUp = contentHeight - visibleHeight;
        scrollOffset = -ratio * maxUp;
        applyScroll();
    }

    // ── Mouse ──

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Scrollbar handle
        if (isOverScrollbarHandle(mouseX, mouseY)) {
            scrollbarDragging = true;
            return true;
        }
        // Scrollable area
        if (mouseY >= CLIP_TOP && mouseY <= clipBottom) {
            for (var sw : scrollWidgets) {
                if (sw.widget.isMouseOver(mouseX, mouseY)) {
                    if (sw.widget.mouseClicked(mouseX, mouseY, button)) {
                        this.setFocused(sw.widget);
                        return true;
                    }
                }
            }
        } else {
            // Click outside scroll area — clear focus from scroll widgets
            for (var sw : scrollWidgets) {
                if (sw.widget.isFocused()) {
                    sw.widget.setFocused(false);
                }
            }
        }
        // Done / Reset All buttons
        if (doneButton.isMouseOver(mouseX, mouseY)) {
            if (doneButton.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(doneButton);
                return true;
            }
        }
        if (resetAllButton.isMouseOver(mouseX, mouseY)) {
            if (resetAllButton.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(resetAllButton);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (scrollbarDragging) {
            scrollbarDragging = false;
            return true;
        }
        if (mouseY >= CLIP_TOP && mouseY <= clipBottom) {
            for (var sw : scrollWidgets) {
                if (sw.widget.isMouseOver(mouseX, mouseY)) {
                    if (sw.widget.mouseReleased(mouseX, mouseY, button)) {
                        return true;
                    }
                }
            }
        }
        if (doneButton.isMouseOver(mouseX, mouseY)) {
            return doneButton.mouseReleased(mouseX, mouseY, button);
        }
        if (resetAllButton.isMouseOver(mouseX, mouseY)) {
            return resetAllButton.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarDragging) {
            scrollbarDragTo(mouseY);
            return true;
        }
        for (var sw : scrollWidgets) {
            if (sw.widget.isFocused() && sw.widget instanceof SliderWidget) {
                if (sw.widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    // ── Reset All ──

    private void resetAllToDefaults() {
        ModConfig.INSTANCE.enableMod.set(true);
        ModConfig.INSTANCE.enableHoverRoll.set(false);
        ModConfig.INSTANCE.enableGlideRoll.set(true);
        ModConfig.INSTANCE.syncRoll.set(true);
        ModConfig.INSTANCE.syncPitch.set(true);
        ModConfig.INSTANCE.syncYaw.set(true);
        ModConfig.INSTANCE.useVanillaVisuals.set(true);
        ModConfig.INSTANCE.switchRollAndYaw.set(false);
        ModConfig.INSTANCE.invertPitch.set(false);
        ModConfig.INSTANCE.momentumBasedMouse.set(false);
        ModConfig.INSTANCE.momentumMouseDeadzone.set(0.2);
        ModConfig.INSTANCE.showMomentumWidget.set(true);
        ModConfig.INSTANCE.disableWhenSubmerged.set(true);
        ModConfig.INSTANCE.showHorizon.set(false);
        ModConfig.INSTANCE.enableBanking.set(true);
        ModConfig.INSTANCE.bankingStrength.set(20.0);
        ModConfig.INSTANCE.simulateControlSurfaceEfficacy.set(false);
        ModConfig.INSTANCE.automaticRighting.set(false);
        ModConfig.INSTANCE.rightingStrength.set(50.0);
        ModConfig.INSTANCE.smoothingPitch.set(1.0);
        ModConfig.INSTANCE.smoothingYaw.set(2.5);
        ModConfig.INSTANCE.smoothingRoll.set(1.0);
        ModConfig.INSTANCE.desktopPitch.set(1.0);
        ModConfig.INSTANCE.desktopYaw.set(0.4);
        ModConfig.INSTANCE.desktopRoll.set(1.0);
        ModConfig.INSTANCE.controllerPitch.set(1.0);
        ModConfig.INSTANCE.controllerYaw.set(0.4);
        ModConfig.INSTANCE.controllerRoll.set(1.0);
        ModConfig.INSTANCE.bankingXFormulaStr.set("sin($roll * TO_RAD) * cos($pitch * TO_RAD) * 10 * $banking_strength");
        ModConfig.INSTANCE.bankingYFormulaStr.set("(-1 + cos($roll * TO_RAD)) * cos($pitch * TO_RAD) * 10 * $banking_strength");
        ModConfig.INSTANCE.elevatorEfficacyFormulaStr.set("$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        ModConfig.INSTANCE.aileronEfficacyFormulaStr.set("$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        ModConfig.INSTANCE.rudderEfficacyFormulaStr.set("$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        ModConfig.SPEC.save();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Forward to EditBox widgets
        for (var sw : scrollWidgets) {
            if (sw.widget instanceof EditBox eb && eb.isFocused()) {
                return eb.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (var sw : scrollWidgets) {
            if (sw.widget instanceof EditBox eb && eb.isFocused()) {
                return eb.charTyped(codePoint, modifiers);
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    // ── Utility ──

    private Component boolText(boolean val) {
        return Component.translatable(val ? "config.dragon_barrel_roll.on" : "config.dragon_barrel_roll.off");
    }

    @Override
    public void onClose() {
        ModConfig.SPEC.save();
        this.minecraft.setScreen(parent);
    }

    // ── Inner widgets ──

    /** Clickable & draggable slider with visible handle. */
    private static class SliderWidget extends AbstractWidget {
        private final ModConfigSpec.DoubleValue value;
        private final double min, max, step;
        private boolean dragging;

        SliderWidget(int x, int y, int width, ModConfigSpec.DoubleValue value, double min, double max, double step) {
            super(x, y, width, 10, Component.literal(""));
            this.value = value;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        private double getProgress() {
            return Math.max(0, Math.min(1, (value.get() - min) / (max - min)));
        }

        private void updateFromMouse(double mouseX) {
            double ratio = Math.max(0, Math.min(1, (mouseX - getX()) / (double) width));
            double newVal = min + ratio * (max - min);
            newVal = Math.round(newVal / step) * step;
            newVal = Math.max(min, Math.min(max, newVal));
            value.set(newVal);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                this.dragging = true;
                updateFromMouse(mouseX);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            this.dragging = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (dragging) {
                updateFromMouse(mouseX);
                return true;
            }
            return false;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int bw = this.width;
            // Track background
            graphics.fill(getX(), getY() + 3, getX() + bw, getY() + 7, 0x88000000);
            // Filled track
            double progress = getProgress();
            int fill = Math.max(0, (int) (bw * progress));
            if (fill > 0) {
                graphics.fill(getX(), getY() + 3, getX() + fill, getY() + 7, 0xCC5A9E5A);
            }
            // Handle knob
            int knobX = getX() + fill - 2;
            if (knobX < getX()) knobX = getX();
            if (knobX > getX() + bw - 4) knobX = getX() + bw - 4;
            graphics.fill(knobX, getY(), knobX + 4, getY() + 10, 0xFFDDDDDD);
            graphics.fill(knobX + 1, getY() + 1, knobX + 3, getY() + 9, dragging ? 0xFFFFFFFF : 0xFFAAAAAA);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
            defaultButtonNarrationText(builder);
        }
    }

    /** Value label that reads current config value every render. */
    private static class ValueLabel extends StringWidget {
        private final ModConfigSpec.DoubleValue configValue;
        private final double max;

        ValueLabel(int x, int y, int width, int height, ModConfigSpec.DoubleValue configValue, double max, Font font) {
            super(x, y, width, height, Component.literal(""), font);
            this.configValue = configValue;
            this.max = max;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            double v = configValue.get();
            var fmt = max < 10.0 ? DF3 : DF;
            setMessage(Component.literal(fmt.format(v)));
            super.renderWidget(graphics, mouseX, mouseY, partialTick);
        }
    }
}
