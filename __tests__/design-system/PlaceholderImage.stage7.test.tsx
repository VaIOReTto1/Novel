import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Image } from 'react-native';

import { buildPicsumUrl } from '../../src/design-system/media/picsum';
import { PlaceholderImage } from '../../src/design-system/media/PlaceholderImage';

describe('Stage 7 PlaceholderImage', () => {
  it('builds a deterministic picsum url', () => {
    const url = buildPicsumUrl({
      width: 240,
      height: 320,
      seed: 'bookshelf-card',
      grayscale: true,
      blur: 3,
      attempt: 0,
    });

    expect(url).toBe(
      'https://picsum.photos/seed/bookshelf-card/240/320?grayscale=1&blur=3&retry=0',
    );
  });

  it('renders Image with picsum source and retries with incremented attempt', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <PlaceholderImage
          width={240}
          height={320}
          seed="bookshelf-card"
          grayscale
          blur={3}
          retryCount={2}
          accessibilityLabel="placeholder"
        />,
      );
    });

    const firstImage = renderer.root.findByType(Image);
    expect(firstImage.props.source.uri).toBe(
      'https://picsum.photos/seed/bookshelf-card/240/320?grayscale=1&blur=3&retry=0',
    );

    ReactTestRenderer.act(() => {
      firstImage.props.onError?.();
    });

    const retriedImage = renderer.root.findByType(Image);
    expect(retriedImage.props.source.uri).toBe(
      'https://picsum.photos/seed/bookshelf-card/240/320?grayscale=1&blur=3&retry=1',
    );
  });
});
