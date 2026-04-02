import { createBookManageStyles } from '../../src/page/Writer/BookManage/styles/bookManageStyles';

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

describe('BookManage novelDesign styles', () => {
  it('uses shared novelDesign shell surfaces for banner, draft and chapter areas', () => {
    const styles = createBookManageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.header.backgroundColor).toBe('#FFFDFC');
    expect(styles.bannerCard.backgroundColor).toBe('#FFFDFC');
    expect(styles.draft.backgroundColor).toBe('#F3ECE3');
    expect(styles.chapterCard.backgroundColor).toBe('#FFFDFC');
    expect(styles.footer.backgroundColor).toBe('#FFFDFC');
    expect(styles.createBtn.backgroundColor).toBe('#C96A34');
  });
});
