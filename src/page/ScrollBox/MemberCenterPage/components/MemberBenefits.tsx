import React from 'react';
import { View, Text } from 'react-native';
import { VIPBenefit } from '../types';

interface MemberBenefitsProps {
  styles: any;
  benefits: VIPBenefit[];
  cardType: string;
}

export const MemberBenefits: React.FC<MemberBenefitsProps> = React.memo(({
  styles,
  benefits,
  cardType,
}) => {
  const getTitle = () => {
    switch (cardType) {
      case 'member':
        return '会员VIP权益';
      case 'svip':
        return 'SVIP权益';
      case 'adfree':
        return '免广告VIP权益';
      default:
        return 'VIP权益';
    }
  };

  return (
    <View style={styles.benefitsContainer}>
      <Text style={styles.benefitsTitle}>{getTitle()}</Text>
      
      <View style={styles.benefitsList}>
        {benefits.map((benefit) => (
          <View key={benefit.id} style={styles.benefitItem}>
            <View style={styles.benefitIcon}>
              <Text style={styles.benefitIconText}>{benefit.icon}</Text>
            </View>
            <Text style={styles.benefitTitle}>{benefit.title}</Text>
          </View>
        ))}
      </View>
    </View>
  );
});