import React from 'react';
import ReactTestRenderer from 'react-test-renderer';

const mockNovelDesignIconSpy = jest.fn(() => null);

jest.mock('../../src/design-system/icons/NovelDesignIcon', () => ({
  NovelDesignIcon: (props: any) => {
    mockNovelDesignIconSpy(props);
    return null;
  },
}));

import IconComponent from '../../src/component/IconComponent';

describe('IconComponent Stage 7 bridge', () => {
  beforeEach(() => {
    mockNovelDesignIconSpy.mockClear();
  });

  it('forwards legacy icon names to Stage7 semantic names', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <IconComponent name="settings" width={20} height={18} color="#123456" />,
      );
    });

    expect(mockNovelDesignIconSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        name: 'legacy.settings',
        width: 20,
        height: 18,
        color: '#123456',
      }),
    );
  });
});
