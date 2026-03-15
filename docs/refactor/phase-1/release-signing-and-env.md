# Phase 1 - Release 签名与环境注入说明

## 目标
- 为 `V1-08` 提供可追溯的签名与环境注入说明。
- 明确当前本地、CI 和后续正式发布时的参数来源与回退策略。

## 当前实现
来源：
- `android/app/build.gradle`

### Release signing 脚手架
- 当前已支持以下来源读取正式签名参数：
  - Gradle 属性
  - 环境变量
- 使用的键：
  - `NOVEL_RELEASE_STORE_FILE`
  - `NOVEL_RELEASE_STORE_PASSWORD`
  - `NOVEL_RELEASE_KEY_ALIAS`
  - `NOVEL_RELEASE_KEY_PASSWORD`
- 当前行为：
  - 若上述参数完整提供，则 `release` 使用正式签名配置
  - 若未提供，则 `release` 临时回退到 `debug` 签名，确保本地仍可完成构建验证

### API Host 环境注入
- 当前已支持以下来源注入 API host：
  - Gradle 属性 `NOVEL_API_BASE_HOST`
  - 环境变量 `NOVEL_API_BASE_HOST`
- 当前行为：
  - 若提供注入值，`BuildConfig.API_BASE_*` 使用注入值生成
  - 若未提供，则暂时回退到本地 fallback 地址，用于当前阶段构建和验证

## 本地开发建议
- 本地若需要验证 release-like 配置，可使用以下方式注入：
```powershell
cd android
.\gradlew.bat app:assembleRelease `
  -PNOVEL_API_BASE_HOST=https://example.com `
  -PNOVEL_RELEASE_STORE_FILE=keystore/release.jks `
  -PNOVEL_RELEASE_STORE_PASSWORD=*** `
  -PNOVEL_RELEASE_KEY_ALIAS=*** `
  -PNOVEL_RELEASE_KEY_PASSWORD=***
```

## CI 注入建议
- CI 中不要把签名信息写入仓库。
- 推荐通过安全变量注入：
  - `NOVEL_RELEASE_STORE_FILE`
  - `NOVEL_RELEASE_STORE_PASSWORD`
  - `NOVEL_RELEASE_KEY_ALIAS`
  - `NOVEL_RELEASE_KEY_PASSWORD`
  - `NOVEL_API_BASE_HOST`
- 若需要在 CI 中生成临时 keystore 路径，应保证：
  - 构建结束后清理
  - 不进入制品归档

## 当前回退策略
- Release 未提供正式签名参数时：
  - 允许回退到 debug signing
  - 目的是确保 Phase 1 的构建验证不中断
- API host 未提供注入值时：
  - 暂时回退到本地 fallback 地址
  - 目的是确保当前阶段构建与验证可持续

## 后续收口建议
- Phase 1 后半段：
  - 将 release 环境下的 API host 改为必须显式注入
  - 将正式 release 包构建切换为必须显式提供签名参数
- Phase 2：
  - 把 CI 的环境变量注入和校验纳入自动门禁

## 当前结论
- 当前 release signing 与环境注入已经从“代码硬编码”推进到“脚手架 + 参数协议”阶段。
- 下一步只需把 CI 注入与正式发布要求收紧，即可进一步推动 `V1-08` 从 `in_progress` 向关闭靠近。
