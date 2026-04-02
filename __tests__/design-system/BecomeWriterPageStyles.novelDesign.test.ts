import { createBecomeWriterPageStyles } from '../../src/page/ScrollBox/BecomeWriterPage/styles/BecomeWriterPageStyles';

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

describe('BecomeWriterPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top chrome, sections and service cards', () => {
    const styles = createBecomeWriterPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.section.backgroundColor).toBe('#FFFDFC');
    expect(styles.announcementSection.backgroundColor).toBe('#FFFDFC');
    expect(styles.tabsContainer.backgroundColor).toBe('#F3ECE3');
    expect(styles.activeTab.backgroundColor).toBe('#FFFDFC');
    expect(styles.subtab.backgroundColor).toBe('#F3ECE3');
    expect(styles.benefitIconWrapper.backgroundColor).toBe('#F3ECE3');
    expect(styles.activityItem.backgroundColor).toBe('#FFFDFC');
  });
});
