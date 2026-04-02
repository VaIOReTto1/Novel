import { createHistoryPageStyles } from '../../src/page/BookshelfPage/pages/History/styles/HistoryPageStyles';

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

describe('HistoryPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for tabs, cards and action pills', () => {
    const styles = createHistoryPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.tabsContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.tab.backgroundColor).toBe('#F3ECE3');
    expect(styles.gridCover.backgroundColor).toBe('#F3ECE3');
    expect(styles.gridAddToShelfButton.backgroundColor).toBe('#F3ECE3');
    expect(styles.listItem.backgroundColor).toBe('#FFFDFC');
  });
});
