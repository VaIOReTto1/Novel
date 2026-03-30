import React, { useEffect, useCallback } from 'react';
import { View, ScrollView } from 'react-native';
import { useUserStore } from './store/userStore';
import { useHomeStore } from './store/BookStore';
import { useNovelColors } from '../../utils/theme/colors';
import { useProfilePageAnimations } from './hooks/useProfilePageAnimations';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { convertHomeBooksToBooks } from './utils/helpers';
import { createHomePageStyles } from './styles/ProfilePageStyles';
import { Book } from './types';
import {
  bootstrapProfilePageData,
  bootstrapProfileUserState,
} from './domain/profileBootstrap';
import { useThemeStore } from '../../utils/theme/themeStore';
import NavBridge from '../../utils/bridge/NavigationBridge';
import { getCurrentUserData, getUserBalance, getUserCoins } from '../../utils/bridge/UserBridge';
import {
  TopBar,
  LoginBar,
  RefreshIndicator,
  ScrollableArea,
  BottomBox,
  WaterfallGrid,
} from './components';

const ProfilePage: React.FC = () => {
  const {
    nickname,
    photo,
    isLoggedIn,
    balance,
    coins,
    isAuthor,
    handleNativeUserData,
    setBalance,
    setCoins,
    initializeFromCache,
    setAuthorStatus,
  } = useUserStore();
  const {
    homeRecommendBooks,
    homeRecommendLoading,
    isRefreshing,
    hasMoreHomeRecommend,
    loadHomeRecommendBooks,
    refreshBooks,
    loadMoreBooks,
  } = useHomeStore();
  const { initializeFromNative, isInitialized } = useThemeStore();

  const colors = useNovelColors();
  const styles = createHomePageStyles(colors);

  const convertedBooks = React.useMemo(
    () => convertHomeBooksToBooks(homeRecommendBooks),
    [homeRecommendBooks],
  );

  const refreshLogic = useRefreshLogic({
    isRefreshing,
    homeRecommendLoading,
    hasMoreHomeRecommend,
    refreshBooks,
    loadMoreBooks,
  });

  const animations = useProfilePageAnimations(
    isRefreshing,
    refreshLogic.isPullingDown,
    refreshLogic.pullDistance,
    refreshLogic.PULL_THRESHOLD,
  );

  useEffect(() => {
    bootstrapProfilePageData({
      isThemeInitialized: isInitialized,
      initializeTheme: initializeFromNative,
      loadHomeRecommendBooks,
    });
  }, [initializeFromNative, isInitialized, loadHomeRecommendBooks]);

  useEffect(() => {
    bootstrapProfileUserState({
      initializeFromCache,
      getCurrentUserData,
      getUserBalance,
      getUserCoins,
      getAuthorStatus: NavBridge.getAuthorStatus,
      handleNativeUserData,
      setBalance,
      setCoins,
      setAuthorStatus,
    });
  }, [
    handleNativeUserData,
    initializeFromCache,
    setAuthorStatus,
    setBalance,
    setCoins,
  ]);

  const toLogin = useCallback(() => {
    NavBridge.goToLogin();
  }, []);

  const handleBookPress = useCallback((book: Book) => {
    console.log('Book pressed:', book.title);
  }, []);

  const handleSettingsPress = useCallback(() => {
    NavBridge.navigateToSettings();
  }, []);

  const handleBeWriterPress = useCallback(() => {
    NavBridge.navigateToBecomeWriterWithFlag(!!isAuthor);
  }, [isAuthor]);

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        onScroll={refreshLogic.handleScroll}
        scrollEventThrottle={16}
        bounces={true}
        alwaysBounceVertical={true}
        scrollEnabled={true}
      >
        <RefreshIndicator
          styles={styles}
          isPullingDown={refreshLogic.isPullingDown}
          isRefreshing={isRefreshing}
          pullDistance={refreshLogic.pullDistance}
          threshold={refreshLogic.PULL_THRESHOLD}
          spinStyle={animations.spinStyle}
        />

        <TopBar styles={styles} onSettingsPress={handleSettingsPress} />

        <LoginBar
          styles={styles}
          photo={photo || undefined}
          isLoggedIn={isLoggedIn}
          nickname={nickname || undefined}
          onLogin={toLogin}
        />

        <ScrollableArea
          styles={styles}
          scrollX={animations.scrollX}
          animatedContainerStyle={animations.animatedContainerStyle}
          firstPageIconsStyle={animations.firstPageIconsStyle}
          secondPageIconsStyle={animations.secondPageIconsStyle}
          thirdPageIconsStyle={animations.thirdPageIconsStyle}
          firstPageAdStyle={animations.firstPageAdStyle}
          colors={colors}
          onBeWriterPress={handleBeWriterPress}
          isAuthor={!!isAuthor}
        />

        <BottomBox styles={styles} coins={coins} balance={balance} />

        <WaterfallGrid
          styles={styles}
          books={convertedBooks}
          loading={homeRecommendLoading}
          hasMore={hasMoreHomeRecommend}
          onBookPress={handleBookPress}
        />
      </ScrollView>
    </View>
  );
};

export default ProfilePage;
