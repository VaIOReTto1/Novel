import React, { useMemo } from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { DataStats, ServiceItem, TaskItem, UserInfo } from '../types';

interface WorkbenchOverviewSectionProps {
  styles: any;
  userInfo: UserInfo | null;
  dataStats: DataStats;
  services: ServiceItem[];
  focusTask: TaskItem | null;
  focusCount: number;
  onServicePress: (serviceId: string) => void;
  onFocusPress: (taskId: string) => void;
}

const buildMetrics = (dataStats: DataStats) => [
  {
    key: 'fans',
    label: '涨粉',
    value: String(dataStats.fans),
    hint: '本周新增',
  },
  {
    key: 'likes',
    label: '获赞',
    value: String(dataStats.likes),
    hint: '热度表现',
  },
  {
    key: 'replies',
    label: '回复',
    value: String(dataStats.replies),
    hint: '读者回流',
  },
];

export const WorkbenchOverviewSection: React.FC<WorkbenchOverviewSectionProps> = React.memo(
  ({
    styles,
    userInfo,
    dataStats,
    services,
    focusTask,
    focusCount,
    onServicePress,
    onFocusPress,
  }) => {
    const displayName = userInfo?.name ?? '创作者';
    const monogram = displayName.slice(0, 1);
    const toolItems = useMemo(() => services.slice(0, 4), [services]);
    const insightCopy = focusTask
      ? `互动回复正在带动内容回流，建议优先跟进「${focusTask.title}」。`
      : '本日互动表现稳定，建议优先维护高反馈内容。';

    return (
      <View style={styles.overviewCard}>
        <View style={styles.overviewAura} />

        <View style={styles.overviewHeader}>
          <View style={styles.avatarBadge}>
            <Text style={styles.avatarBadgeText}>{monogram}</Text>
          </View>

          <View style={styles.overviewHeaderContent}>
            <Text style={styles.overviewEyebrow}>增长总览</Text>
            <Text style={styles.overviewName}>{`Hi，${displayName}`}</Text>
            <Text style={styles.overviewMeta}>{`今日重点 ${focusCount} 项`}</Text>
          </View>

          <View style={styles.overviewValueBadge}>
            <Text style={styles.overviewValueLabel}>可提现</Text>
            <Text style={styles.overviewValueText}>{`¥${dataStats.withdrawable}`}</Text>
          </View>
        </View>

        <View style={styles.kpiRow}>
          {buildMetrics(dataStats).map((metric) => (
            <View key={metric.key} style={styles.kpiCard}>
              <Text style={styles.kpiLabel}>{metric.label}</Text>
              <Text style={styles.kpiValue}>{metric.value}</Text>
              <Text style={styles.kpiHint}>{metric.hint}</Text>
            </View>
          ))}
        </View>

        <View style={styles.insightCard}>
          <View style={styles.insightHeader}>
            <Text style={styles.insightTitle}>本日增长摘要</Text>
            <Text style={styles.insightAccent}>增长优先</Text>
          </View>
          <Text style={styles.insightText}>{insightCopy}</Text>
          {focusTask ? (
            <TouchableOpacity
              activeOpacity={0.85}
              onPress={() => onFocusPress(focusTask.id)}
              style={styles.insightAction}>
              <Text style={styles.insightActionText}>查看重点任务</Text>
            </TouchableOpacity>
          ) : null}
        </View>

        <View style={styles.toolSection}>
          <Text style={styles.toolSectionTitle}>运营工具</Text>

          <View style={styles.toolsGrid}>
            {toolItems.map((service) => (
              <TouchableOpacity
                key={service.id}
                activeOpacity={0.85}
                onPress={() => onServicePress(service.id)}
                style={styles.toolCard}>
                <Text style={styles.toolCardTitle}>{service.title}</Text>
                <Text numberOfLines={1} style={styles.toolCardDescription}>
                  {service.description}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    );
  },
);
