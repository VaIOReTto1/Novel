import { createWatchlistPageStyles } from '../../src/page/BookshelfPage/pages/Watchlist/styles/WatchlistPageStyles';

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

describe('WatchlistPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top bar, edit rail and grid cards', () => {
    const styles = createWatchlistPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBarContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.editToolbar.backgroundColor).toBe('#F3ECE3');
    expect(styles.gridItem.backgroundColor).toBe('#FFFDFC');
    expect(styles.adBanner.backgroundColor).toBe('#F3ECE3');
    expect(styles.gridItemImage.backgroundColor).toBe('#F3ECE3');
  });
});
