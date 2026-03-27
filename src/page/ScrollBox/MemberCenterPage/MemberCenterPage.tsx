import React, { useEffect, useCallback, useMemo } from 'react';
import { View, ScrollView, NativeModules, Text, BackHandler } from 'react-native';
import { useMemberCenterStore } from './store/memberCenterStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createMemberCenterPageStyles, getVIPThemeColors } from './styles/MemberCenterPageStyles';
import {
  TopBar,
  VIPCardCarousel,
  MemberBenefits,
  PricePackages,
  BenefitComparison,
  VIPRecommendation,
  BottomPurchase,
  TaskCards,
} from './components';

const { NavigationBridge } = NativeModules;

const MemberCenterPage: React.FC = () => {
  // 使用Zustand store
  const {
    loading,
    error,
    vipCards,
    currentCardIndex,
    currentBenefits,
    pricePackages,
    selectedPackageId,
    benefitComparison,
    vipRecommendations,
    taskCards,
    loadInitialData,
    setCurrentCard,
    selectPricePackage,
    handlePurchase,
    handlePrivacyPress,
    handleTermsPress,
    handleTaskPress,
  } = useMemberCenterStore();

  const colors = useNovelColors();

  // 获取当前卡片信息
  const currentCard = vipCards[currentCardIndex];
  const currentCardType = currentCard?.type || 'member';

  // 根据当前卡片类型创建动态样式
  const styles = useMemo(() => {
    return createMemberCenterPageStyles(colors, currentCardType);
  }, [colors, currentCardType]);

  // 初始化数据
  useEffect(() => {
    const initializeData = async () => {
      try {
        console.log('[MemberCenterPage] 开始初始化数据');
        await loadInitialData();
        console.log('[MemberCenterPage] 数据初始化完成');
      } catch (err) {
        console.error('[MemberCenterPage] 初始化失败:', err);
      }
    };

    initializeData();
  }, [loadInitialData]);

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('MemberCenter page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('MemberCenterPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // Android硬件返回按钮处理
  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      console.log('[MemberCenterPage] Android硬件返回按钮被按下');
      if (NavigationBridge?.navigateBack) {
        NavigationBridge.navigateBack('MemberCenterPageComponent');
      }
      return true; // 阻止默认行为
    });

    return () => backHandler.remove();
  }, []);

  // 卡片切换
  const handleCardChange = useCallback((index: number) => {
    console.log('Card changed to index:', index);
    setCurrentCard(index);
  }, [setCurrentCard]);

  // 套餐选择
  const handlePackageSelect = useCallback((packageId: string) => {
    console.log('Package selected:', packageId);
    selectPricePackage(packageId);
  }, [selectPricePackage]);

  // 购买按钮点击
  const handlePurchasePress = useCallback(() => {
    console.log('Purchase pressed');
    handlePurchase();
  }, [handlePurchase]);

  // 隐私政策点击
  const handlePrivacyLinkPress = useCallback(() => {
    console.log('Privacy link pressed');
    handlePrivacyPress();
  }, [handlePrivacyPress]);

  // 服务条款点击
  const handleTermsLinkPress = useCallback(() => {
    console.log('Terms link pressed');
    handleTermsPress();
  }, [handleTermsPress]);

  // 任务卡片点击
  const handleTaskCardPress = useCallback((taskId: string) => {
    console.log('Task pressed:', taskId);
    handleTaskPress(taskId);
  }, [handleTaskPress]);

  const themeColors = getVIPThemeColors(currentCardType);  // member / svip / adfree

  // 获取当前选中的套餐
  const selectedPackage = pricePackages.find(pkg => pkg.id === selectedPackageId) || null;

  // 加载状态
  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="会员中心"
          onBackPress={handleBackPress}
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  // 错误状态
  if (error) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="会员中心"
          onBackPress={handleBackPress}
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载失败: {error}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* 固定顶部导航 */}
      <TopBar
        styles={styles}
        title={currentCard?.title || '会员中心'}
        onBackPress={handleBackPress}
      />

      {/* 滚动内容区域 */}
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        {/* VIP卡片轮播 */}
        <VIPCardCarousel
          styles={styles}
          cards={vipCards}
          currentIndex={currentCardIndex}
          onCardChange={handleCardChange}
        />

        {/* 当前VIP权益展示 */}
        <MemberBenefits
          styles={styles}
          benefits={currentBenefits}
          cardType={currentCardType}
        />

        {/* 价格套餐选择 */}
        <PricePackages
          gradientColors={themeColors.gradient}
          styles={styles}
          packages={pricePackages}
          onSelectPackage={handlePackageSelect}
        />

        {/* 权益对比表格 */}
        <BenefitComparison
          styles={styles}
          comparisons={benefitComparison}
          currentCardType={currentCardType}
        />

        {/* 任务卡片（仅特定VIP类型显示） */}
        <TaskCards
          styles={styles}
          taskCards={taskCards}
          onTaskPress={handleTaskCardPress}
        />

        {/* VIP专属推荐（免广告VIP不显示） */}
        <VIPRecommendation
          styles={styles}
          recommendations={vipRecommendations}
          cardType={currentCardType}
        />
      </ScrollView>

      {/* 固定底部购买区域 */}
      <BottomPurchase
        styles={styles}
        selectedPackage={selectedPackage}
        onPurchase={handlePurchasePress}
        onPrivacyPress={handlePrivacyLinkPress}
        onTermsPress={handleTermsLinkPress}
      />
    </View>
  );
};

export default MemberCenterPage;
