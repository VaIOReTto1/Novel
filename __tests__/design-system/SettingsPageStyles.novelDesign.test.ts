import { createSettingsPageStyles } from '../../src/page/SettingsPage/settingspage/styles/SettingsPageStyles';

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

describe('SettingsPage Stage 7 styles', () => {
  it('uses Stage 7 canvas and surface colors for shell rows and panels', () => {
    const styles = createSettingsPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.topBar.borderBottomColor).toBe('#E8DDD1');
    expect(styles.settingRow.backgroundColor).toBe('#FFFDFC');
    expect(styles.settingRow.borderBottomColor).toBe('#E8DDD1');
    expect(styles.sectionHeader.backgroundColor).toBe('#FAF6F0');
    expect(styles.logoutModalContainer.backgroundColor).toBe('#FFFDFC');
  });
});
