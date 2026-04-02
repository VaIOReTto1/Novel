import { createAIStyles } from '../../src/page/Writer/AIWriteAssistant/styles/aiStyles';

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

describe('AIWriteAssistant novelDesign styles', () => {
  it('uses shared novelDesign shell surfaces for the writer assistant flow', () => {
    const styles = createAIStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.header.backgroundColor).toBe('#FFFDFC');
    expect(styles.introBubble.backgroundColor).toBe('#F3ECE3');
    expect(styles.bubbleAssistant.backgroundColor).toBe('#FFFDFC');
    expect(styles.thinkingContainer.backgroundColor).toBe('#F3ECE3');
    expect(styles.inputBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.input.backgroundColor).toBe('#F3ECE3');
    expect(styles.ideaFloatPanel.backgroundColor).toBe('#FFFDFC');
  });
});
