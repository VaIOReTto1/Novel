import { shouldRenderStage7Showcase } from '../../src/web/webEntryConfig';

describe('webEntryConfig', () => {
  it('enables Stage 7 showcase only for explicit query flag', () => {
    expect(shouldRenderStage7Showcase('?stage7Showcase=1')).toBe(true);
    expect(shouldRenderStage7Showcase('?foo=bar')).toBe(false);
    expect(shouldRenderStage7Showcase('')).toBe(false);
  });
});
