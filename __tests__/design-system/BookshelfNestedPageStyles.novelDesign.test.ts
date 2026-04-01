import { createBookshelfPageStyles } from '../../src/page/BookshelfPage/pages/Bookshelf/styles/BookshelfPageStyles';

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

describe('Bookshelf nested page novelDesign styles', () => {
  it('uses novelDesign shell surfaces for tabs, empty state and shelf grid', () => {
    const styles = createBookshelfPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.tabsContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.postList?.backgroundColor ?? '#FAF6F0').toBe('#FAF6F0');
    expect(styles.gridCover.backgroundColor).toBe('#F3ECE3');
    expect(styles.emptyContainer.backgroundColor ?? '#FAF6F0').toBe('#FAF6F0');
    expect(styles.gridItem.backgroundColor).toBe('#FFFDFC');
  });
});
