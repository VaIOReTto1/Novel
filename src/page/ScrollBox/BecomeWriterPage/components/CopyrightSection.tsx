import React from 'react';
import { View, Text, Image } from 'react-native';
import { CopyrightWork } from '../types';

interface CopyrightSectionProps {
  styles: any;
  copyrightWorks: CopyrightWork[];
}

export const CopyrightSection: React.FC<CopyrightSectionProps> = React.memo(({
  styles,
  copyrightWorks,
}) => {
  return (
    <View>
      {/* 标题与副标题：移到外层 */}
      <View style={[styles.section, styles.transparentSectionHeader]}>
        <Text style={styles.sectionTitle}>版权改编</Text>
        <Text style={styles.sectionSubtitle}>
          助力你的作品改编为爆火动漫、优秀短剧
        </Text>
      </View>

      {/* 白底卡片区域，只包裹网格 */}
      <View style={styles.section}>
        <View style={styles.copyrightGrid}>
          {copyrightWorks.map((work) => (
            <View key={work.id} style={styles.copyrightItem}>
              <Image
                source={{ uri: work.coverUrl }}
                style={styles.copyrightCover}
              />
              <Text style={styles.copyrightTitle} numberOfLines={1}>
                {work.title}
              </Text>
            </View>
          ))}
        </View>
      </View>
    </View>
  );
});
