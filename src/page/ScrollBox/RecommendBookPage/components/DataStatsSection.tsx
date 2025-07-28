import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { DataStats } from '../types';

interface DataStatsSectionProps {
  styles: any;
  dataStats: DataStats;
  onWithdrawPress: () => void;
}

export const DataStatsSection: React.FC<DataStatsSectionProps> = React.memo(({
  styles,
  dataStats,
  onWithdrawPress,
}) => {
  const currentStats = dataStats;

  return (
    <View style={styles.section}>

      {/* Stats Display */}
      <View style={styles.statsContainer}>
        <View style={styles.statItem}>
          <Text style={styles.statLabel}>近7天涨粉</Text>
          <Text style={styles.statNumber}>{currentStats.fans}</Text>
          <Text style={styles.statBottomLabel}>昨日 0</Text>
        </View>
        <View style={styles.statItem}>
          <Text style={styles.statLabel}>近7天获赞</Text>
          <Text style={styles.statNumber}>{currentStats.likes}</Text>
          <Text style={styles.statBottomLabel}>昨日 0</Text>
        </View>
        <View style={styles.statItem}>
          <Text style={styles.statLabel}>近7天回复</Text>
          <Text style={styles.statNumber}>{currentStats.replies}</Text>
          <Text style={styles.statBottomLabel}>昨日 0</Text>
        </View>
        <View style={styles.statItem}>
          <Text style={styles.statLabel}>可提现收益(元)</Text>
          <Text style={styles.statNumber}>{currentStats.withdrawable}</Text>
          <TouchableOpacity onPress={onWithdrawPress} style={styles.withdrawButton}>
            <Text style={styles.withdrawButtonText}>去提现</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
});
