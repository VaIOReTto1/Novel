#!/usr/bin/env node

/**
 * 版本同步脚本
 * 用于同步 Android 和 React Native 的版本号
 */

const fs = require('fs');
const path = require('path');

// 文件路径
const packageJsonPath = path.join(__dirname, '..', 'package.json');
const versionPropsPath = path.join(__dirname, '..', 'android', 'version.properties');
const appJsonPath = path.join(__dirname, '..', 'app.json');

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
 * 写入版本属性文件
 */
function writeVersionProps(props) {
    const content = `# 版本配置文件
# 此文件用于管理应用的版本号

# 版本代码 (用于Google Play Store的内部版本号)
VERSION_CODE=${props.VERSION_CODE}

# 语义化版本号
VERSION_MAJOR=${props.VERSION_MAJOR}
VERSION_MINOR=${props.VERSION_MINOR}
VERSION_PATCH=${props.VERSION_PATCH}

# 构建类型标识 (可选)
VERSION_SUFFIX=${props.VERSION_SUFFIX || ''}
`;
    
    fs.writeFileSync(versionPropsPath, content, 'utf8');
}

/**
 * 同步版本号到 package.json
 */
function syncToPackageJson(versionName) {
    const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
    packageJson.version = versionName;
    fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2) + '\n', 'utf8');
    console.log(`✅ package.json 版本已更新为: ${versionName}`);
}

/**
 * 同步版本号到 app.json
 */
function syncToAppJson(versionName) {
    const appJson = JSON.parse(fs.readFileSync(appJsonPath, 'utf8'));
    appJson.version = versionName;
    fs.writeFileSync(appJsonPath, JSON.stringify(appJson, null, 2) + '\n', 'utf8');
    console.log(`✅ app.json 版本已更新为: ${versionName}`);
}

/**
 * 递增版本号
 */
function incrementVersion(type = 'patch') {
    const props = readVersionProps();
    
    let major = parseInt(props.VERSION_MAJOR) || 1;
    let minor = parseInt(props.VERSION_MINOR) || 0;
    let patch = parseInt(props.VERSION_PATCH) || 0;
    let code = parseInt(props.VERSION_CODE) || 1;
    
    switch (type) {
        case 'major':
            major += 1;
            minor = 0;
            patch = 0;
            break;
        case 'minor':
            minor += 1;
            patch = 0;
            break;
        case 'patch':
        default:
            patch += 1;
            break;
    }
    
    code += 1;
    
    const newProps = {
        VERSION_CODE: code.toString(),
        VERSION_MAJOR: major.toString(),
        VERSION_MINOR: minor.toString(),
        VERSION_PATCH: patch.toString(),
        VERSION_SUFFIX: props.VERSION_SUFFIX || ''
    };
    
    const versionName = `${major}.${minor}.${patch}`;
    
    // 写入版本文件
    writeVersionProps(newProps);
    
    // 同步到其他文件
    syncToPackageJson(versionName);
    syncToAppJson(versionName);
    
    console.log(`\n🎉 版本递增完成!`);
    console.log(`📱 版本号: ${versionName}`);
    console.log(`🔢 版本代码: ${code}`);
    
    return { versionName, versionCode: code };
}

/**
 * 显示当前版本信息
 */
function showCurrentVersion() {
    const props = readVersionProps();
    const versionName = `${props.VERSION_MAJOR}.${props.VERSION_MINOR}.${props.VERSION_PATCH}`;
    
    console.log(`\n📱 当前版本信息:`);
    console.log(`版本号: ${versionName}`);
    console.log(`版本代码: ${props.VERSION_CODE}`);
    console.log(`构建后缀: ${props.VERSION_SUFFIX || '无'}`);
}

// 命令行参数处理
const args = process.argv.slice(2);
const command = args[0];

switch (command) {
    case 'patch':
    case 'minor':
    case 'major':
        incrementVersion(command);
        break;
    case 'show':
    case 'current':
        showCurrentVersion();
        break;
    case 'sync':
        const props = readVersionProps();
        const versionName = `${props.VERSION_MAJOR}.${props.VERSION_MINOR}.${props.VERSION_PATCH}`;
        syncToPackageJson(versionName);
        syncToAppJson(versionName);
        break;
    default:
        console.log(`\n📋 版本管理脚本使用说明:`);
        console.log(`node scripts/version-sync.js <command>`);
        console.log(`\n可用命令:`);
        console.log(`  patch   - 递增补丁版本号 (1.0.0 -> 1.0.1)`);
        console.log(`  minor   - 递增次版本号 (1.0.1 -> 1.1.0)`);
        console.log(`  major   - 递增主版本号 (1.1.0 -> 2.0.0)`);
        console.log(`  show    - 显示当前版本信息`);
        console.log(`  sync    - 同步版本号到所有配置文件`);
        console.log(`\n示例:`);
        console.log(`  npm run version:patch`);
        console.log(`  npm run version:minor`);
        console.log(`  npm run version:show`);
        break;
}