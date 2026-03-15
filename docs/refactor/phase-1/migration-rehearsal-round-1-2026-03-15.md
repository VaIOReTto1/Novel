# Phase 1 - 迁移演练首轮记录（2026-03-15）

## 演练目标
- 完成一轮最小可执行的“重装保留数据”验证。
- 验证当前安装包重装后，数据库文件与用户偏好文件是否仍然保留。
- 为 `V1-06` 提供首轮实操证据。

## 演练类型
- 类型：同版本重装保留数据验证
- 设备：`192.168.8.130:5555`
- 应用包：`android/app/build/outputs/apk/debug/app-debug.apk`

## 演练前样本

### 数据库目录
```text
RKStorage
kxq.db
kxq.db-shm
kxq.db-wal
```

### SharedPreferences 目录
```text
Novel_user_defaults.xml
AwOriginVisitLoggerPrefs.xml
WebViewChromiumPrefs.xml
com.novel_preferences.xml
dialog_launch_prefs.xml
```

### 关键偏好字段
- `reading_progress_data` 存在
- `reading_history` 存在
- `backgroundColor = #FFF5F5DC`
- `brightness = 0.5`
- `fontSize = 16`
- `pageFlipEffect = PAGECURL`
- `textColor = #FF2E2E2E`

## 执行步骤
1. 使用 `run-as` 读取重装前数据库目录和 shared_prefs 目录。
2. 使用 `run-as` 读取 `Novel_user_defaults.xml`。
3. 执行：
   - `adb install -r -d -g android/app/build/outputs/apk/debug/app-debug.apk`
4. 再次读取数据库目录、shared_prefs 目录和 `Novel_user_defaults.xml`。
5. 对比关键文件与关键字段。

## 演练结果

### 数据库目录对比
- 重装前后均存在：
  - `kxq.db`
  - `kxq.db-shm`
  - `kxq.db-wal`
  - `RKStorage`
- 未观察到数据库目录被清空或重建。

### SharedPreferences 对比
- 重装前后均存在：
  - `Novel_user_defaults.xml`
  - `AwOriginVisitLoggerPrefs.xml`
  - `WebViewChromiumPrefs.xml`
  - `com.novel_preferences.xml`
  - `dialog_launch_prefs.xml`

### 关键偏好字段对比
- 以下字段重装前后保持一致：
  - `reading_progress_data`
  - `reading_history`
  - `backgroundColor`
  - `brightness`
  - `fontSize`
  - `pageFlipEffect`
  - `textColor`

## 未覆盖项
- 本轮未覆盖：
  - 旧数据库版本升级到新版本
  - KeyChain 中真实 token 的保留读取
  - 升级中断恢复
- 当前设备状态下未发现 `Novel_keystore_prefs.xml`，因此本轮未能验证 token 样本保留。

## 结论
- 已完成 `V1-06` 的首轮实操演练。
- 当前轮次可证明：
  - 重装当前包不会清除 Room 数据库文件
  - 关键 SharedPreferences 偏好可保留
- 后续仍建议补：
  - 含 token 样本的 KeyChain 保留验证
  - 旧 schema 升级样本
  - 升级中断恢复样本
