import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/design-system/icons/Stage7Icon', () => ({
  Stage7Icon: () => null,
}));

jest.mock('../../src/design-system/media/PlaceholderImage', () => ({
  PlaceholderImage: () => null,
}));

jest.mock('../../src/design-system/media/PexelsCreditOverlay', () => ({
  PexelsCreditOverlay: () => null,
}));

import { Stage7Showcase } from '../../src/design-system/showcase/Stage7Showcase';

describe('Stage7Showcase', () => {
  it('renders foundation, icon and media sections', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<Stage7Showcase />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining([
        'Stage 7 Showcase',
        'Foundations',
        'Icons',
        'Media',
        'Warm editorial tokens for RN and Android',
      ]),
    );
  });
});
