import { createCommentPageStyles } from '../../src/page/comment/CommentPage/styles/CommentPageStyles';

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

describe('CommentPage Stage 7 styles', () => {
  it('uses Stage 7 canvas and paper surfaces for shell and review panels', () => {
    const styles = createCommentPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.searchContainer.backgroundColor).toBe('#F3ECE3');
    expect(styles.ratingSection.backgroundColor).toBe('#FFFDFC');
    expect(styles.commentItem.backgroundColor).toBe('#FFFDFC');
    expect(styles.refreshIndicator.backgroundColor).toBe('#F3ECE3');
  });
});
