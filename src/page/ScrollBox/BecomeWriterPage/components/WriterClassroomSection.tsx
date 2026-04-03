import React from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';

import { CourseItem } from '../types';

interface WriterClassroomSectionProps {
  styles: any;
  courses: CourseItem[];
  onMorePress: () => void;
}

export const WriterClassroomSection: React.FC<WriterClassroomSectionProps> =
  React.memo(({ styles, courses, onMorePress }) => {
    return (
      <View style={styles.section}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>作者课堂</Text>
          <TouchableOpacity onPress={onMorePress}>
            <Text style={styles.moreLink}>更多 &gt;</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.coursesGrid}>
          {courses.map((course) => (
            <View key={course.id} style={styles.courseCard}>
              <Image source={{ uri: course.coverUrl }} style={styles.courseCover} />
              <Text style={styles.courseTitle} numberOfLines={2}>
                {course.title}
              </Text>
            </View>
          ))}
        </View>
      </View>
    );
  });
