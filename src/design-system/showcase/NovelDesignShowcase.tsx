import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';

import { NovelDesignIcon } from '../icons/NovelDesignIcon';
import { PlaceholderImage } from '../media/PlaceholderImage';
import { PexelsCreditOverlay } from '../media/PexelsCreditOverlay';
import { novelDesignLightTheme } from '../tokens/novelDesignTokens';
import { novelDesignShowcaseSections } from './showcaseData';

export const NovelDesignShowcase: React.FC = () => {
  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Text style={styles.eyebrow}>Novel Design Showcase</Text>
      <Text style={styles.title}>Novel Visual System</Text>

      {novelDesignShowcaseSections.map((section) => (
        <View key={section.title} style={styles.section}>
          <Text style={styles.sectionTitle}>{section.title}</Text>
          <Text style={styles.sectionDescription}>{section.description}</Text>
        </View>
      ))}

      <View style={styles.demoRow}>
        <NovelDesignIcon
          name="legacy.settings"
          width={24}
          height={24}
          color={novelDesignLightTheme.color.brand.primary}
        />
        <PlaceholderImage width={120} height={80} seed="novel-design-showcase" blur={2} />
      </View>

      <PexelsCreditOverlay
        authorName="Novel Design Demo Author"
        sourceUrl="https://www.pexels.com/photo/example"
      />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: novelDesignLightTheme.color.bg.canvas,
  },
  content: {
    gap: 16,
    padding: novelDesignLightTheme.space[300],
  },
  eyebrow: {
    color: novelDesignLightTheme.color.brand.secondary,
    fontSize: novelDesignLightTheme.typography.label.sm.size,
  },
  title: {
    color: novelDesignLightTheme.color.text.primary,
    fontSize: novelDesignLightTheme.typography.title.hero.size,
    fontWeight: '600',
  },
  section: {
    backgroundColor: novelDesignLightTheme.color.bg.surface,
    borderColor: novelDesignLightTheme.color.border.subtle,
    borderRadius: novelDesignLightTheme.radius.lg,
    borderWidth: 1,
    gap: 8,
    padding: novelDesignLightTheme.space[200],
  },
  sectionTitle: {
    color: novelDesignLightTheme.color.text.primary,
    fontSize: novelDesignLightTheme.typography.title.section.size,
    fontWeight: '600',
  },
  sectionDescription: {
    color: novelDesignLightTheme.color.text.secondary,
    fontSize: novelDesignLightTheme.typography.body.md.size,
  },
  demoRow: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: 16,
  },
});
