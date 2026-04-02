import React from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';
import { CommunityCircle } from '../types';

interface TabBarProps {
  circles: CommunityCircle[];
  selectedCircle: string;
  onCircleChange: (circleId: string) => void;
}

export const TabBar: React.FC<TabBarProps> = ({
  circles,
  selectedCircle,
  onCircleChange,
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <View style={styles.circleTabContainer}>
      <View style={styles.circleTabHeader}>
        <Text style={styles.circleTabTitle}>最常访问</Text>
      </View>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.circleScrollView}>
        <TouchableOpacity
          style={styles.allCircleItem}
          onPress={() => onCircleChange('all')}>
          <View
            style={[
              styles.allCircleIconContainer,
              selectedCircle === 'all' && styles.activeAllCircleIconContainer,
            ]}>
            <MaterialIcons
              name="menu-book"
              size={18}
              color={selectedCircle === 'all' ? colors.novelMain : colors.novelTextGray}
            />
          </View>
          <Text
            style={[
              styles.allCircleName,
              selectedCircle === 'all' && styles.activeAllCircleName,
            ]}>
            全部
          </Text>
        </TouchableOpacity>

        {circles.map((circle) => {
          const isActive = circle.id === selectedCircle;
          return (
            <TouchableOpacity
              key={circle.id}
              style={styles.circleItem}
              onPress={() => onCircleChange(circle.id)}>
              <View
                style={[
                  styles.circleIconContainer,
                  isActive && styles.activeCircleIconContainer,
                ]}>
                <MaterialIcons
                  name={circle.icon || 'forum'}
                  size={18}
                  color={isActive ? colors.novelMain : colors.novelTextGray}
                />
              </View>
              <Text
                style={[
                  styles.circleName,
                  isActive && styles.activeCircleName,
                ]}
                numberOfLines={2}>
                {circle.name}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
    </View>
  );
};
