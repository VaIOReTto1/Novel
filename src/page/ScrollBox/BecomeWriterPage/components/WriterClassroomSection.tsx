import React from 'react';
import { View, Text, Image, TouchableOpacity } from 'react-native';
import { CourseItem } from '../types';

interface WriterClassroomSectionProps {
  styles: any;
  courses: CourseItem[];
  onMorePress: () => void;
}

export const WriterClassroomSection: React.FC<WriterClassroomSectionProps> = React.memo(({
  styles,
  courses,
  onMorePress,
}) => {
  return (
    <View style={styles.section}>
      {/* 标题行 */}
      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>作家课堂</Text>
        <TouchableOpacity onPress={onMorePress}>
          <Text style={styles.moreLink}>更多 ›</Text>
        </TouchableOpacity>
      </View>

      {/* 两列网格布局 */}
      <View style={styles.coursesGrid}>
        {courses.map(course => (
          <View key={course.id} style={styles.courseCard}>
            <Image
              source={{ uri: course.coverUrl }}
              style={styles.courseCover}
            />
            <Text style={styles.courseTitle} numberOfLines={2}>
              {course.title}
            </Text>
          </View>
        ))}
      </View>
    </View>
  );
});
