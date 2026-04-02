import { createHelpSupportPageStyles } from '../../src/page/SettingsPage/helpsupportPage/styles/HelpSupportPageStyles';

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

describe('HelpSupportPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top bar and document body', () => {
    const styles = createHelpSupportPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.topBar.borderBottomColor).toBe('#E8DDD1');
    expect(styles.descriptionContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.descriptionText.color).toBe('#6F6258');
    expect(styles.topBarTitle.color).toBe('#201A17');
  });
});
