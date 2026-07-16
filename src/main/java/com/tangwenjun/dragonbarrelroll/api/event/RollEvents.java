package com.tangwenjun.dragonbarrelroll.api.event;

import com.tangwenjun.dragonbarrelroll.impl.event.EventImpl;

public interface RollEvents {
    Event<ShouldRollCheckEvent> SHOULD_ROLL_CHECK = new EventImpl<>();

    interface ShouldRollCheckEvent {
        boolean shouldRoll();
    }

    static boolean shouldRoll() {
        for (var listener : SHOULD_ROLL_CHECK.getListeners()) {
            if (listener.shouldRoll()) {
                return true;
            }
        }

        return false;
    }

    Event<CameraModifiersEvent> EARLY_CAMERA_MODIFIERS = new EventImpl<>();

    static void earlyCameraModifiers(RollContext context) {
        for (var listener : EARLY_CAMERA_MODIFIERS.getListeners()) {
            listener.applyCameraModifiers(context);
        }
    }

    Event<CameraModifiersEvent> LATE_CAMERA_MODIFIERS = new EventImpl<>();

    static void lateCameraModifiers(RollContext context) {
        for (var listener : LATE_CAMERA_MODIFIERS.getListeners()) {
            listener.applyCameraModifiers(context);
        }
    }

    interface CameraModifiersEvent {
        void applyCameraModifiers(RollContext context);
    }
}
