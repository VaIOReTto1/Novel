import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

import { ConsultSectionProps } from '../types';

export const ConsultSection: React.FC<ConsultSectionProps> = React.memo(({
  styles,
  categories,
  onCategoryPress,
}) => {
  return (
    <View style={styles.consultSection}>
      <View style={styles.consultHeader}>
        <Text style={styles.consultTitle}>咨询场景</Text>
        <TouchableOpacity style={styles.moreButton}>
          <Text style={styles.moreText}>更多</Text>
          <Text style={styles.moreIcon}>{'>'}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.consultGrid}>
        {categories.map((category) => (
          <TouchableOpacity
            key={category.id}
            style={styles.consultItem}
            onPress={() => onCategoryPress(category.id)}
            activeOpacity={0.8}>
            <LinearGradient
              colors={category.bgGradient}
              start={{ x: 0, y: 0 }}
              end={{ x: 1, y: 1 }}
              style={styles.consultGradient}>
              <View style={styles.consultItemHeader}>
                <Text style={styles.consultItemTitle}>{category.title}</Text>
                <Text style={styles.consultArrow}>{'>'}</Text>
              </View>

              <View style={styles.consultItemContent}>
                {category.items.map((item, index) => (
                  <Text key={index} style={styles.consultItemText}>
                    {item}
                  </Text>
                ))}
              </View>
            </LinearGradient>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
});
