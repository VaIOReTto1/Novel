# 设备侧 Compile Blocker 记录 - 2026-03-21

## 场景
- 在 benchmark 外部直接验证连接设备是否支持 package compile。

## 命令
- `adb shell cmd package compile -f -m speed-profile com.novel`
- `adb shell cmd package compile -f -m speed-profile com.android.settings`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- ROM 指纹：`OnePlus/DN2101IND/OP515BL1:13/TP1A.220905.001/R.221a094-16f10:user/release-keys`

## 预期
- 如果 blocker 是应用特有问题，命令应只在 `com.novel` 上失败。
- 如果 blocker 是设备或 ROM 级问题，其他无关包也会同样失败。

## 实际结果
- 两条命令都返回相同错误：
  - `Error: Failed to cpmpile !`
- 由于 `com.android.settings` 也同样失败，当前默认将该问题认定为设备 / ROM 侧 compile blocker，而不是 `com.novel` 运行时回归。

## 证据
- `docs/refactor/evidence/device-compile-blocker-2026-03-21.txt`
- `docs/refactor/evidence/baseline-profile-benchmark-testlog-2026-03-21.txt`

## 结论
- `接受为环境阻塞项`

## 残余风险
- 这个判断仍需要第二设备复验，才能进一步证明 compiled-mode 性能门禁可以脱离 `DN2101`。
