import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import IconComponent from '../../../../component/IconComponent';
import { commonSizes } from '../../../../utils/theme/dimensions';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createSettingsPageStyles } from '../styles/SettingsPageStyles';
import { SettingItem } from '../types/index';
import MoonSunSwitch from './ThemeSwitcher';

interface SettingRowProps {
  item: SettingItem;
  onPress?: () => void;
}

export const SettingRow: React.FC<SettingRowProps> = ({ item, onPress }) => {
  const colors = useNovelColors();
  const styles = createSettingsPageStyles(colors);

  const handlePress = () => {
    if (item.onPress) {
      item.onPress();
    } else if (onPress) {
      onPress();
    }
  };

  const renderRightContent = () => {
    switch (item.type) {
      case 'switch':
        return (
          <TouchableOpacity
            onPress={() => item.onValueChange?.(!item.value)}
            disabled={item.disabled}
            style={[
              styles.customSwitch,
              item.value && styles.customSwitchActive,
            ]}>
            <View
              style={[
                styles.customSwitchThumb,
                item.value
                  ? styles.customSwitchThumbActive
                  : styles.customSwitchThumbInactive,
              ]}
            />
          </TouchableOpacity>
        );

      case 'toggle':
        return (
          <MoonSunSwitch
            isDark={item.value === 'dark'}
            onToggle={handlePress}
          />
        );

      case 'action':
      case 'arrow':
      default:
        return (
          <View style={styles.settingRight}>
            {item.value ? <Text style={styles.settingValue}>{item.value}</Text> : null}
            <Text style={styles.arrow}>&gt;</Text>
          </View>
        );
    }
  };

  const renderLeftContent = () => (
    <View style={styles.settingLeft}>
      {item.icon ? (
        <View style={styles.settingIcon}>
          <IconComponent
            name={item.icon}
            width={commonSizes.iconSize}
            height={commonSizes.iconSize}
          />
        </View>
      ) : null}
      <Text
        style={[
          styles.settingTitle,
          item.disabled && styles.disabledTitle,
        ]}>
        {item.title}
      </Text>
    </View>
  );

  const isInteractive =
    item.type === 'arrow' || item.type === 'action' || item.type === 'toggle';

  if (isInteractive && !item.disabled) {
    return (
      <TouchableOpacity
        style={[styles.settingRow, item.disabled && styles.disabledRow]}
        onPress={handlePress}
        activeOpacity={0.7}>
        {renderLeftContent()}
        {renderRightContent()}
      </TouchableOpacity>
    );
  }

  return (
    <View style={[styles.settingRow, item.disabled && styles.disabledRow]}>
      {renderLeftContent()}
      {renderRightContent()}
    </View>
  );
};
