import React, { useState, useCallback } from 'react';
import { View, TouchableOpacity, Text, Modal, TouchableWithoutFeedback } from 'react-native';
import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
}

interface MenuOption {
  id: string;
  title: string;
  icon: string;
  onPress: () => void;
}

export const TopBar: React.FC<TopBarComponentProps> = ({ 
  styles, 
  title,
  onBackPress
}) => {
  const [showMenu, setShowMenu] = useState(false);

  const handleMenuPress = useCallback(() => {
    setShowMenu(!showMenu);
  }, [showMenu]);

  const handleMenuClose = useCallback(() => {
    setShowMenu(false);
  }, []);

  const menuOptions: MenuOption[] = [
    {
      id: 'share',
      title: '分享会员',
      icon: '📤',
      onPress: () => {
        handleMenuClose();
        console.log('分享会员');
        alert('分享会员功能开发中...');
      }
    },
    {
      id: 'contact',
      title: '联系客服',
      icon: '💬',
      onPress: () => {
        handleMenuClose();
        console.log('联系客服');
        alert('联系客服功能开发中...');
      }
    },
    {
      id: 'help',
      title: '帮助中心',
      icon: '❓',
      onPress: () => {
        handleMenuClose();
        console.log('帮助中心');
        alert('帮助中心功能开发中...');
      }
    }
  ];

  return (
    <>
      <View style={styles.topBar}>
        <TouchableOpacity 
          style={styles.backButton} 
          onPress={onBackPress}
          activeOpacity={0.7}
        >
          <Text style={styles.backArrow}>‹</Text>
        </TouchableOpacity>
        
        <View style={styles.titleContainer}>
          <Text style={styles.title}>{title}</Text>
        </View>
        
        {/* 右侧菜单按钮 */}
        <TouchableOpacity
          style={styles.menuButton}
          onPress={handleMenuPress}
          activeOpacity={0.7}
        >
          <Text style={styles.menuIcon}>⋯</Text>
        </TouchableOpacity>
      </View>

      {/* 下拉菜单模态 */}
      <Modal
        visible={showMenu}
        transparent
        animationType="fade"
        onRequestClose={handleMenuClose}
      >
        <TouchableWithoutFeedback onPress={handleMenuClose}>
          <View style={{ flex: 1 }}>
            <View style={styles.menuDropdown}>
              {menuOptions.map((option) => (
                <TouchableOpacity
                  key={option.id}
                  style={styles.menuItem}
                  onPress={option.onPress}
                  activeOpacity={0.7}
                >
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