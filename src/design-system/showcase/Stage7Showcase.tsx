import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';

import { Stage7Icon } from '../icons/Stage7Icon';
import { PlaceholderImage } from '../media/PlaceholderImage';
import { PexelsCreditOverlay } from '../media/PexelsCreditOverlay';
import { stage7LightTheme } from '../tokens/stage7Tokens';
import { stage7ShowcaseSections } from './showcaseData';

export const Stage7Showcase: React.FC = () => {
  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Text style={styles.eyebrow}>Stage 7 Showcase</Text>
      <Text style={styles.title}>Novel Visual System</Text>

      {stage7ShowcaseSections.map((section) => (
        <View key={section.title} style={styles.section}>
          <Text style={styles.sectionTitle}>{section.title}</Text>
          <Text style={styles.sectionDescription}>{section.description}</Text>
        </View>
      ))}

      <View style={styles.demoRow}>
        <Stage7Icon
          name="legacy.settings"
          width={24}
          height={24}
          color={stage7LightTheme.color.brand.primary}
        />
        <PlaceholderImage width={120} height={80} seed="stage7-showcase" blur={2} />
      </View>

      <PexelsCreditOverlay
        authorName="Stage 7 Demo Author"
        sourceUrl="https://www.pexels.com/photo/example"
      />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: stage7LightTheme.color.bg.canvas,
  },
  content: {
    gap: 16,
    padding: stage7LightTheme.space[300],
  },
  eyebrow: {
    color: stage7LightTheme.color.brand.secondary,
    fontSize: stage7LightTheme.typography.label.sm.size,
  },
  title: {
    color: stage7LightTheme.color.text.primary,
    fontSize: stage7LightTheme.typography.title.hero.size,
    fontWeight: '600',
  },
  section: {
    backgroundColor: stage7LightTheme.color.bg.surface,
    borderColor: stage7LightTheme.color.border.subtle,
    borderRadius: stage7LightTheme.radius.lg,
    borderWidth: 1,
    gap: 8,
    padding: stage7LightTheme.space[200],
  },
  sectionTitle: {
    color: stage7LightTheme.color.text.primary,
    fontSize: stage7LightTheme.typography.title.section.size,
    fontWeight: '600',
  },
  sectionDescription: {
    color: stage7LightTheme.color.text.secondary,
    fontSize: stage7LightTheme.typography.body.md.size,
  },
  demoRow: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: 16,
  },
});
