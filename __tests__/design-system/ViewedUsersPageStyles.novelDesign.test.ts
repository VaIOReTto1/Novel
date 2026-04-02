import { createViewedUsersPageStyles } from '../../src/page/ScrollBox/ViewedUsersPage/styles/ViewedUsersPageStyles';

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

describe('ViewedUsersPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top bar, tabs and follow cards', () => {
    const styles = createViewedUsersPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.tabsContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.emptyIcon.backgroundColor).toBe('#F3ECE3');
    expect(styles.followButton.backgroundColor).toBe('#FFFDFC');
    expect(styles.followButtonText.color).toBe('#C96A34');
    expect(styles.loadingText.color).toBe('#6F6258');
  });
});
