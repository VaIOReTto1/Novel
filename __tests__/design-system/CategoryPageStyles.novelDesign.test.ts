import { createCategoryPageStyles } from '../../src/page/CategoryPage/styles/CategoryPageStyles';

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

describe('CategoryPage Stage 7 styles', () => {
  it('uses Stage 7 canvas, surface and border tokens for the browsing shell', () => {
    const styles = createCategoryPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topTabs.backgroundColor).toBe('#FFFDFC');
    expect(styles.topTabs.borderBottomColor).toBe('#E8DDD1');
    expect(styles.sidebar.backgroundColor).toBe('#FFFDFC');
    expect(styles.sidebar.borderRightColor).toBe('#E8DDD1');
    expect(styles.list.backgroundColor).toBe('#FFFDFC');
    expect(styles.card.borderColor).toBe('#E8DDD1');
  });
});
