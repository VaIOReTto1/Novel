import { createHomePageStyles } from '../../src/page/ProfilePage/styles/ProfilePageStyles';

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

describe('ProfilePage Stage 7 styles', () => {
  it('uses Stage 7 canvas and surface colors for the main shell', () => {
    const styles = createHomePageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.loginBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.scrollableContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.bottomBox.backgroundColor).toBe('#FFFDFC');
    expect(styles.waterfallBookItem.borderColor).toBe('#E8DDD1');
  });
});
