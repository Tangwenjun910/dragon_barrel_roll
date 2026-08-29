# Dragon Barrel Roll（龙桶滚）

为《龙之生存》（Dragon Survival）添加桶滚飞行机制——俯仰、偏航、滚转，像战斗机一样自由飞翔。  
Adds barrel roll flight mechanics to Dragon Survival — pitch, yaw, and roll freely like a fighter jet.

> **支持版本 / Supported versions**  
> Minecraft 1.21.1 · NeoForge 21.1.65+ · DragonSurvival 2.0.66+

---

## 特性 / Features

- 为龙之生存的龙飞行加入完整的桶滚操控：**俯仰（Pitch）、偏航（Yaw）、滚转（Roll）**。
- 鼠标与键盘混合操控，支持自定义按键。
- 可选的**动量鼠标（Momentum Mouse）**输入模式。
- 滚转、俯仰、偏航可同步到龙模型，并在多人游戏中同步给其他玩家。
- 飞行物理增强：**倾斜（Banking）**、**平滑（Smoothing）**、**自动回正（Auto Righting）**、**模拟舵面效能（Control Surface Efficacy）**。
- HUD 地平线指示器（Horizon Line）。
- 内置配置界面，支持中文与英文。
- 与原版 Do A Barrel Roll（DABR）兼容：DABR 继续负责人类/鞘翅飞行，本模组负责龙飞行。

---

## 环境要求 / Requirements

| 项目 | 要求 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.65+ |
| DragonSurvival（龙之生存） | 2.0.66+ |
| 可选模组 | Do A Barrel Roll（DABR） |

> 多人游戏建议同时安装到客户端和服务器，以获得完整的飞行姿态同步效果。

---

## 安装 / Installation

1. 安装对应版本的 Minecraft、NeoForge 与 DragonSurvival。
2. 将 `dragon_barrel_roll` 的 jar 文件放入游戏目录的 `mods` 文件夹。
3. 启动游戏即可。

---

## 使用说明 / Usage

1. 成为龙并展开翅膀进入飞行状态。
2. 默认情况下，**滑翔（冲刺飞行）时启用桶滚**；悬停（非冲刺飞行）时需要在配置中开启“悬停桶滚”。
3. 使用鼠标控制**俯仰**和**滚转**，使用 **A / D** 控制**偏航**。
4. 按 **O** 可快速切换整个模组的启用状态。
5. 在“选项 → 控制 → 龙桶滚”或“Mod 设置”中修改按键与配置。

### 默认按键 / Default Keybinds

| 功能 | 默认按键 |
| --- | --- |
| 切换启用 / Toggle Enabled | `O` |
| 打开配置 / Open Config | 未绑定 |
| 俯仰向上 / Pitch Up | 未绑定 |
| 俯仰向下 / Pitch Down | 未绑定 |
| 偏航向左 / Yaw Left | `A` |
| 偏航向右 / Yaw Right | `D` |
| 滚转向左 / Roll Left | 未绑定 |
| 滚转向右 / Roll Right | 未绑定 |
| 鼠标 / Mouse | 俯仰 + 滚转（可在配置中交换滚转与偏航） |

> “未绑定”的按键可以到游戏的控制设置中自行指定。

---

## 配置 / Configuration

- 配置界面：通过“Mod 设置”或绑定的“打开配置”按键打开。
- 配置文件：`config/dragon_barrel_roll-client.toml`

### 常用配置项 / Common Options

| 配置项 | 说明 | 默认值 |
| --- | --- | --- |
| 启用模组 / Enable Mod | 整个模组的总开关 | 开启 |
| 悬停桶滚 / Hover Barrel Roll | 允许在悬停（非冲刺飞行）时桶滚 | 关闭 |
| 滑翔桶滚 / Glide Barrel Roll | 允许在滑翔（冲刺飞行）时桶滚 | 开启 |
| 模型同步 / Model Sync | 将滚转/俯仰/偏航同步到龙模型 | 开启 |
| 骑乘时禁用模型倾斜 | 携带乘客时龙模型、骑乘者、骑乘点不跟随倾斜，仅保留第一人称滚转 | 开启 |
| 交换滚转/偏航 | 开启后鼠标横向移动改为控制偏航，滚转交给按键 | 关闭 |
| 反转俯仰 / Invert Pitch | 反转俯仰方向 | 关闭 |
| 动量鼠标 / Momentum Mouse | 使用基于动量的鼠标输入 | 关闭 |
| 动量死区 / Momentum Deadzone | 动量鼠标输入的死区范围 | 0.2 |
| 水下禁用 / Disable Underwater | 在水下时禁用模组效果 | 开启 |
| 显示地平线 / Show Horizon | 显示地平线指示器 | 关闭 |
| 启用倾斜效果 / Enable Banking | 飞行时加入倾斜效果 | 开启 |
| 倾斜强度 / Banking Strength | 倾斜效果强度倍率 | 20.0 |
| 模拟舵面效能 | 基于速度模拟控制面效能 | 关闭 |
| 自动回正 / Auto Righting | 未主动滚转时自动回正 | 关闭 |
| 回正力度 / Righting Strength | 自动回正力度 | 50.0 |
| 平滑度 / Smoothing | 俯仰 / 偏航 / 滚转平滑系数 | 1.0 / 2.5 / 1.0 |
| 键盘灵敏度 / Desktop Sensitivity | 键盘俯仰 / 偏航 / 滚转灵敏度（度/秒） | 1.0 / 0.4 / 1.0 |
| 手柄灵敏度 / Controller Sensitivity | 手柄俯仰 / 偏航 / 滚转灵敏度（度/秒） | 1.0 / 0.4 / 1.0 |
| 高级公式 / Advanced Formulas | 倾斜、升降舵、副翼、方向舵等公式（专家向） | 默认公式 |

---

## 兼容性 / Compatibility

- 必须安装 **DragonSurvival 2.0.66+**。
- 若同时安装 **Do A Barrel Roll（DABR）**：
  - DABR 继续负责**人类/鞘翅飞行**；
  - 本模组负责**龙飞行**；
  - 会自动禁用 DragonSurvival 原生的 DABR 龙飞行兼容逻辑，避免冲突。
- 网络包为可选（optional）设计：未安装本模组的客户端/服务器仍可连接；但只有双方都安装时，其他玩家才能看到完整的龙飞行姿态同步。

---

## 许可证 / License

本项目基于 **GPL-3.0** 许可证发布。
