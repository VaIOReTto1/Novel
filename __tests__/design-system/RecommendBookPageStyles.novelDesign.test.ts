import { createRecommendBookPageStyles } from '../../src/page/ScrollBox/RecommendBookPage/styles/RecommendBookPageStyles';

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

describe('RecommendBookPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for profile blocks, sections and service cards', () => {
    const styles = createRecommendBookPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.section.backgroundColor).toBe('#FFFDFC');
    expect(styles.subtab.backgroundColor).toBe('#F3ECE3');
    expect(styles.serviceItem.backgroundColor).toBe('#F3ECE3');
    expect(styles.serviceIconWrapper.backgroundColor).toBe('#FFFDFC');
    expect(styles.withdrawButtonText.color).toBe('#C96A34');
  });
});
