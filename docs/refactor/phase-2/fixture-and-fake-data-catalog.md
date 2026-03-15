# Phase 2 - Fixture 与 Fake Data 目录

## 目标
- 将 `V2-03` 从“方案”推进到“代码可复用”的阶段。
- 为后续 reducer/usecase/repository JVM 测试与 smoke 提供稳定样本。

## 当前落地位置

### 测试资源
- `android/app/src/test/resources/fixtures/reader_history_sample.json`
- `android/app/src/test/resources/fixtures/user_defaults_sample.json`
- `android/app/src/test/resources/fixtures/home_books_sample.json`

### 测试辅助代码
- `android/app/src/test/java/com/novel/testing/FixtureLoader.kt`
- `android/app/src/test/java/com/novel/testing/FixtureCatalog.kt`
- `android/app/src/test/java/com/novel/testing/FakeReaderHistorySource.kt`
- `android/app/src/test/java/com/novel/testing/FixtureCatalogTest.kt`

## 当前覆盖的样本类型
- 阅读历史样本
- 阅读设置 / 用户偏好样本
- 首页推荐书籍样本

## 当前结论
- Phase 2 已具备首批可复用的 fixture 和 fake data 基础层。
- 下一步可直接在 reducer/usecase/repository JVM 测试中复用这些样本，而不必继续直接依赖在线数据。
