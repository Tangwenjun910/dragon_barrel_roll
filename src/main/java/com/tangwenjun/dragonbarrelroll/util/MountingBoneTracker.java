package com.tangwenjun.dragonbarrelroll.util;

import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores the entity-relative position of the dragon's MountingBone,
 * captured during {@code DragonRenderer.postRender} for use in
 * {@code getPassengerAttachmentPoint}.
 * <p>
 * Thread-safe for concurrent render/tick access.
 */
public final class MountingBoneTracker {
    private static final ConcurrentMap<Integer, Vec3> BONE_POSITIONS = new ConcurrentHashMap<>();

    private MountingBoneTracker() {}

    public static void store(int entityId, Vec3 entityRelativePos) {
        BONE_POSITIONS.put(entityId, entityRelativePos);
    }

    public static Vec3 get(int entityId) {
        return BONE_POSITIONS.get(entityId);
    }

    public static void remove(int entityId) {
        BONE_POSITIONS.remove(entityId);
    }
}
