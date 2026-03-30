import React, { useEffect } from 'react';
import { View, Text, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';
import { createHelpSupportPageStyles } from './styles/HelpSupportPageStyles';
import { useNovelColors } from '../../../utils/theme/colors';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';

/**
 * 帮助与支持页面
 * 向用户展示 FAQ、联系方式等信息
 */
const HelpSupportPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createHelpSupportPageStyles(colors);

  const handleBackPress = () => {
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('HelpSupportPageComponent');
    }
  };

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handleBackPress();
      return true;
    });
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
          <Text style={styles.topBarTitle}>帮助与支持</Text>
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
            欢迎使用 Novel App！如果您在使用过程中遇到任何问题，以下资源可为您提供帮助：
          </Text>
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>1. 常见问题 FAQ</Text>
          <Text style={styles.descriptionText}>
            在"设置 &gt; 帮助与支持"页面中查看常见问题列表，涵盖账户、支付、离线阅读等高频问题的解答。
          </Text>
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>2. 在线客服</Text>
          <Text style={styles.descriptionText}>
            工作日 09:00-18:00 您可以点击页面底部的"联系客服"按钮，进入在线客服对话，平均等待时长 &lt; 30 秒。
          </Text>
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>3. 官方邮箱</Text>
          <Text style={styles.descriptionText}>
            如需提交反馈、业务合作或举报侵权，请发送邮件至 support@novelapp.dev，我们将在 2 个工作日内给予回复。
          </Text>
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>4. 社区论坛</Text>
          <Text style={styles.descriptionText}>
            访问 forum.novelapp.dev 与数万名书友共同交流阅读心得，获取第一手产品动态与活动信息。
          </Text>
          <Text style={[styles.descriptionText, styles.descriptionSectionTitle]}>5. 社交媒体</Text>
          <Text style={styles.descriptionText}>
            关注新浪微博 @NovelApp、微信公众号"Novel Pro" 获取最新福利与更新预告。
          </Text>
          <Text style={[styles.descriptionText, styles.descriptionClosingText]}>如以上渠道仍无法解决您的问题，您可在应用内提交"问题工单"，我们会在 24 小时内跟进处理。</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default HelpSupportPage;
