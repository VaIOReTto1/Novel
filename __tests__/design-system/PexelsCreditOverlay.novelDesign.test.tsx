import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text, TouchableOpacity } from 'react-native';

import { PexelsCreditOverlay } from '../../src/design-system/media/PexelsCreditOverlay';

describe('Stage 7 PexelsCreditOverlay', () => {
  it('renders author and provider text and forwards press to callback', () => {
    const onOpenSource = jest.fn();
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <PexelsCreditOverlay
          authorName="Alex Green"
          sourceUrl="https://www.pexels.com/photo/example"
          onOpenSource={onOpenSource}
        />,
      );
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['Photo by Alex Green', 'View on Pexels']),
    );

    const button = renderer.root.findByType(TouchableOpacity);
    ReactTestRenderer.act(() => {
      button.props.onPress();
    });

    expect(onOpenSource).toHaveBeenCalledWith('https://www.pexels.com/photo/example');
  });
});
