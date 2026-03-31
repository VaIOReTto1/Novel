import { stage7DarkTheme, stage7LightTheme } from '../../src/design-system/tokens/stage7Tokens';
import { resolveStage7Theme } from '../../src/design-system/tokens/resolveStage7Theme';

describe('resolveStage7Theme', () => {
  it('selects light and dark themes from current Novel colors', () => {
    expect(resolveStage7Theme('#FFFFFF')).toBe(stage7LightTheme);
    expect(resolveStage7Theme('#000000')).toBe(stage7DarkTheme);
  });
});
