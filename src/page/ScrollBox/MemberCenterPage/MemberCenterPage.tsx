import React, { useEffect, useMemo } from 'react';
import { View, ScrollView, Text } from 'react-native';
import { useMemberCenterStore } from './store/memberCenterStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createMemberCenterPageStyles, getVIPThemeColors } from './styles/MemberCenterPageStyles';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
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
import {
  bootstrapMemberCenterPage,
  createMemberCenterPageHandlers,
} from './domain/memberCenterPageModel';

const MemberCenterPage: React.FC = () => {
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
  const currentCard = vipCards[currentCardIndex];
  const currentCardType = currentCard?.type || 'member';
  const styles = useMemo(
    () => createMemberCenterPageStyles(colors, currentCardType),
    [colors, currentCardType],
  );

  useEffect(() => {
    bootstrapMemberCenterPage({
      loadInitialData,
    });
  }, [loadInitialData]);

  const handlers = useMemo(
    () =>
      createMemberCenterPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('MemberCenterPageComponent'),
        setCurrentCard,
        selectPricePackage,
        handlePurchase,
        handlePrivacyPress,
        handleTermsPress,
        handleTaskPress,
      }),
    [
      handlePrivacyPress,
      handlePurchase,
      handleTaskPress,
      handleTermsPress,
      selectPricePackage,
      setCurrentCard,
    ],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBackPress();
      return true;
    });
  }, [handlers]);

  const themeColors = getVIPThemeColors(currentCardType);
  const selectedPackage = pricePackages.find(pkg => pkg.id === selectedPackageId) || null;

  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="会员中心"
          onBackPress={handlers.handleBackPress}
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="会员中心"
          onBackPress={handlers.handleBackPress}
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载失败: {error}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        title={currentCard?.title || '会员中心'}
        onBackPress={handlers.handleBackPress}
      />

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        <VIPCardCarousel
          styles={styles}
          cards={vipCards}
          currentIndex={currentCardIndex}
          onCardChange={handlers.handleCardChange}
        />

        <MemberBenefits
          styles={styles}
          benefits={currentBenefits}
          cardType={currentCardType}
        />

        <PricePackages
          gradientColors={themeColors.gradient}
          styles={styles}
          packages={pricePackages}
          onSelectPackage={handlers.handlePackageSelect}
        />

        <BenefitComparison
          styles={styles}
          comparisons={benefitComparison}
          currentCardType={currentCardType}
        />

        <TaskCards
          styles={styles}
          taskCards={taskCards}
          onTaskPress={handlers.handleTaskCardPress}
        />

        <VIPRecommendation
          styles={styles}
          recommendations={vipRecommendations}
          cardType={currentCardType}
        />
      </ScrollView>

      <BottomPurchase
        styles={styles}
        selectedPackage={selectedPackage}
        onPurchase={handlers.handlePurchasePress}
        onPrivacyPress={handlers.handlePrivacyLinkPress}
        onTermsPress={handlers.handleTermsLinkPress}
      />
    </View>
  );
};

export default MemberCenterPage;
