#!/bin/bash

# Baseline Profile 生成和验证脚本
# 用于自动化Baseline Profile的生成、验证和集成流程

set -e  # 遇到错误立即退出

# 脚本配置
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
ANDROID_DIR="$PROJECT_ROOT/android"
MACROBENCHMARK_DIR="$ANDROID_DIR/macrobenchmark"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Android SDK和工具
check_android_tools() {
    log_info "检查Android开发环境..."
    
    if ! command -v adb &> /dev/null; then
        log_error "ADB未找到，请确保Android SDK已正确安装并添加到PATH"
        exit 1
    fi
    
    if ! command -v ./gradlew &> /dev/null; then
        log_error "Gradle Wrapper未找到，请在Android项目根目录运行此脚本"
        exit 1
    fi
    
    log_success "Android开发环境检查通过"
}

# 检查设备连接
check_device_connection() {
    log_info "检查设备连接状态..."
    
    DEVICE_COUNT=$(adb devices | grep -c "device$" || true)
    
    if [ "$DEVICE_COUNT" -eq 0 ]; then
        log_error "未检测到连接的Android设备"
        log_info "请确保："
        log_info "1. 设备已连接并启用USB调试"
        log_info "2. 已授权此计算机的调试权限"
        exit 1
    elif [ "$DEVICE_COUNT" -gt 1 ]; then
        log_warning "检测到多个设备，将使用第一个设备"
    fi
    
    DEVICE_NAME=$(adb devices | grep "device$" | head -1 | cut -f1)
    log_success "设备连接正常: $DEVICE_NAME"
}

# 构建应用
build_app() {
    log_info "构建Novel应用..."
    
    cd "$ANDROID_DIR"
    
    # 清理之前的构建
    ./gradlew clean
    
    # 构建benchmark和应用
    ./gradlew :app:assembleBenchmark
    ./gradlew :macrobenchmark:assembleBenchmark
    
    log_success "应用构建完成"
}

# 安装应用
install_app() {
    log_info "安装Novel应用到设备..."
    
    cd "$ANDROID_DIR"
    
    # 卸载现有应用（如果存在）
    adb uninstall com.novel 2>/dev/null || true
    
    # 安装benchmark版本
    ./gradlew :app:installBenchmark
    
    log_success "应用安装完成"
}

# 生成Baseline Profile
generate_profile() {
    log_info "开始生成Baseline Profile..."
    
    cd "$ANDROID_DIR"
    
    # 运行Baseline Profile生成器
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.BaselineProfileGenerator
    
    log_success "Baseline Profile生成完成"
}

# 运行性能基准测试
run_benchmarks() {
    log_info "运行启动性能基准测试..."
    
    cd "$ANDROID_DIR"
    
    # 运行启动性能测试
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark
    
    # 运行滚动性能测试
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ScrollPerformanceBenchmark
    
    log_success "性能基准测试完成"
}

# 验证Profile文件
verify_profile() {
    log_info "验证生成的Baseline Profile..."
    
    PROFILE_PATH="$ANDROID_DIR/app/src/main/baseline-prof.txt"
    
    if [ ! -f "$PROFILE_PATH" ]; then
        log_error "Baseline Profile文件未找到: $PROFILE_PATH"
        return 1
    fi
    
    # 检查文件大小
    PROFILE_SIZE=$(wc -l < "$PROFILE_PATH")
    if [ "$PROFILE_SIZE" -lt 10 ]; then
        log_warning "Baseline Profile文件较小 ($PROFILE_SIZE 行)，可能生成不完整"
    else
        log_success "Baseline Profile文件验证通过 ($PROFILE_SIZE 行)"
    fi
    
    # 显示Profile内容预览
    log_info "Profile内容预览 (前10行):"
    head -10 "$PROFILE_PATH" | while read line; do
        echo "  $line"
    done
}

# 构建生产版本验证
build_release_with_profile() {
    log_info "构建包含Baseline Profile的Release版本..."
    
    cd "$ANDROID_DIR"
    
    # 构建Release版本
    ./gradlew :app:assembleRelease
    
    RELEASE_APK="$ANDROID_DIR/app/build/outputs/apk/release/app-release.apk"
    
    if [ -f "$RELEASE_APK" ]; then
        log_success "Release APK构建成功: $RELEASE_APK"
        
        # 检查APK大小
        APK_SIZE=$(du -h "$RELEASE_APK" | cut -f1)
        log_info "APK大小: $APK_SIZE"
    else
        log_error "Release APK构建失败"
        return 1
    fi
}

# 性能对比测试
run_performance_comparison() {
    log_info "运行性能对比测试..."
    
    cd "$ANDROID_DIR"
    
    log_info "测试不同编译模式下的启动性能..."
    
    # 测试无编译模式
    log_info "测试无编译模式..."
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark \
        -Pandroid.testInstrumentationRunnerArguments.method=startupNoCompilation
    
    # 测试Baseline Profile模式
    log_info "测试Baseline Profile模式..."
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark \
        -Pandroid.testInstrumentationRunnerArguments.method=startupBaselineProfile
    
    # 测试完全编译模式
    log_info "测试完全编译模式..."
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark \
        -Pandroid.testInstrumentationRunnerArguments.method=startupFullCompilation
    
    log_success "性能对比测试完成"
}

# 生成性能报告
generate_report() {
    log_info "生成性能优化报告..."
    
    REPORT_FILE="$PROJECT_ROOT/baseline_profile_report.md"
    
    cat > "$REPORT_FILE" << EOF
# Baseline Profile 性能优化报告

## 生成时间
$(date)

## 优化内容

### 1. 冷启动优化
- ✅ 实现延迟初始化策略
- ✅ 将非关键组件初始化移至后台线程
- ✅ 优化Application类初始化流程
- ✅ 添加启动性能监控

### 2. Room数据库优化
- ✅ 启用WAL模式提升并发性能
- ✅ 添加FTS5全文搜索支持
- ✅ 优化查询索引策略
- ✅ 添加数据库性能监控

### 3. Baseline Profiles实现
- ✅ 配置Macrobenchmark模块
- ✅ 录制关键用户路径Profile
- ✅ 生成生产环境Profile
- ✅ 集成到构建流程

## Baseline Profile统计
- Profile文件大小: $(wc -l < "$ANDROID_DIR/app/src/main/baseline-prof.txt" 2>/dev/null || echo "未生成") 行
- 涵盖关键路径: 应用启动、首页滚动、分类切换、书籍详情、阅读页面

## 性能测试结果
请查看Macrobenchmark输出结果以获取详细的性能指标。

## 建议
1. 定期重新生成Baseline Profile以适应代码变更
2. 监控生产环境的启动性能指标
3. 继续优化高耗时的组件初始化

## 使用方法
```bash
# 重新生成Baseline Profile
./scripts/generate_baseline_profiles.sh

# 仅运行性能测试
./scripts/generate_baseline_profiles.sh --benchmark-only

# 生成Release版本
./scripts/generate_baseline_profiles.sh --release-only
```
EOF

    log_success "性能优化报告已生成: $REPORT_FILE"
}

# 主函数
main() {
    log_info "开始Baseline Profile生成流程..."
    
    # 解析命令行参数
    BENCHMARK_ONLY=false
    RELEASE_ONLY=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --benchmark-only)
                BENCHMARK_ONLY=true
                shift
                ;;
            --release-only)
                RELEASE_ONLY=true
                shift
                ;;
            *)
                log_error "未知参数: $1"
                exit 1
                ;;
        esac
    done
    
    # 检查环境
    check_android_tools
    check_device_connection
    
    if [ "$RELEASE_ONLY" = true ]; then
        build_release_with_profile
        return
    fi
    
    if [ "$BENCHMARK_ONLY" = true ]; then
        run_benchmarks
        run_performance_comparison
        return
    fi
    
    # 完整流程
    build_app
    install_app
    generate_profile
    verify_profile
    run_benchmarks
    run_performance_comparison
    build_release_with_profile
    generate_report
    
    log_success "🎉 Baseline Profile生成流程全部完成！"
    log_info "接下来你可以："
    log_info "1. 查看性能报告: baseline_profile_report.md"
    log_info "2. 查看Macrobenchmark结果"
    log_info "3. 部署包含Profile的Release版本"
}

# 执行主函数
main "$@" 