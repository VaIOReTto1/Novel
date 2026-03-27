import React, { useEffect } from 'react';
import { View, Text, TouchableOpacity, SafeAreaView, ScrollView, BackHandler } from 'react-native';
import { createPrivacyPolicyPageStyles } from './styles/PrivacyPolicyPageStyles';
import { useNovelColors } from '../../../utils/theme/colors';
import { NativeModules } from 'react-native';

const { NavigationBridge } = NativeModules;

/**
 * 隐私政策页面
 * 展示应用隐私条款与数据处理规则
 */
const PrivacyPolicyPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createPrivacyPolicyPageStyles(colors);

  const handleBackPress = () => {
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('PrivacyPolicyPageComponent');
    }
  };

  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      handleBackPress();
      return true;
    });

    return () => backHandler.remove();
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      {/* 顶部导航栏 */}
      <View style={styles.topBar}>
        <TouchableOpacity
          style={styles.backButton}
          onPress={handleBackPress}
          activeOpacity={0.7}
        >
          <Text style={styles.backArrow}>‹</Text>
        </TouchableOpacity>
        <View style={styles.titleContainer}>
          <Text style={styles.topBarTitle}>隐私政策</Text>
        </View>
        <View style={styles.rightPlaceholder} />
      </View>

      {/* 内容区域 */}
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.descriptionContainer}>
          <Text style={styles.descriptionText}>
            本隐私政策旨在帮助您了解我们如何收集、使用、存储和保护您的个人信息，以及您所享有的相关权利。阅读并同意本政策后，您可以放心使用 Novel App 的全部功能。
          </Text>
          {/* 一、信息收集 */}
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>一、信息收集</Text>
          <Text style={styles.descriptionText}>
            1.1 账户信息：当您注册账号或登录时，我们会收集您的手机号、头像、昵称等信息。
          </Text>
          <Text style={styles.descriptionText}>
            1.2 阅读数据：包括书籍阅读记录、书签、批注、阅读偏好等，用于跨设备同步与个性化推荐。
          </Text>
          <Text style={styles.descriptionText}>
            1.3 设备信息：为保障应用安全与功能正常运行，我们可能收集设备型号、操作系统、唯一设备标识符等信息。
          </Text>

          {/* 二、信息使用 */}
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>二、信息使用</Text>
          <Text style={styles.descriptionText}>
            我们收集信息的主要目的包括：
            (1) 提供核心阅读服务；(2) 同步阅读进度；(3) 保障账号与服务安全；(4) 改进产品体验及个性化推荐；(5) 向您发送重要通知。
          </Text>

          {/* 三、信息共享 */}
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>三、信息共享</Text>
          <Text style={styles.descriptionText}>
            我们仅在法律法规或获得您明确同意的情况下共享信息。常见场景包括：向云同步服务商（如 Firebase）托管阅读数据、向支付渠道验证交易等。我们与受托方签署严格的数据保护协议，确保其仅限于完成服务目的使用信息。
          </Text>

          {/* 四、信息存储与保护 */}
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>四、信息存储与保护</Text>
          <Text style={styles.descriptionText}>
            您的个人信息将被加密存储在境内服务器，并通过 SSL/TLS 进行传输。我们采用 AES-256、访问控制、最小权限等多重安全措施防止数据泄露、损毁或未经授权的访问。
          </Text>

          {/* 五、您的权利 */}
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>五、您的权利</Text>
          <Text style={styles.descriptionText}>
            您有权访问、更正、删除您的个人信息以及撤回授权。您可在"设置 &gt; 隐私设置"中执行上述操作，或通过联系我们的方式提出请求，我们将在 15 日内予以回复。
          </Text>

          {/* 六、联系我们 */}
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>六、联系我们</Text>
          <Text style={styles.descriptionText}>
            如对本隐私政策或您个人信息的处理方式有任何疑问、意见或投诉，您可通过 support@novelapp.dev 与我们取得联系。
          </Text>

          <Text style={[styles.descriptionText, styles.descriptionClosingText]}>本政策自 2024-01-01 起生效。如我们对本政策进行重大变更，我们将通过应用公告或其他方式通知您。</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default PrivacyPolicyPage;
