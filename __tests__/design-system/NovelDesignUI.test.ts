import { sp, wp } from '../../src/utils/theme/dimensions';
import { createNovelDesignUI } from '../../src/design-system/novelDesign/NovelDesignUI';

const lightColors = {
  novelMain: '#FF995D',
  novelMainLight: '#F86827',
  novelBookBackground: '#E8E3CF',
  novelBackground: '#FFFFFF',
  novelDivider: '#F7F7F8',
  novelSecondaryBackground: '#F7F7F8',
  novelText: '#000000',
  novelTextGray: '#7F7F7F',
  novelLightGray: '#DDDDDD',
  novelChipBackground: '#EBEDF0',
  novelError: '#FF995D',
  outline: '#E0E0E0',
};

const darkColors = {
  ...lightColors,
  novelBackground: '#000000',
};

describe('NovelDesignUI', () => {
  it('centralizes light-mode theme tokens and scaled shell metrics', () => {
    const ui = createNovelDesignUI(lightColors as any);

    expect(ui.mode).toBe('light');
    expect(ui.color.bg.canvas).toBe('#FAF6F0');
    expect(ui.presets.topBar.backgroundColor).toBe('#FFFDFC');
    expect(ui.presets.paperCard.borderColor).toBe('#E8DDD1');
    expect(ui.metrics.pageGutter).toBe(wp(16));
    expect(ui.metrics.writerCover.width).toBe(wp(100));
    expect(ui.metrics.writerCover.height).toBe(wp(130));
    expect(ui.metrics.cardRadius).toBe(sp(18));
  });

  it('switches dark-mode tokens from the same global UI config entry', () => {
    const ui = createNovelDesignUI(darkColors as any);

    expect(ui.mode).toBe('dark');
    expect(ui.color.bg.canvas).toBe('#161311');
    expect(ui.color.bg.surface).toBe('#211C19');
    expect(ui.presets.topBar.borderColor).toBe('#3A302B');
    expect(ui.color.text.primary).toBe('#F5EEE7');
  });
});
