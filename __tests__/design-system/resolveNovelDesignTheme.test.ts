import { novelDesignDarkTheme, novelDesignLightTheme } from '../../src/design-system/tokens/novelDesignTokens';
import { resolveNovelDesignTheme } from '../../src/design-system/tokens/resolveNovelDesignTheme';

describe('resolveNovelDesignTheme', () => {
  it('selects light and dark themes from explicit mode first, then falls back to background', () => {
    expect(resolveNovelDesignTheme({ novelThemeMode: 'light', novelBackground: '#000000' })).toBe(
      novelDesignLightTheme,
    );
    expect(resolveNovelDesignTheme({ novelThemeMode: 'dark', novelBackground: '#FFFFFF' })).toBe(
      novelDesignDarkTheme,
    );
    expect(resolveNovelDesignTheme('#FFFFFF')).toBe(novelDesignLightTheme);
    expect(resolveNovelDesignTheme('#000000')).toBe(novelDesignDarkTheme);
  });
});
