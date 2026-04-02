import { createHistoryPageStyles } from '../../src/page/ScrollBox/HistoryPage/styles/HistoryPageStyles';

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

describe('ScrollBox HistoryPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top bar, tabs and history cards', () => {
    const styles = createHistoryPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.tabsContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.tabsDivider.backgroundColor).toBe('#E8DDD1');
    expect(styles.gridCover.backgroundColor).toBe('#F3ECE3');
    expect(styles.listCover.backgroundColor).toBe('#F3ECE3');
    expect(styles.gridProgress.color).toBe('#C96A34');
    expect(styles.activeTabText.color).toBe('#201A17');
  });
});
