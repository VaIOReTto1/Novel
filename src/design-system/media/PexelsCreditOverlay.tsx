import React from 'react';
import {
  Linking,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

export interface PexelsCreditOverlayProps {
  authorName: string;
  sourceUrl: string;
  onOpenSource?: (url: string) => void;
}

export const PexelsCreditOverlay: React.FC<PexelsCreditOverlayProps> = ({
  authorName,
  sourceUrl,
  onOpenSource,
}) => {
  const handleOpenSource = React.useCallback(() => {
    if (onOpenSource) {
      onOpenSource(sourceUrl);
      return;
    }

    Linking.openURL(sourceUrl).catch(() => undefined);
  }, [onOpenSource, sourceUrl]);

  return (
    <View style={styles.container}>
      <Text style={styles.author}>{`Photo by ${authorName}`}</Text>
      <TouchableOpacity onPress={handleOpenSource}>
        <Text style={styles.link}>View on Pexels</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'rgba(22, 19, 17, 0.72)',
    borderRadius: 12,
    gap: 4,
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  author: {
    color: '#FFFDFC',
    fontSize: 12,
  },
  link: {
    color: '#E7BA74',
    fontSize: 12,
  },
});
