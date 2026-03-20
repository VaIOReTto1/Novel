import React from 'react';
import { View, Text } from 'react-native';
import { BenefitComparison as BenefitComparisonType } from '../types';

interface BenefitComparisonProps {
  styles: any;
  comparisons: BenefitComparisonType[];
  currentCardType: string;
}

export const BenefitComparison: React.FC<BenefitComparisonProps> = React.memo(({
  styles,
  comparisons,
  currentCardType,
}) => {
  // 根据当前VIP类型确定要显示的两列
  const getDisplayColumns = () => {
    switch (currentCardType) {
      case 'member':
        return ['member', 'svip']; // 会员VIP显示会员和SVIP
      case 'svip':
        return ['svip', 'adfree']; // SVIP显示SVIP和免广告
      case 'adfree':
        return ['adfree', 'svip']; // 免广告VIP显示免广告和SVIP
      default:
        return ['member', 'svip'];
    }
  };

  const displayColumns = getDisplayColumns();
  const primaryColumn = displayColumns[0]; // 当前选中的VIP类型
  const secondaryColumn = displayColumns[1]; // 对比的VIP类型

  // 获取列的值
  const getColumnValue = (comparison: BenefitComparisonType, column: string) => {
    switch (column) {
      case 'member':
        return comparison.member;
      case 'svip':
        return comparison.svip;
      case 'adfree':
        return comparison.adfree || '';
      default:
        return '——';
    }
  };

  // 获取列标题
  const getDisplayColumnTitle = (column: string) => {
    switch (column) {
      case 'member':
        return '会员VIP';
      case 'svip':
        return 'SVIP';
      case 'adfree':
        return '免广告VIP';
      default:
        return '';
    }
  };

  return (
    <View style={styles.comparisonContainer}>
      <Text style={styles.comparisonTitle}>会员权益对比</Text>
      
      <View style={styles.comparisonTable}>
        {/* 表头 */}
        <View style={styles.comparisonHeader}>
          <View style={styles.comparisonHeaderCell}>
            <Text style={styles.comparisonHeaderText}>权益</Text>
          </View>
          <View style={[
            styles.comparisonHeaderCellHighlight,
            styles.comparisonHeaderCell
          ]}>
            <Text style={[
              styles.comparisonHeaderText,
              styles.comparisonHighlight
            ]}>
              {getDisplayColumnTitle(primaryColumn)}
            </Text>
          </View>
          <View style={styles.comparisonHeaderCell}>
            <Text style={styles.comparisonHeaderText}>
              {getDisplayColumnTitle(secondaryColumn)}
            </Text>
          </View>
        </View>

        {/* 表格内容 */}
        {comparisons.map((comparison, index) => (
          <View key={index} style={styles.comparisonRow}>
            <View style={styles.comparisonCell}>
              <Text style={styles.comparisonCellText}>{comparison.category}</Text>
            </View>
            <View style={[
              styles.comparisonCell,
              styles.comparisonCellHighlight
            ]}>
              <Text style={[
                styles.comparisonCellText,
                styles.comparisonHighlight
              ]}>
                {getColumnValue(comparison, primaryColumn)}
              </Text>
            </View>
            <View style={styles.comparisonCell}>
              <Text style={styles.comparisonCellText}>
                {getColumnValue(comparison, secondaryColumn)}
              </Text>
            </View>
          </View>
        ))}
      </View>
    </View>
  );
});
