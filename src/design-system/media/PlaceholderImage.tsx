import React from 'react';
import { Image, ImageProps, ImageStyle, StyleProp } from 'react-native';

import { buildPicsumUrl } from './picsum';

export interface PlaceholderImageProps {
  width: number;
  height: number;
  seed: string;
  grayscale?: boolean;
  blur?: number;
  retryCount?: number;
  cachePolicy?: ImageProps['source'] extends infer T
    ? T extends { cache?: infer C }
      ? C
      : 'force-cache'
    : 'force-cache';
  accessibilityLabel?: string;
  style?: StyleProp<ImageStyle>;
}

export const PlaceholderImage: React.FC<PlaceholderImageProps> = ({
  width,
  height,
  seed,
  grayscale = false,
  blur = 0,
  retryCount = 1,
  cachePolicy = 'force-cache',
  accessibilityLabel,
  style,
}) => {
  const [attempt, setAttempt] = React.useState(0);

  const source = React.useMemo(
    () => ({
      uri: buildPicsumUrl({
        width,
        height,
        seed,
        grayscale,
        blur,
        attempt,
      }),
      cache: cachePolicy,
    }),
    [attempt, blur, cachePolicy, grayscale, height, seed, width],
  );

  return (
    <Image
      accessibilityLabel={accessibilityLabel}
      source={source}
      style={[{ width, height }, style]}
      onError={() => {
        if (attempt < retryCount) {
          setAttempt((current) => current + 1);
        }
      }}
    />
  );
};
