import React, { useState, useEffect } from 'react';
import { View, Text, SafeAreaView, DeviceEventEmitter, NativeModules, TouchableOpacity } from 'react-native';
import { SettingRow } from '../settingspage/components/SettingRow';
import { TimePickerModal } from './components/TimePickerModal';
import { useSettingsStore } from '../settingspage/store/settingsStore';
import { createTimedSwitchPageStyles } from './styles/TimedSwitchPageStyles';
import { useNovelColors } from '../../../utils/theme/colors';
import { SettingItem } from '../settingspage/types';

const { NavigationBridge } = NativeModules;

/**
 * 定时切换页面
 * 专门用于配置定时切换日夜间模式的详细设置
 */
const TimedSwitchPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createTimedSwitchPageStyles(colors);
  const {
    autoSwitchNightMode,
    nightModeStartTime,
    nightModeEndTime,
    setAutoSwitchNightMode,
    setNightModeTime,
  } = useSettingsStore();

  // 时间选择器状态
  const [showTimePickerModal, setShowTimePickerModal] = useState(false);
  const [editingTimeType, setEditingTimeType] = useState<'day' | 'night'>('day'); // 当前正在编辑的时间类型

  // 监听主题变化
  useEffect(() => {
    console.log('[TimedSwitchPage] 📱 TimedSwitchPage组件开始挂载');
    
    const subscription = DeviceEventEmitter.addListener('ThemeChanged', (data: { colorScheme: string }) => {
      console.log('[TimedSwitchPage] 🎨 收到主题变化事件:', data.colorScheme);
    });

    return () => {
      console.log('[TimedSwitchPage] 📱 TimedSwitchPage组件即将卸载');
      subscription.remove();
    };
  }, []);

  // 定时切换开关切换处理
  const handleTimeBasedThemeToggle = (value: string | boolean) => {
    const enabled = typeof value === 'boolean' ? value : value === 'true';
    console.log('[TimedSwitchPage] ⏰ 用户切换定时主题开关:', enabled);
    setAutoSwitchNightMode(enabled);
  };

  // 处理日间模式时间选择（实际是夜间模式结束时间）
  const handleDayModeTimePress = () => {
    console.log('[TimedSwitchPage] ☀️ 用户点击设置日间模式时间');
    setEditingTimeType('day');
    setShowTimePickerModal(true);
  };

  // 处理夜间模式时间选择（夜间模式开始时间）
  const handleNightModeTimePress = () => {
    console.log('[TimedSwitchPage] 🌙 用户点击设置夜间模式时间');
    setEditingTimeType('night');
    setShowTimePickerModal(true);
  };

  // 时间选择确认处理
  const handleTimeConfirm = (selectedTime: string) => {
    console.log(`[TimedSwitchPage] ⏰ 用户确认设置${editingTimeType === 'day' ? '日间' : '夜间'}模式时间:`, selectedTime);
    if (editingTimeType === 'day') {
      // 日间模式时间实际是夜间模式的结束时间
      setNightModeTime(nightModeStartTime, selectedTime);
    } else {
      // 夜间模式开始时间
      setNightModeTime(selectedTime, nightModeEndTime);
    }
  };

  // 获取当前编辑的时间
  const getCurrentEditingTime = () => {
    return editingTimeType === 'day' ? nightModeEndTime : nightModeStartTime;
  };

  // 获取时间选择器标题
  const getTimePickerTitle = () => {
    return editingTimeType === 'day' ? '设置日间模式时间' : '设置夜间模式时间';
  };

  // 返回按钮处理
  const handleBackPress = () => {
    console.log('[TimedSwitchPage] ⬅️ 用户点击返回按钮');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('TimedSwitchPageComponent');
    }
  };

  const settingItems: SettingItem[] = [
    {
      id: 'timed_switch_toggle',
      title: '定时切换日夜间',
      type: 'switch',
      value: autoSwitchNightMode,
      onValueChange: handleTimeBasedThemeToggle,
    },
    {
      id: 'day_mode_time',
      title: '日间模式',
      type: 'action',
      value: nightModeEndTime, // 日间模式是夜间模式的结束时间
      onPress: handleDayModeTimePress,
      disabled: !autoSwitchNightMode,
    },
    {
      id: 'night_mode_time',
      title: '夜间模式',
      type: 'action',
      value: nightModeStartTime, // 夜间模式的开始时间
      onPress: handleNightModeTimePress,
      disabled: !autoSwitchNightMode,
    },
  ];

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.container}>
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
            <Text style={styles.topBarTitle}>定时切换</Text>
          </View>
          <View style={styles.rightPlaceholder} />
        </View>

        {/* 提示信息 */}
        <View style={styles.descriptionContainer}>
          <Text style={styles.descriptionText}>
            开启定时切换日夜间后，将会根据小时进行自动切换对应主题模式
          </Text>
        </View>

        {/* 设置内容 */}
        <View style={styles.settingsContainer}>
          {settingItems.map((item) => (
            <SettingRow
              key={item.id}
              item={item}
            />
          ))}
        </View>

        {/* 时间选择器模态框 */}
        <TimePickerModal
          visible={showTimePickerModal}
          onClose={() => setShowTimePickerModal(false)}
          onConfirm={handleTimeConfirm}
          initialTime={getCurrentEditingTime()}
          title={getTimePickerTitle()}
        />
      </View>
    </SafeAreaView>
  );
};

export default TimedSwitchPage;
