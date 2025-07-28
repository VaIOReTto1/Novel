#!/usr/bin/env node

/**
 * CI/CD 版本管理脚本
 * 用于自动化构建流程中的版本管理
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// 文件路径
const packageJsonPath = path.join(__dirname, '..', 'package.json');
const versionPropsPath = path.join(__dirname, '..', 'android', 'version.properties');

/**
 * 读取版本属性文件
 */
function readVersionProps() {
    const content = fs.readFileSync(versionPropsPath, 'utf8');
    const props = {};

    content.split('\n').forEach(line => {
        const trimmed = line.trim();
        if (trimmed && !trimmed.startsWith('#')) {
            const [key, value] = trimmed.split('=');
            if (key && value) {
                props[key.trim()] = value.trim();
            }
        }
    });

    return props;
}

/**
 * 获取Git信息
 */
function getGitInfo() {
    try {
        const branch = execSync('git rev-parse --abbrev-ref HEAD', { encoding: 'utf8' }).trim();
        const commit = execSync('git rev-parse --short HEAD', { encoding: 'utf8' }).trim();
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5);

        return { branch, commit, timestamp };
    } catch (error) {
        console.warn('⚠️  无法获取Git信息，使用默认值');
        return {
            branch: 'unknown',
            commit: 'unknown',
            timestamp: new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5),
        };
    }
}

/**
 * 生成构建版本号
 */
function generateBuildVersion(buildType = 'debug') {
    const props = readVersionProps();
    const gitInfo = getGitInfo();

    const major = props.VERSION_MAJOR || '1';
    const minor = props.VERSION_MINOR || '0';
    const patch = props.VERSION_PATCH || '0';
    const code = props.VERSION_CODE || '1';

    let versionName = `${major}.${minor}.${patch}`;
    let versionSuffix = '';

    // 根据构建类型添加后缀
    switch (buildType.toLowerCase()) {
        case 'debug':
            versionSuffix = `-debug.${gitInfo.commit}`;
            break;
        case 'beta':
        case 'staging':
            versionSuffix = `-beta.${gitInfo.timestamp}`;
            break;
        case 'release':
        case 'production':
            // 生产版本不添加后缀
            break;
        default:
            versionSuffix = `-${buildType}.${gitInfo.commit}`;
            break;
    }

    const fullVersionName = versionName + versionSuffix;

    return {
        versionName: fullVersionName,
        versionCode: code,
        baseVersion: versionName,
        buildType,
        gitInfo,
    };
}

/**
 * 更新构建配置
 */
function updateBuildConfig(buildInfo) {
    // 更新 package.json
    const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
    packageJson.version = buildInfo.versionName;
    fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2) + '\n', 'utf8');

    // 输出构建信息
    console.log('\n🏗️  构建版本信息:');
    console.log(`📱 版本名称: ${buildInfo.versionName}`);
    console.log(`🔢 版本代码: ${buildInfo.versionCode}`);
    console.log(`🏷️  构建类型: ${buildInfo.buildType}`);
    console.log(`🌿 Git分支: ${buildInfo.gitInfo.branch}`);
    console.log(`📝 Git提交: ${buildInfo.gitInfo.commit}`);
    console.log(`⏰ 构建时间: ${buildInfo.gitInfo.timestamp}`);

    // 输出环境变量（供CI/CD使用）
    console.log('\n🔧 环境变量:');
    console.log(`export VERSION_NAME="${buildInfo.versionName}"`);
    console.log(`export VERSION_CODE="${buildInfo.versionCode}"`);
    console.log(`export BUILD_TYPE="${buildInfo.buildType}"`);
    console.log(`export GIT_BRANCH="${buildInfo.gitInfo.branch}"`);
    console.log(`export GIT_COMMIT="${buildInfo.gitInfo.commit}"`);

    return buildInfo;
}

/**
 * 生成发布说明
 */
function generateReleaseNotes(buildInfo) {
    const releaseNotes = {
        version: buildInfo.versionName,
        versionCode: buildInfo.versionCode,
        buildType: buildInfo.buildType,
        buildTime: new Date().toISOString(),
        git: buildInfo.gitInfo,
        features: [],
        bugfixes: [],
        breaking: [],
    };

    const releaseNotesPath = path.join(__dirname, '..', 'build', 'release-notes.json');

    // 确保build目录存在
    const buildDir = path.dirname(releaseNotesPath);
    if (!fs.existsSync(buildDir)) {
        fs.mkdirSync(buildDir, { recursive: true });
    }

    fs.writeFileSync(releaseNotesPath, JSON.stringify(releaseNotes, null, 2), 'utf8');
    console.log(`\n📋 发布说明已生成: ${releaseNotesPath}`);

    return releaseNotes;
}

// 命令行参数处理
const args = process.argv.slice(2);
const buildType = args[0] || 'debug';

const buildInfo = generateBuildVersion(buildType);
updateBuildConfig(buildInfo);
generateReleaseNotes(buildInfo);

console.log('\n✅ CI版本管理完成!');
