# Optional Network Payload Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `SyncDragonRoll` payload optional so clients can join servers without the mod (and vice versa), losing sync but keeping local visuals.

**Architecture:** Use NeoForge's `PayloadRegistrar.optional()` to mark the bidirectional play-phase payload as optional during registration. Add null-safety guards around `PacketDistributor.sendToServer()` calls. The existing default-value-0 logic in `DragonRendererMixin` and `PassengerAttachmentMixin` already handles the "no data" case correctly.

**Tech Stack:** NeoForge 21.1.65 networking API, Mixin, Java 21

---

## File Structure

| File | Responsibility |
|------|---------------|
| `gradle.properties:35` | Version declaration, consumed by build and mod metadata |
| `NetworkHandler.java:21-25` | Payload registration — marks `sync_dragon_roll` as optional |
| `EventCallbacksClient.java:37-69` | Client tick → sync logic — guards `sendToServer` with null checks |

**Not modified (existing tolerance for absent data):**
- `SyncDragonRoll.java:35-41` — `getSyncedRollDeg/getSyncedPitch` default to `0f`
- `DragonRendererMixin.java:51-53` — skips rendering when `syncedRoll == 0f && syncedPitch == 0f`
- `PassengerAttachmentMixin.java:53,79` — skips roll application when `rollDeg == 0f`

---

### Task 1: Bump version to 1.0.1

**Files:**
- Modify: `gradle.properties:35`

- [ ] **Step 1: Edit gradle.properties**

```properties
# Line 35, change:
mod_version=1.0.0
# to:
mod_version=1.0.1
```

- [ ] **Step 2: Verify the change**

Run: `grep "mod_version" gradle.properties`
Expected output: `mod_version=1.0.1`

- [ ] **Step 3: Commit**

```bash
git add gradle.properties
git commit -m "chore: bump version to 1.0.1"
```

---

### Task 2: Make SyncDragonRoll payload optional

**Files:**
- Modify: `src/main/java/com/tangwenjun/dragonbarrelroll/net/NetworkHandler.java:21-25`

**Background:** `PayloadRegistrar.optional()` creates a clone with `optional = true`. Subsequent registration calls on that clone pass `optional=true` to `NetworkRegistry.register()`. During handshake, NeoForge skips version/presence checks for optional payloads — the connection proceeds regardless of whether the other side knows about the type.

- [ ] **Step 1: Insert `.optional()` before `playBidirectional`**

The current code (lines 21-25):
```java
        // Bidirectional: client→server (store+broadcast), server→client (store for rendering)
        registrar.playBidirectional(
                SyncDragonRoll.TYPE,
                SyncDragonRoll.STREAM_CODEC,
                new DirectionalPayloadHandler<>(SyncDragonRoll::handleClient, SyncDragonRoll::handleServer)
        );
```

Must become:
```java
        // Bidirectional: client→server (store+broadcast), server→client (store for rendering)
        // Marked optional: clients/servers without this mod can still connect
        registrar.optional().playBidirectional(
                SyncDragonRoll.TYPE,
                SyncDragonRoll.STREAM_CODEC,
                new DirectionalPayloadHandler<>(SyncDragonRoll::handleClient, SyncDragonRoll::handleServer)
        );
```

The edit is a single insertion: add `optional().` after `registrar.` on the `playBidirectional` line.

- [ ] **Step 2: Verify imports are still correct**

No new imports needed — `PayloadRegistrar` is already the return type of `event.registrar()`.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/tangwenjun/dragonbarrelroll/net/NetworkHandler.java
git commit -m "feat: mark SyncDragonRoll payload as optional for cross-mod compatibility"
```

---

### Task 3: Guard sendToServer with null checks

**Files:**
- Modify: `src/main/java/com/tangwenjun/dragonbarrelroll/EventCallbacksClient.java:37-69`

**Background:** `PacketDistributor.sendToServer()` (source: NeoForge 21.1.65 `PacketDistributor.java:46-51`) calls `Objects.requireNonNull(Minecraft.getInstance().getConnection())` — throws NPE if connection is null. When the server doesn't have the mod, the packet is sent over the wire but silently dropped by the server (no handler registered). The null check is the safety mechanism.

- [ ] **Step 1: Guard the cleanup packet (line 45)**

Current code (lines 42-47):
```java
            if (wasFlying && (lastSyncedRoll != 0 || lastSyncedPitch != 0)) {
                var player = client.player;
                if (player != null) {
                    PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), 0, 0, 0));
                }
                lastSyncedRoll = 0;
                lastSyncedPitch = 0;
            }
```

Change to:
```java
            if (wasFlying && (lastSyncedRoll != 0 || lastSyncedPitch != 0)) {
                var player = client.player;
                if (player != null && client.getConnection() != null) {
                    PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), 0, 0, 0));
                }
                lastSyncedRoll = 0;
                lastSyncedPitch = 0;
            }
```

- [ ] **Step 2: Guard the sync packet (line 69)**

Current code (lines 63-69):
```java
        float roll = ((RollEntity) player).doABarrelRoll$getRoll();
        float pitch = player.getXRot();
        float yaw = player.getYRot();

        lastSyncedRoll = roll;
        lastSyncedPitch = pitch;
        PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), roll, pitch, yaw));
```

Change to:
```java
        float roll = ((RollEntity) player).doABarrelRoll$getRoll();
        float pitch = player.getXRot();
        float yaw = player.getYRot();

        lastSyncedRoll = roll;
        lastSyncedPitch = pitch;
        if (client.getConnection() != null) {
            PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), roll, pitch, yaw));
        }
```

The `lastSyncedRoll`/`lastSyncedPitch` assignments stay outside the guard — they track state for the cleanup packet, which needs them regardless of connection.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/tangwenjun/dragonbarrelroll/EventCallbacksClient.java
git commit -m "fix: guard sendToServer against null connection for optional payload"
```

---

### Task 4: Build verification

- [ ] **Step 1: Clean build**

```bash
./gradlew clean build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: Verify the JAR version**

```bash
jar tf build/libs/dragon_barrel_roll-1.0.1.jar | head -20
```

Expected: jar contains all mod classes, no errors.

- [ ] **Step 3: Final commit (if any build fixes)**

If the build revealed issues (e.g., import errors), fix them and commit. Otherwise, no additional commit needed.

---

## Verification Checklist (post-implementation)

- [ ] Singleplayer (`runClient`): barrel roll visuals work normally
- [ ] Multiplayer with mod on both sides: sync works, remote dragons tilt correctly
- [ ] Client with mod → vanilla server: client can join, local visuals work, no crash
- [ ] Vanilla client → server with mod: client can join, no crash (no visuals expected)
- [ ] No `NullPointerException` in logs during world join/leave transitions
