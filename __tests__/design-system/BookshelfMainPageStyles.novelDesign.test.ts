import { createMainPageStyles } from '../../src/page/BookshelfPage/styles/MainPageStyles';

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

describe('Bookshelf MainPage Stage 7 styles', () => {
  it('uses Stage 7 canvas, surface and border tokens for the shell and tabs', () => {
    const styles = createMainPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.tabBarContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.tabBarContainer.borderBottomColor).toBe('#E8DDD1');
    expect(styles.pageContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.errorContainer.backgroundColor).toBe('#FFFDFC');
  });
});
