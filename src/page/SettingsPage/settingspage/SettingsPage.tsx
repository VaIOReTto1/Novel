import React, { useState, useEffect } from 'react';
// @ts-ignore
import { View, ScrollView, Text, TouchableOpacity, SafeAreaView, Modal } from 'react-native';
import { SettingRow } from './components';
import { useSettingsStore } from './store/settingsStore';
import { useUserStore } from '../../ProfilePage/store/userStore';
import { createSettingsPageStyles } from './styles/SettingsPageStyles';
import { SettingsSection } from './types/index';
import { useNovelColors } from '../../../utils/theme/colors';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  bootstrapSettingsPage,
  createSettingsSections,
} from './domain/settingsPageModel';

const SettingsPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createSettingsPageStyles(colors);
  const [showLogoutModal, setShowLogoutModal] = useState(false);

  const {
    isInitialized,
    cacheSize,
    pushNotificationEnabled,
    benefitNotificationEnabled,
    followSystemTheme,
    autoSwitchNightMode,
    useMobileDataWhenWiFiPoor,
    enableFloatingWindow,
    youthModeEnabled,
    clearCache,
    calculateCacheSize,
    setPushNotification,
    setBenefitNotification,
    setFollowSystemTheme,
    toggleColorScheme,
    setUseMobileDataWhenWiFiPoor,
    setEnableFloatingWindow,
    setYouthMode,
    navigateToAbout,
    navigateToCustomerService,
    navigateToPrivacyPolicy,
    navigateToFontSettings,
    getCurrentDisplayTheme,
    initializeSettings,
    logout,
  } = useSettingsStore();
  const { isLoggedIn, logout: userLogout } = useUserStore();

  useEffect(() => {
    console.log('[SettingsPage] mount');
    bootstrapSettingsPage({
      isInitialized,
      calculateCacheSize,
      initializeSettings,
    });

    return () => {
      console.log('[SettingsPage] 馃摫 SettingsPage缁勪欢鍗冲皢鍗歌浇');
    };
  }, [calculateCacheSize, initializeSettings, isInitialized]);

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handleBackPress();
      return true;
    });
  }, []);

  const handleBackPress = () => {
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('SettingsPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  };

  const handleLogout = () => {
    setShowLogoutModal(true);
  };

  const confirmLogout = async () => {
    try {
      console.log('[SettingsPage] begin logout');
      setShowLogoutModal(false);
      await logout();
      userLogout();
      console.log('[SettingsPage] logout success');
    } catch (error) {
      console.error('[SettingsPage] logout failed', error);
    }
  };

  const cancelLogout = () => {
    setShowLogoutModal(false);
  };

  const settingsSections: SettingsSection[] = React.useMemo(
    () =>
      createSettingsSections({
        cacheSize,
        pushNotificationEnabled,
        benefitNotificationEnabled,
        followSystemTheme,
        autoSwitchNightMode,
        useMobileDataWhenWiFiPoor,
        enableFloatingWindow,
        youthModeEnabled,
        displayTheme: getCurrentDisplayTheme(),
        isLoggedIn,
        handlers: {
          clearCache,
          setPushNotification,
          setBenefitNotification,
          setFollowSystemTheme: (value) => {
            console.log('[SettingsPage] 馃攧 鐢ㄦ埛鍒囨崲璺熼殢绯荤粺涓婚:', value);
            setFollowSystemTheme(value);
          },
          toggleColorScheme: () => {
            console.log('[SettingsPage] 馃寵 鐢ㄦ埛鐐瑰嚮涓婚妯″紡鍒囨崲鎸夐挳');
            toggleColorScheme();
          },
          navigateToTimedSwitch: () => {
            console.log('[SettingsPage] 鈴?鐢ㄦ埛鐐瑰嚮瀹氭椂鍒囨崲鏃ュ闂存ā寮忥紝瀵艰埅鍒癟imedSwitchPage');
            if (NavigationBridge?.navigateToTimedSwitch) {
              NavigationBridge.navigateToTimedSwitch();
            } else {
              console.log('NavigationBridge.navigateToTimedSwitch not available');
            }
          },
          setEnableFloatingWindow,
          setUseMobileDataWhenWiFiPoor,
          navigateToPrivacyPolicy,
          setYouthMode,
          navigateToCustomerService,
          navigateToAbout,
          navigateToFontSettings,
          showLogoutModal: handleLogout,
        },
      }),
    [
      autoSwitchNightMode,
      benefitNotificationEnabled,
      cacheSize,
      clearCache,
      enableFloatingWindow,
      followSystemTheme,
      getCurrentDisplayTheme,
      isLoggedIn,
      navigateToAbout,
      navigateToCustomerService,
      navigateToFontSettings,
      navigateToPrivacyPolicy,
      pushNotificationEnabled,
      setBenefitNotification,
      setEnableFloatingWindow,
      setFollowSystemTheme,
      setPushNotification,
      setUseMobileDataWhenWiFiPoor,
      setYouthMode,
      toggleColorScheme,
      useMobileDataWhenWiFiPoor,
      youthModeEnabled,
    ],
  );

  const renderTopBar = () => (
    <View style={styles.topBar}>
      <TouchableOpacity
        style={styles.backButton}
        onPress={handleBackPress}
        activeOpacity={0.7}
      >
        <Text style={styles.backArrow}>{'<'}</Text>
      </TouchableOpacity>

      <View style={styles.titleContainer}>
        <Text style={styles.topBarTitle}>设置</Text>
      </View>

      <View style={styles.rightPlaceholder} />
    </View>
  );

  const renderSection = (section: SettingsSection) => (
    <View key={section.id}>
      {section.title && (
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>{section.title}</Text>
        </View>
      )}
      {section.items.map((item) => (
        <SettingRow key={item.id} item={item} />
      ))}
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      {renderTopBar()}

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {settingsSections.map(renderSection)}
      </ScrollView>

      {showLogoutModal && (
        <Modal
          transparent={true}
          visible={showLogoutModal}
          animationType="fade"
          onRequestClose={cancelLogout}
        >
          <View style={styles.logoutModalOverlay}>
            <View style={styles.logoutModalContainer}>
              <Text style={styles.logoutModalTitle}>确认退出登录</Text>
              <Text style={styles.logoutModalDescription}>
                退出登录后，您需要重新登录才能继续使用相关功能。
              </Text>
              <View style={styles.logoutModalButtonContainer}>
                <TouchableOpacity
                  style={[styles.logoutModalButton, styles.logoutModalCancelButton]}
                  onPress={cancelLogout}
                >
                  <Text style={styles.logoutModalCancelButtonText}>取消</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.logoutModalButton, styles.logoutModalConfirmButton]}
                  onPress={confirmLogout}
                >
                  <Text style={styles.logoutModalConfirmButtonText}>确认退出</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </Modal>
      )}
    </SafeAreaView>
  );
};

export default SettingsPage;
