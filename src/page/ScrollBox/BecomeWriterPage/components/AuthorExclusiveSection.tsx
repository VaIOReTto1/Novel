import React, { useCallback } from 'react';
import { View, Text, TouchableOpacity, Image, ScrollView } from 'react-native';
import { BenefitItem, TimelineItem, PlatformItem } from '../types';

interface AuthorExclusiveSectionProps {
  styles: any;
  selectedTab: 'benefits' | 'road' | 'platform';
  benefits: BenefitItem[];
  roadTimeline: TimelineItem[];
  platforms: PlatformItem[];
  onTabChange: (tab: 'benefits' | 'road' | 'platform') => void;
}

export const AuthorExclusiveSection: React.FC<AuthorExclusiveSectionProps> = React.memo(({
  styles,
  selectedTab,
  benefits,
  roadTimeline,
  platforms,
  onTabChange,
}) => {
  const handleTabPress = useCallback((tab: 'benefits' | 'road' | 'platform') => {
    onTabChange(tab);
  }, [onTabChange]);

  const renderContent = () => {
    switch (selectedTab) {
      case 'benefits':
        return (
          <View style={styles.benefitsList}>
            {benefits.map(item => (
              <View key={item.id} style={styles.benefitItem}>
                <View style={styles.benefitIconWrapper}>
                  <Text style={styles.benefitIcon}>{item.icon}</Text>
                </View>
                <View style={styles.benefitTextGroup}>
                  <Text style={styles.benefitTitle}>{item.title}</Text>
                  <Text style={styles.benefitSubtitle}>{item.description}</Text>
                </View>
              </View>
            ))}
            <TouchableOpacity style={styles.viewAllLink}>
              <Text style={styles.viewAllText}>查看更多&nbsp;›</Text>
            </TouchableOpacity>
          </View>
        );

      case 'road':
        return (
          <View style={styles.timelineContainer}>
            {roadTimeline.map((item, idx) => (
              <View key={item.id} style={styles.timelineRow}>
                {/* 左侧竖线和节点 */}
                <View style={styles.timelineIndicator}>
                  <View style={styles.timelineLine} />
                  <View style={styles.timelineIconWrapper}>
                    <Text style={styles.timelineIcon}>{item.icon}</Text>
                  </View>
                  {idx !== roadTimeline.length - 1 && <View style={styles.timelineLine} />}
                </View>
                {/* 右侧文字 */}
                <View style={styles.timelineTextGroup}>
                  <TouchableOpacity>
                    <Text style={styles.timelineTitle}>{item.title} &nbsp;›</Text>
                  </TouchableOpacity>
                  <Text style={styles.timelineSubtitle}>{item.subTitle}</Text>
                </View>
              </View>
            ))}
          </View>
        );

      case 'platform':
        return (
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.platformsContainer}
          >
            {platforms.map((platform) => (
              <View key={platform.id} style={styles.platformItem}>
                <Image
                  source={{ uri: platform.logo }}
                  style={styles.platformLogo}
                />
                <Text style={styles.platformName}>{platform.name}</Text>
              </View>
            ))}
          </ScrollView>
        );

      default:
        return null;
    }
  };

  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>作家专属</Text>
      <Text style={styles.sectionSubtitle}>入驻番茄，享亿级现金扶持内容</Text>

      {/* Subtabs */}
      <View style={styles.subtabsContainer}>
        <TouchableOpacity
          style={[styles.subtab, selectedTab === 'benefits' && styles.activeSubtab]}
          onPress={() => handleTabPress('benefits')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.subtabText,
            selectedTab === 'benefits' && styles.activeSubtabText,
          ]}>
            超多福利
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.subtab, selectedTab === 'road' && styles.activeSubtab]}
          onPress={() => handleTabPress('road')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.subtabText,
            selectedTab === 'road' && styles.activeSubtabText,
          ]}>
            成神之路
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.subtab, selectedTab === 'platform' && styles.activeSubtab]}
          onPress={() => handleTabPress('platform')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.subtabText,
            selectedTab === 'platform' && styles.activeSubtabText,
          ]}>
            平台实力
          </Text>
        </TouchableOpacity>
      </View>

      {/* Content */}
      {renderContent()}
    </View>
  );
});
