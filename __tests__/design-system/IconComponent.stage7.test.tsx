import React from 'react';
import ReactTestRenderer from 'react-test-renderer';

const mockStage7IconSpy = jest.fn(() => null);

jest.mock('../../src/design-system/icons/Stage7Icon', () => ({
  Stage7Icon: (props: any) => {
    mockStage7IconSpy(props);
    return null;
  },
}));

import IconComponent from '../../src/component/IconComponent';

describe('IconComponent Stage 7 bridge', () => {
  beforeEach(() => {
    mockStage7IconSpy.mockClear();
  });

  it('forwards legacy icon names to Stage7 semantic names', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <IconComponent name="settings" width={20} height={18} color="#123456" />,
      );
    });

    expect(mockStage7IconSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        name: 'legacy.settings',
        width: 20,
        height: 18,
        color: '#123456',
      }),
    );
  });
});
