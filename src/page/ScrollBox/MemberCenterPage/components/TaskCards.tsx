import React, { useCallback, useMemo } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

import { TaskCard } from '../types';
import { VIP_COLORS } from '../styles/MemberCenterPageStyles';

interface TaskCardsProps {
  styles: any;
  taskCards: TaskCard[];
  onTaskPress: (taskId: string) => void;
}

export const TaskCards: React.FC<TaskCardsProps> = React.memo(
  ({ styles, taskCards, onTaskPress }) => {
    /** 点任务按钮 */
    const handleTaskPress = useCallback(
      (taskId: string) => onTaskPress(taskId),
      [onTaskPress],
    );

    const gradientWithAlpha = useMemo(
      () => VIP_COLORS.memberGradient.map((c) => `${c}50`),
      [],
    );

    /** 列表为空不渲染 */
    if (!taskCards || taskCards.length === 0) {
      return null;
    }

    return (
      /* ================= 云朵渐变背景卡片 ================= */
      <LinearGradient
        colors={gradientWithAlpha}    //
        locations={[0, 0.35, 1]}                     // 深色仅 10%
        start={{ x: 0, y: 0 }}                      // 左上开始
        end={{ x: 1, y: 1 }}                        // 右下结束
        useAngle
        angle={135}
        style={styles.taskCardsGradient}
      >
        {/* ----------- 标题 ----------- */}
        <Text style={styles.taskCardsTitle}>做任务兑体验时长</Text>
        <View style={styles.taskCardsDivider} />

        {/* ----------- 任务列表 ----------- */}
        <View style={styles.taskCardsList}>
          {taskCards.map((task) => (
            <View key={task.id} style={styles.taskCardItem}>
              {/* 文案区 */}
              <View style={styles.taskCardInfo}>
                <Text style={styles.taskCardTitle}>{task.title}</Text>

              {/* ===== badgeText  rewardText 水平排布 ===== */}
              {(task.badgeText || task.rewardText) && (
                <View style={styles.taskCardRewardRow}>
                  {task.badgeText ? (
                    <Text style={styles.taskCardBadge}>{task.badgeText}</Text>
                  ) : null}

                  {task.rewardText ? (
                    <Text style={styles.taskCardRewardText}>{task.rewardText}</Text>
                  ) : null}
                </View>
              )}
              </View>

              {/* 右侧按钮 */}
              <TouchableOpacity
                activeOpacity={0.8}
                disabled={task.isCompleted}
                onPress={() => handleTaskPress(task.id)}
                style={[
                  styles.taskCardButton,
                  task.isCompleted && styles.taskCardButtonCompleted,
                ]}
              >
                <Text
                  style={[
                    styles.taskCardButtonText,
                    task.isCompleted && styles.taskCardButtonTextCompleted,
                  ]}
                >
                  {task.isCompleted ? '已完成' : task.actionText || '去完成'}
                </Text>
              </TouchableOpacity>
            </View>
          ))}
        </View>
      </LinearGradient>
    );
  },
);
