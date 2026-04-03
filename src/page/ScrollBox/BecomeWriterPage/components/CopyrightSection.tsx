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
      <View style={[styles.section, styles.transparentSectionHeader]}>
        <Text style={styles.sectionTitle}>版权衍生</Text>
        <Text style={styles.sectionSubtitle}>
          支持作品延展到动漫、短剧等更多内容形态
        </Text>
      </View>

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
