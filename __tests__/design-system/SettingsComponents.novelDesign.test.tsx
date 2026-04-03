import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelDivider: '#E8DDD1',
    novelSecondaryBackground: '#F3ECE3',
    novelChipBackground: '#EBEDF0',
    novelError: '#B3453C',
  }),
}));

jest.mock('../../src/page/SettingsPage/settingspage/styles/SettingsPageStyles', () => ({
  createSettingsPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/component/IconComponent', () => ({
  __esModule: true,
  default: () => null,
}));

import { SettingRow } from '../../src/page/SettingsPage/settingspage/components/SettingRow';
import ThemeSwitcher from '../../src/page/SettingsPage/settingspage/components/ThemeSwitcher';

describe('Settings components novelDesign', () => {
  it('renders readable row action copy and chevron', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <SettingRow
          item={{
            id: 'privacy',
            title: '隐私设置',
            type: 'arrow',
            value: '已开启',
          }}
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
      expect.arrayContaining(['隐私设置', '已开启', '>']),
    );
  });

  it('renders chinese accessibility label for theme switcher', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <ThemeSwitcher isDark={false} onToggle={jest.fn()} />,
      );
    });

    const pressable = renderer.root.findByProps({ accessibilityRole: 'switch' });
    expect(pressable.props.accessibilityLabel).toBe('切换主题');
  });
});
