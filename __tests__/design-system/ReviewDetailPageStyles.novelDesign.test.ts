import { createReviewDetailPageStyles } from '../../src/page/comment/ReviewDetailPage/styles/ReviewDetailPageStyles';

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

describe('ReviewDetailPage Stage 7 styles', () => {
  it('uses Stage 7 canvas and paper surfaces for review detail shell', () => {
    const styles = createReviewDetailPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.topBar.borderBottomColor).toBe('#E8DDD1');
    expect(styles.reviewDetailContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.commentItem.backgroundColor).toBe('#FFFDFC');
    expect(styles.actionButton.backgroundColor).toBe('#FFFDFC');
    expect(styles.commentInputBox.backgroundColor).toBe('#F3ECE3');
    expect(styles.modalTopBar.borderBottomColor).toBe('#E8DDD1');
    expect(styles.bottomInputContainer.backgroundColor).toBe('#FFFDFC');
  });
});
