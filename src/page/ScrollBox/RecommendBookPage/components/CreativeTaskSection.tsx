import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { TaskItem } from '../types';

interface CreativeTaskSectionProps {
  styles: any;
  selectedTab: 'recommend' | 'book';
  tasks: Record<'recommend' | 'book', TaskItem[]>;
  onTabChange: (tab: 'recommend' | 'book') => void;
  onTaskPress: (taskId: string) => void;
  onViewAllPress: () => void;
}

const formatCurrency = (value: number) => `¥${value.toFixed(0)}`;

const getCoverToneStyle = (styles: any, tone?: TaskItem['coverTone']) => {
  switch (tone) {
    case 'sunrise':
      return styles.taskCoverSunrise;
    case 'sage':
      return styles.taskCoverSage;
    case 'ink':
      return styles.taskCoverInk;
    case 'sand':
    default:
      return styles.taskCoverSand;
  }
};

const TaskCoverCard: React.FC<{
  styles: any;
  task: TaskItem;
  compact?: boolean;
}> = ({ styles, task, compact = false }) => {
  const supportLabel = task.tags[0] ?? '增长专题';

  return (
    <View
      style={[
        styles.taskCoverCard,
        compact ? styles.taskCoverCardCompact : null,
        getCoverToneStyle(styles, task.coverTone),
      ]}>
      <View style={styles.taskCoverHeader}>
        <Text style={styles.taskCoverType}>{task.type === 'recommend' ? '推荐' : '推书'}</Text>
        <View style={styles.taskCoverBadge}>
          <Text style={styles.taskCoverBadgeText}>{supportLabel}</Text>
        </View>
      </View>

      <View style={styles.taskCoverFooter}>
        <Text numberOfLines={compact ? 1 : 2} style={styles.taskCoverLabel}>
          {task.coverLabel ?? task.title.slice(0, compact ? 4 : 6)}
        </Text>
        {!compact ? (
          <Text numberOfLines={1} style={styles.taskCoverSupport}>
            {task.growthGoal ?? task.description}
          </Text>
        ) : null}
      </View>
    </View>
  );
};

export const CreativeTaskSection: React.FC<CreativeTaskSectionProps> = React.memo(
  ({ styles, selectedTab, tasks, onTabChange, onTaskPress, onViewAllPress }) => {
    const currentTasks = tasks[selectedTab];
    const primaryTask = currentTasks[0] ?? null;
    const secondaryTasks = currentTasks.slice(1);

    return (
      <View style={styles.taskQueueCard}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>增长任务队列</Text>

          <TouchableOpacity activeOpacity={0.7} onPress={onViewAllPress}>
            <Text style={styles.moreLink}>全部任务 &gt;</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.subtabsContainer}>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => onTabChange('recommend')}
            style={[styles.subtab, selectedTab === 'recommend' && styles.activeSubtab]}>
            <Text
              style={[
                styles.subtabText,
                selectedTab === 'recommend' && styles.activeSubtabText,
              ]}>
              推荐
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => onTabChange('book')}
            style={[styles.subtab, selectedTab === 'book' && styles.activeSubtab]}>
            <Text
              style={[
                styles.subtabText,
                selectedTab === 'book' && styles.activeSubtabText,
              ]}>
              推书
            </Text>
          </TouchableOpacity>
        </View>

        {!primaryTask ? (
          <View style={styles.emptyState}>
            <Text style={styles.emptyStateText}>当前暂无增长任务</Text>
          </View>
        ) : (
          <>
            <View style={styles.primaryTaskCard}>
              <TaskCoverCard styles={styles} task={primaryTask} />

              <View style={styles.primaryTaskContent}>
                <Text style={styles.primaryTaskTitle}>{primaryTask.title}</Text>
                <Text style={styles.primaryTaskDescription}>{primaryTask.description}</Text>

                <View style={styles.taskMetricRow}>
                  <View style={styles.taskMetricBlock}>
                    <Text style={styles.taskMetricLabel}>增长目标</Text>
                    <Text style={styles.taskMetricValue}>
                      {primaryTask.growthGoal ?? primaryTask.description}
                    </Text>
                  </View>

                  <View style={styles.taskMetricBlock}>
                    <Text style={styles.taskMetricLabel}>预计收益</Text>
                    <Text style={styles.taskMetricValueAccent}>
                      {formatCurrency(primaryTask.maxEarnings)}
                    </Text>
                  </View>
                </View>

                <View style={styles.taskTags}>
                  {primaryTask.tags.map((tag) => (
                    <View key={`${primaryTask.id}-${tag}`} style={styles.taskTag}>
                      <Text style={styles.taskTagText}>{tag}</Text>
                    </View>
                  ))}
                </View>

                <TouchableOpacity
                  activeOpacity={0.85}
                  onPress={() => onTaskPress(primaryTask.id)}
                  style={styles.taskActionButton}>
                  <Text style={styles.taskActionButtonText}>立即跟进</Text>
                </TouchableOpacity>
              </View>
            </View>

            <View style={styles.secondaryTaskList}>
              {secondaryTasks.map((task) => (
                <View key={task.id} style={styles.secondaryTaskCard}>
                  <TaskCoverCard compact styles={styles} task={task} />

                  <View style={styles.secondaryTaskContent}>
                    <Text style={styles.secondaryTaskTitle}>{task.title}</Text>
                    <Text numberOfLines={2} style={styles.secondaryTaskDescription}>
                      {task.description}
                    </Text>

                    <View style={styles.secondaryTaskFooter}>
                      <Text style={styles.secondaryTaskValue}>
                        {formatCurrency(task.maxEarnings)}
                      </Text>

                      <TouchableOpacity
                        activeOpacity={0.8}
                        onPress={() => onTaskPress(task.id)}
                        style={styles.secondaryTaskAction}>
                        <Text style={styles.secondaryTaskActionText}>查看任务</Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                </View>
              ))}
            </View>
          </>
        )}
      </View>
    );
  },
);
