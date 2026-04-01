import { createWriteReviewPageStyles } from '../../src/page/comment/WriteReviewPage/styles/WriteReviewPageStyles';

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

describe('WriteReviewPage Stage 7 styles', () => {
  it('uses Stage 7 canvas and paper surfaces for compose shell', () => {
    const styles = createWriteReviewPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.ratingContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.formContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.tipsContainer.backgroundColor).toBe('#F3ECE3');
    expect(styles.submitContainer.backgroundColor).toBe('#FFFDFC');
  });
});
