# Phase 1 - 权限矩阵

## 目标
- 将当前 Android 权限使用情况、适用系统范围、运行时依赖与后续治理方向整理成可审查的矩阵。
- 为 `V1-03` 提供正式文档证据，而不是仅依赖 manifest 变更。

## 当前 Manifest 权限
来源：
- `android/app/src/main/AndroidManifest.xml`

| 权限 | 当前状态 | 适用范围 | 当前用途 | 备注 |
| --- | --- | --- | --- | --- |
| `android.permission.ACCESS_NETWORK_STATE` | 保留 | 全版本 | 网络状态感知 | 合理保留 |
| `android.permission.INTERNET` | 保留 | 全版本 | 网络请求、RN、WebView | 合理保留 |
| `android.permission.READ_PHONE_NUMBERS` | 保留 | Android 13+ 运行时逻辑使用 | 手机号获取 | 与 `PhoneInfoUtil` 逻辑一致 |
| `android.permission.READ_PHONE_STATE` | 保留 | `maxSdkVersion=32` | Android 12 及以下手机号读取 | 已限制范围 |
| `android.permission.VIBRATE` | 保留 | 全版本 | 触觉反馈 | 合理保留 |
| `READ_PRIVILEGED_PHONE_STATE` | 已移除 | 不适用于普通应用 | 无 | Phase 1 已清理 |

## 运行时逻辑映射

### 手机号读取
来源：
- `android/app/src/main/java/com/novel/utils/PhoneInfoUtil.kt`

| 系统版本 | 运行时权限 | 当前行为 |
| --- | --- | --- |
| Android 13+ | `READ_PHONE_NUMBERS` | 通过 `SubscriptionManager.getPhoneNumber()` 获取 |
| Android 12 及以下 | `READ_PHONE_STATE` | 通过 `TelephonyManager.line1Number` 获取 |

## 当前判断
- 当前 manifest 与运行时逻辑已经基本一致。
- `READ_PRIVILEGED_PHONE_STATE` 的移除，消除了最明显的不合规项。
- `READ_PHONE_STATE` 的 `maxSdkVersion=32` 已把旧权限范围显式限定到旧系统。

## 后续治理建议
- 若后端或产品流程允许，继续评估是否可以完全移除手机号自动读取能力，改为用户手输。
- 若保留手机号自动读取：
  - 在 UI 层明确权限申请时机
  - 在隐私协议中补充用途说明
  - 在拒绝权限场景下提供降级路径

## 通过标准
- Manifest 权限与运行时逻辑一致
- 不包含普通应用不应声明的权限
- 旧版权限具备清晰的系统范围限制
- 每个保留权限都能说清用途与降级路径
