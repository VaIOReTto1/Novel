import { createFeedbackHelpPageStyles } from '../../src/page/ScrollBox/FeedbackHelpPage/styles/FeedbackHelpPageStyles';

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

describe('FeedbackHelpPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top bar, search and consult cards', () => {
    const styles = createFeedbackHelpPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.searchInputContainer.backgroundColor).toBe('#F3ECE3');
    expect(styles.userSection.backgroundColor).toBe('#FAF6F0');
    expect(styles.consultSection.backgroundColor).toBe('#FFFDFC');
    expect(styles.frequentSection.backgroundColor).toBe('#FFFDFC');
    expect(styles.frequentTitle.color).toBe('#C96A34');
  });
});
