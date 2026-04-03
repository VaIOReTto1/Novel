import React, { useState, useCallback } from 'react';
import {
  Alert,
  Modal,
  Text,
  TouchableOpacity,
  TouchableWithoutFeedback,
  View,
} from 'react-native';

import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
}

interface MenuOption {
  id: string;
  title: string;
  onPress: () => void;
}

export const TopBar: React.FC<TopBarComponentProps> = ({
  styles,
  title,
  onBackPress,
}) => {
  const [showMenu, setShowMenu] = useState(false);

  const handleMenuPress = useCallback(() => {
    setShowMenu((current) => !current);
  }, []);

  const handleMenuClose = useCallback(() => {
    setShowMenu(false);
  }, []);

  const menuOptions: MenuOption[] = [
    {
      id: 'share',
      title: '分享会员',
      onPress: () => {
        handleMenuClose();
        Alert.alert('分享会员功能开发中...');
      },
    },
    {
      id: 'contact',
      title: '联系客服',
      onPress: () => {
        handleMenuClose();
        Alert.alert('联系客服功能开发中...');
      },
    },
    {
      id: 'help',
      title: '帮助中心',
      onPress: () => {
        handleMenuClose();
        Alert.alert('帮助中心功能开发中...');
      },
    },
  ];

  return (
    <>
      <View style={styles.topBar}>
        <TouchableOpacity
          style={styles.backButton}
          onPress={onBackPress}
          activeOpacity={0.7}>
          <Text style={styles.backArrow}>{'<'}</Text>
        </TouchableOpacity>

        <View style={styles.titleContainer}>
          <Text style={styles.title}>{title}</Text>
        </View>

        <TouchableOpacity
          style={styles.menuButton}
          onPress={handleMenuPress}
          activeOpacity={0.7}>
          <Text style={styles.menuIcon}>更多</Text>
        </TouchableOpacity>
      </View>

      <Modal
        visible={showMenu}
        transparent
        animationType="fade"
        onRequestClose={handleMenuClose}>
        <TouchableWithoutFeedback onPress={handleMenuClose}>
          <View style={styles.menuOverlay}>
            <View style={styles.menuDropdown}>
              {menuOptions.map((option) => (
                <TouchableOpacity
                  key={option.id}
                  style={styles.menuItem}
                  onPress={option.onPress}
                  activeOpacity={0.7}>
                  <Text style={styles.menuItemText}>{option.title}</Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        </TouchableWithoutFeedback>
      </Modal>
    </>
  );
};
