import { shouldRenderNovelDesignShowcase } from '../../src/web/webEntryConfig';

describe('webEntryConfig', () => {
  it('enables Stage 7 showcase only for explicit query flag', () => {
    expect(shouldRenderNovelDesignShowcase('?novelDesignShowcase=1')).toBe(true);
    expect(shouldRenderNovelDesignShowcase('?foo=bar')).toBe(false);
    expect(shouldRenderNovelDesignShowcase('')).toBe(false);
  });
});
