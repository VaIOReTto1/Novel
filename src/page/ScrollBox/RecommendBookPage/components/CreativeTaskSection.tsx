import React, { useCallback } from 'react';
import { View, Text, TouchableOpacity, Image } from 'react-native';
import { TaskItem } from '../types';

interface CreativeTaskSectionProps {
    styles: any;
    selectedTab: 'recommend' | 'book';
    tasks: Record<'recommend' | 'book', TaskItem[]>;
    onTabChange: (tab: 'recommend' | 'book') => void;
    onTaskPress: (taskId: string) => void;
    onViewAllPress: () => void;
}

export const CreativeTaskSection: React.FC<CreativeTaskSectionProps> = React.memo(({
    styles,
    selectedTab,
    tasks,
    onTabChange,
    onTaskPress,
    onViewAllPress,
}) => {
    const handleTabPress = useCallback((tab: 'recommend' | 'book') => {
        onTabChange(tab);
    }, [onTabChange]);

    const handleTaskPress = useCallback((taskId: string) => {
        onTaskPress(taskId);
    }, [onTaskPress]);

    const currentTasks = tasks[selectedTab];

    return (
        <View style={styles.section}>
            <View style={styles.sectionHeader}>
                <Text style={styles.sectionTitle}>创作任务</Text>
                <TouchableOpacity onPress={onViewAllPress}>
                    <Text style={styles.moreLink}>更多 ›</Text>
                </TouchableOpacity>
            </View>

            {/* Tab 切换 */}
            <View style={styles.subtabsContainer}>
                <TouchableOpacity
                    style={[styles.subtab, selectedTab === 'recommend' && styles.activeSubtab]}
                    onPress={() => handleTabPress('recommend')}
                    activeOpacity={0.7}
                >
                    <Text style={[
                        styles.subtabText,
                        selectedTab === 'recommend' && styles.activeSubtabText,
                    ]}>
                        推荐
                    </Text>
                </TouchableOpacity>

                <TouchableOpacity
                    style={[styles.subtab, selectedTab === 'book' && styles.activeSubtab]}
                    onPress={() => handleTabPress('book')}
                    activeOpacity={0.7}
                >
                    <Text style={[
                        styles.subtabText,
                        selectedTab === 'book' && styles.activeSubtabText,
                    ]}>
                        推书
                    </Text>
                </TouchableOpacity>
            </View>

            {/* 任务列表 */}
            <View style={styles.tasksList}>
                {currentTasks.map((task) => (
                    <View key={task.id} style={styles.taskItem}>
                        <View style={styles.taskMainRow}>
                            <Image source={{ uri: task.coverUrl }} style={styles.taskCover} />
                            <View style={styles.taskContent}>
                                <Text style={styles.taskTitle}>{task.title}</Text>
                                <Text style={styles.taskDescription} numberOfLines={2}>
                                    {task.description}
                                </Text>

                                <View style={styles.taskEarnings}>
                                    <Text style={styles.taskEarningsLabel}>当前最高收益</Text>
                                    <Text style={styles.taskEarningsAmount}>
                                        ¥{task.maxEarnings.toFixed(2)}
                                    </Text>
                                </View>

                                {/* 底部一行：左侧标签，右侧按钮 */}
                                <View style={styles.taskFooter}>
                                    <View style={styles.taskTags}>
                                        {task.tags.map((tag, idx) => (
                                            <View key={idx} style={styles.taskTag}>
                                                <Text style={styles.taskTagText}>{tag}</Text>
                                            </View>
                                        ))}
                                    </View>

                                    <TouchableOpacity
                                        style={styles.taskButton}
                                        onPress={() => handleTaskPress(task.id)}
                                        activeOpacity={0.7}
                                    >
                                        <Text style={styles.taskButtonText}>查看任务</Text>
                                    </TouchableOpacity>
                                </View>
                            </View>
                        </View>
                    </View>
                ))}
            </View>

            {/* 查看更多任务 */}
            <View style={styles.viewAllTasksContainer}>
                <TouchableOpacity
                    style={styles.viewAllTasksButton}
                    onPress={onViewAllPress}
                    activeOpacity={0.7}
                >
                    <Text style={styles.viewAllTasksText}>查看更多任务</Text>
                    <Text style={styles.viewAllTasksArrow}>›</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
});
