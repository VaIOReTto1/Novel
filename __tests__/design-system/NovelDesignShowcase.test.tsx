import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/design-system/icons/NovelDesignIcon', () => ({
  NovelDesignIcon: () => null,
}));

jest.mock('../../src/design-system/media/PlaceholderImage', () => ({
  PlaceholderImage: () => null,
}));

jest.mock('../../src/design-system/media/PexelsCreditOverlay', () => ({
  PexelsCreditOverlay: () => null,
}));

import { NovelDesignShowcase } from '../../src/design-system/showcase/NovelDesignShowcase';

describe('NovelDesignShowcase', () => {
  it('renders foundation, icon and media sections', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<NovelDesignShowcase />);
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
        'Novel Design Showcase',
        'Foundations',
        'Icons',
        'Media',
        'Warm editorial tokens for RN and Android',
      ]),
    );
  });
});
