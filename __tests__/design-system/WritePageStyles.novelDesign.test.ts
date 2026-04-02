import { createWritePageStyles } from '../../src/page/Writer/WritePage/styles/WritePageStyles';

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

describe('WritePage novelDesign styles', () => {
  it('uses novelDesign canvas and paper surfaces for editor shell', () => {
    const styles = createWritePageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.editor.backgroundColor).toBe('#FFFDFC');
    expect(styles.welcomePanel.backgroundColor).toBe('#F3ECE3');
    expect(styles.selectionToolbarContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.modalContainer.backgroundColor).toBe('#FFFDFC');
  });
});
