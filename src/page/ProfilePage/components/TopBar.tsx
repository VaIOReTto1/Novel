import React from 'react';
import { TouchableOpacity, View } from 'react-native';

import IconComponent from '../../../component/IconComponent';
import { commonSizes } from '../../../utils/theme/dimensions';
import { useThemeStore } from '../../../utils/theme/themeStore';

interface TopBarProps {
  styles: any;
  onSettingsPress?: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({ styles, onSettingsPress }) => {
  const { setTheme, currentTheme } = useThemeStore();
  const themeIconName = currentTheme === 'dark' ? 'sun_mode' : 'moon_mode';

  const handleThemeToggle = () => {
    const nextTheme = currentTheme === 'light' ? 'dark' : 'light';
    setTheme(nextTheme);
    console.log('主题切换到:', nextTheme);
  };

  return (
    <View style={styles.topBar}>
      <TouchableOpacity style={styles.topBarButton} onPress={() => console.log('QR Code')}>
        <IconComponent
          name="qrscan"
          width={commonSizes.iconSize}
          height={commonSizes.iconSize}
        />
      </TouchableOpacity>
      <TouchableOpacity style={styles.topBarButton} onPress={handleThemeToggle}>
        <IconComponent
          name={themeIconName}
          width={commonSizes.iconSize}
          height={commonSizes.iconSize}
        />
      </TouchableOpacity>
      <TouchableOpacity
        style={styles.topBarButton}
        onPress={onSettingsPress || (() => console.log('Settings'))}
      >
        <IconComponent
          name="settings"
          width={commonSizes.iconSize}
          height={commonSizes.iconSize}
        />
      </TouchableOpacity>
    </View>
  );
};
