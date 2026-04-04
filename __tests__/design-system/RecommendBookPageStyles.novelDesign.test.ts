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

describe('RecommendBookPage growth dashboard styles', () => {
  it('defines a stronger hero card and tone-based task covers with warm tokens', () => {
    const styles = createRecommendBookPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.overviewCard).toMatchObject({
      backgroundColor: '#FFFDFC',
      borderColor: '#E8DDD1',
      shadowColor: '#C96A34',
    });
    expect(styles.kpiCard.backgroundColor).toBe('#F3ECE3');
    expect(styles.insightCard.backgroundColor).toBe('#FAF6F0');
    expect(styles.toolCard.backgroundColor).toBe('#FFFDFC');
    expect(styles.primaryTaskCard.backgroundColor).toBe('#FFFDFC');
    expect(styles.taskCoverSand.backgroundColor).toBe('#E8E3CF');
    expect(styles.taskCoverSunrise.backgroundColor).toBeDefined();
    expect(styles.taskCoverSage.backgroundColor).toBeDefined();
    expect(styles.taskCoverInk.backgroundColor).toBeDefined();
    expect(styles.taskActionButton.backgroundColor).toBe('#C96A34');
    expect(styles.taskActionButtonText.color).toBe('#FFFDFC');
  });
});
