import { novelDesignDarkTheme, novelDesignLightTheme } from '../../src/design-system/tokens/novelDesignTokens';
import { resolveNovelDesignTheme } from '../../src/design-system/tokens/resolveNovelDesignTheme';

describe('resolveNovelDesignTheme', () => {
  it('selects light and dark themes from current Novel colors', () => {
    expect(resolveNovelDesignTheme('#FFFFFF')).toBe(novelDesignLightTheme);
    expect(resolveNovelDesignTheme('#000000')).toBe(novelDesignDarkTheme);
  });
});
