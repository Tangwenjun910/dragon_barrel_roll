package com.tangwenjun.dragonbarrelroll.mixin.client.roll.entity;

import net.minecraft.client.player.LocalPlayer;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.tangwenjun.dragonbarrelroll.api.event.RollContext;
import com.tangwenjun.dragonbarrelroll.api.event.RollEvents;
import com.tangwenjun.dragonbarrelroll.api.rotation.RotationInstant;
import com.tangwenjun.dragonbarrelroll.config.Sensitivity;
import com.tangwenjun.dragonbarrelroll.flight.RotationModifiers;
import com.tangwenjun.dragonbarrelroll.math.MagicNumbers;
import com.tangwenjun.dragonbarrelroll.mixin.roll.entity.PlayerEntityMixin;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {
	@Unique
	private float do_a_barrel_roll$renderYaw;
	@Unique
	private float do_a_barrel_roll$lastRenderYaw;
	@Unique
	private boolean do_a_barrel_roll$renderYawInit;

	@Override
	@Unique
	protected void doABarrelRoll$baseTickTail2() {
		// Update rolling status
		doABarrelRoll$setRolling(RollEvents.shouldRoll());
	}

	@Override
	public void doABarrelRoll$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta) {
		var rotDelta = RotationInstant.of(pitch, yaw, roll);
		var currentRoll = doABarrelRoll$getRoll();
		var currentRotation = RotationInstant.of(
				getXRot(),
				getYRot(),
				currentRoll
		);
		var context = RollContext.of(currentRotation, rotDelta, mouseDelta);

		context.useModifier(RotationModifiers.fixNaN("INPUT"));
		RollEvents.earlyCameraModifiers(context);
		context.useModifier(RotationModifiers.fixNaN("EARLY_CAMERA_MODIFIERS"));
		context.useModifier((rotation, ctx) -> rotation.applySensitivity(sensitivity));
		context.useModifier(RotationModifiers.fixNaN("SENSITIVITY"));
		RollEvents.lateCameraModifiers(context);
		context.useModifier(RotationModifiers.fixNaN("LATE_CAMERA_MODIFIERS"));

		rotDelta = context.getRotationDelta();

		doABarrelRoll$changeElytraLook((float) rotDelta.pitch(), (float) rotDelta.yaw(), (float) rotDelta.roll());
	}

	@Override
	public void doABarrelRoll$changeElytraLook(float pitch, float yaw, float roll) {
		// Initialize continuous render yaw tracking on first call
		if (!do_a_barrel_roll$renderYawInit) {
			do_a_barrel_roll$renderYaw = getYRot();
			do_a_barrel_roll$lastRenderYaw = getYRot();
			do_a_barrel_roll$renderYawInit = true;
		}

		var currentPitch = getXRot();
		var currentYaw = getYRot();
		var currentRoll = doABarrelRoll$getRoll();

		// Convert pitch, yaw, and roll to a facing and left vector
		var facing = new Vector3d(getLookAngle().toVector3f());
		var left = new Vector3d(1, 0, 0);
		left.rotateZ(-currentRoll * MagicNumbers.TORAD);
		left.rotateX(-currentPitch * MagicNumbers.TORAD);
		left.rotateY(-(currentYaw + 180) * MagicNumbers.TORAD);


		// Apply pitch
		facing.rotateAxis(-0.15 * pitch * MagicNumbers.TORAD, left.x, left.y, left.z);

		// Apply yaw
		var up = facing.cross(left, new Vector3d());
		facing.rotateAxis(0.15 * yaw * MagicNumbers.TORAD, up.x, up.y, up.z);
		left.rotateAxis(0.15 * yaw * MagicNumbers.TORAD, up.x, up.y, up.z);

		// Apply roll
		left.rotateAxis(0.15 * roll * MagicNumbers.TORAD, facing.x, facing.y, facing.z);


		// Extract new pitch, yaw, and roll
		double newPitch = -Math.asin(facing.y) * MagicNumbers.TODEG;
		double newYaw = -Math.atan2(facing.x, facing.z) * MagicNumbers.TODEG;

		var normalLeft = new Vector3d(1, 0, 0).rotateY(-(newYaw + 180) * MagicNumbers.TORAD);
		double newRoll = -Math.atan2(left.cross(normalLeft, new Vector3d()).dot(facing), left.dot(normalLeft)) * MagicNumbers.TODEG;

		// Calculate deltas
		double deltaY = newPitch - currentPitch;
		double deltaX = newYaw - currentYaw;
		double deltaRoll = newRoll - currentRoll;

		// Track render yaw before turn
		float oldRenderYaw = do_a_barrel_roll$renderYaw;

		// Apply vanilla pitch and yaw
		turn(deltaX / 0.15, deltaY / 0.15);

		// Update continuous render yaw tracking
		do_a_barrel_roll$lastRenderYaw = oldRenderYaw;
		do_a_barrel_roll$renderYaw = oldRenderYaw + (float) deltaX;

		// Apply roll
		this.roll += (float) deltaRoll;
		this.prevRoll += (float) deltaRoll;

		// fix hand spasm when wrapping yaw value
		if (getYRot() < -90 && do_a_barrel_roll$renderYaw > 90) {
			do_a_barrel_roll$renderYaw -= 360;
			do_a_barrel_roll$lastRenderYaw -= 360;
		} else if (getYRot() > 90 && do_a_barrel_roll$renderYaw < -90) {
			do_a_barrel_roll$renderYaw += 360;
			do_a_barrel_roll$lastRenderYaw += 360;
		}
	}
}
