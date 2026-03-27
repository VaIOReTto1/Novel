import React from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
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
      {/* 标题 */}
      <View style={styles.circleTabHeader}>
        <Text style={styles.circleTabTitle}>最常访问</Text>
      </View>

      {/* 圈子列表 */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.circleScrollView}
      >
        {/* 全部选项 */}
        <TouchableOpacity
          style={styles.allCircleItem}
          onPress={() => onCircleChange('all')}
        >
          <View style={[
            styles.allCircleIconContainer,
            selectedCircle === 'all' && styles.activeAllCircleIconContainer,
          ]}>
            <Text style={[
              styles.allCircleIcon,
              selectedCircle === 'all' && styles.activeAllCircleIcon,
            ]}>📚</Text>
          </View>
          <Text style={[
            styles.allCircleName,
            selectedCircle === 'all' && styles.activeAllCircleName,
          ]}>全部</Text>
        </TouchableOpacity>

        {/* 圈子选项 */}
        {circles.map((circle) => {
          const isActive = circle.id === selectedCircle;
          return (
            <TouchableOpacity
              key={circle.id}
              style={styles.circleItem}
              onPress={() => onCircleChange(circle.id)}
            >
              <View style={[
                styles.circleIconContainer,
                isActive && styles.activeCircleIconContainer,
              ]}>
                <Text style={styles.circleIcon}>{circle.icon}</Text>
              </View>
              <Text style={[
                styles.circleName,
                isActive && styles.activeCircleName,
              ]} numberOfLines={2}>
                {circle.name}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
    </View>
  );
};
