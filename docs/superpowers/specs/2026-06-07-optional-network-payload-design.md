# Optional Network Payload — 客户端/服务端可选安装

**日期**: 2026-06-07
**状态**: 待实施
**关联**: Dragon Barrel Roll v1.0.1

## 目标

允许 Dragon Barrel Roll 模组在客户端和/或服务端**单独安装**，不必双方都安装。只有双方都安装时才启用翻滚同步效果。

## 背景

目前 `SyncDragonRoll` 网络包通过 `registrar.playBidirectional()` 注册为**必需**载荷。NeoForge 握手阶段要求双方都认识该包类型，否则连接被拒绝。这导致：

- 客户端有模组 + 服务端无模组 → ❌ 无法加入
- 服务端有模组 + 客户端无模组 → ❌ 无法加入

## 方案

使用 NeoForge 的 `optional()` payload 注册机制，将 `sync_dragon_roll` 包标记为可选。握手时对方不认识该包类型也不会断开连接。

### 涉及文件

| 文件 | 变更 | 说明 |
|------|------|------|
| `gradle.properties` | 改 | `mod_version`: `1.0.0` → `1.0.1` |
| `NetworkHandler.java` | 改 | `registrar.playBidirectional(...)` → `registrar.optional().playBidirectional(...)` |
| `EventCallbacksClient.java` | 改 | `sendToServer` 加 null check + try-catch 双保险 |

### 不需要改的文件

这些文件已有内置的"无数据"容错逻辑：

- **`DragonRendererMixin.java`**（第 51-53 行）：远程玩家 `syncedRoll == 0f && syncedPitch == 0f` → 直接 return，不渲染倾斜
- **`PassengerAttachmentMixin.java`**（第 53、79 行）：`rollDeg == 0f` → 不应用滚转
- **`SyncDragonRoll.java`**：默认值 `0f` 即为「无翻滚」状态

### 运行时行为

| 场景 | v1.0.0 | v1.0.1 |
|------|--------|--------|
| 客户端有模组 / 单机 | ✅ | ✅ |
| 客户端有模组 + 服务端有模组 | ✅ 同步正常 | ✅ 同步正常 |
| 客户端有模组 + 服务端无模组 | ❌ 无法连接 | ✅ 可加入，本地视觉效果正常，无多人同步 |
| 客户端无模组 + 服务端有模组 | ❌ 无法连接 | ✅ 可加入，该客户端无翻滚效果 |

## 详细变更

### 1. gradle.properties（第 35 行）

```properties
# 修改前
mod_version=1.0.0

# 修改后
mod_version=1.0.1
```

### 2. NetworkHandler.java

```java
// 修改前
registrar.playBidirectional(
        SyncDragonRoll.TYPE,
        SyncDragonRoll.STREAM_CODEC,
        new DirectionalPayloadHandler<>(SyncDragonRoll::handleClient, SyncDragonRoll::handleServer)
);

// 修改后
registrar.optional().playBidirectional(
        SyncDragonRoll.TYPE,
        SyncDragonRoll.STREAM_CODEC,
        new DirectionalPayloadHandler<>(SyncDragonRoll::handleClient, SyncDragonRoll::handleServer)
);
```

**API 验证说明**：实施时需确认 NeoForge 21.1.65 中 `registrar.optional()` 的确切返回类型及 `playBidirectional` 的可用性。若 API 形态为链式 `.optional()` 在后方调用（如 `playBidirectional(...).optional()`），按实际 API 调整。

### 3. EventCallbacksClient.java — syncBarrelRollData()

在两个 `PacketDistributor.sendToServer(...)` 调用处分别加保护：

```java
// 清零包（第 45 行附近）
if (client.player != null && client.getConnection() != null) {
    try {
        PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), 0, 0, 0));
    } catch (Exception ignored) {
        // Server doesn't have the mod — packet silently dropped
    }
}

// 正常同步包（第 69 行附近）
if (client.player != null && client.getConnection() != null) {
    try {
        PacketDistributor.sendToServer(new SyncDragonRoll(player.getId(), roll, pitch, yaw));
    } catch (Exception ignored) {
        // Server doesn't have the mod — packet silently dropped
    }
}
```

## 边界情况

| 边界 | 处理方式 |
|------|----------|
| `client.getConnection()` 为 null（未连接任何服务器） | null check → 跳过发送 |
| 飞行开始/结束的过渡 | 清零包逻辑不变，`wasFlying` / `lastSyncedRoll` / `lastSyncedPitch` 状态正常维护 |
| 服务端中途安装/卸载模组 | 需要重新连接（标准 Minecraft 行为） |
| 局域网联机 | 与专用服务器行为一致 |

## 测试要点

1. **单机**（`runClient`）：功能不退化
2. **双方安装**（`runClient` + `runServer`）：同步正常，远程玩家龙倾斜可见
3. **客户端安装 + 原版服务端**：客户端能加入，本地翻滚正常，不崩溃
4. **原版客户端 + 模组服务端**：客户端能加入，无翻滚效果，不崩溃
5. **飞行开始/停止**：清零包正常发送（有模组服务器）或被安全丢弃（无模组服务器）

## 弃用/回退

无破坏性变更。如需回退，将 `optional()` 移除即可恢复 v1.0.0 行为。
