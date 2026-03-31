import { createMemberCenterPageStyles } from '../../src/page/ScrollBox/MemberCenterPage/styles/MemberCenterPageStyles';

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

describe('MemberCenterPage Stage 7 styles', () => {
  it('uses Stage 7 canvas and paper surfaces for shell panels', () => {
    const styles = createMemberCenterPageStyles(lightColors as any, 'member');

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.menuDropdown.backgroundColor).toBe('#FFFDFC');
    expect(styles.comparisonTable.backgroundColor).toBe('#FFFDFC');
    expect(styles.bottomPurchaseContainer.backgroundColor).toBe('#FFFDFC');
  });
});
