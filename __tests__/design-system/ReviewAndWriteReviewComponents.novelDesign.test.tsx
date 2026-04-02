import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelBookBackground: '#E8E3CF',
  }),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/hooks/useReviewDetailPageStyles', () => ({
  useReviewDetailPageStyles: () => ({
    colors: {
      novelMain: '#C96A34',
      novelText: '#201A17',
      novelTextGray: '#6F6258',
      novelBackground: '#FFFDFC',
      novelBookBackground: '#E8E3CF',
    },
    styles: new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
  }),
}));

jest.mock('../../src/page/comment/WriteReviewPage/styles/WriteReviewPageStyles', () => ({
  createWriteReviewPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { TopBar as ReviewTopBar } from '../../src/page/comment/ReviewDetailPage/components/TopBar';
import { TopBar as WriteReviewTopBar } from '../../src/page/comment/WriteReviewPage/components/TopBar';
import { RatingInput } from '../../src/page/comment/WriteReviewPage/components/RatingInput';

describe('Review detail and write-review components novelDesign', () => {
  it('renders readable review-detail top-bar title', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<ReviewTopBar onBack={jest.fn()} />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toContain('评论详情');
  });

  it('renders readable write-review header and rating label', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <WriteReviewTopBar onBackPress={jest.fn()} canSubmit onSubmit={jest.fn()} />
          <RatingInput rating={4} onRatingChange={jest.fn()} />
        </>,
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
      expect.arrayContaining(['点评此书', '发布', '优秀']),
    );
  });
});
