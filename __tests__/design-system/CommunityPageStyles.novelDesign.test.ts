import { createCommunityPageStyles } from '../../src/page/BookshelfPage/pages/Community/styles/CommunityPageStyles';

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

describe('CommunityPage novelDesign styles', () => {
  it('uses novelDesign shell surfaces for top chrome and post feed', () => {
    const styles = createCommunityPageStyles(lightColors as any);

    expect(styles.container.backgroundColor).toBe('#FAF6F0');
    expect(styles.topBar.backgroundColor).toBe('#FFFDFC');
    expect(styles.tabContainer.backgroundColor).toBe('#FFFDFC');
    expect(styles.postList.backgroundColor).toBe('#FAF6F0');
    expect(styles.postItem.backgroundColor).toBe('#FFFDFC');
    expect(styles.topDivider.backgroundColor).toBe('#F3ECE3');
    expect(styles.subscribeButton.backgroundColor).toBe('#C96A34');
    expect(styles.subscribeButtonText.color).toBe('#FFFDFC');
    expect(styles.bottomDivider.backgroundColor).toBe('#E8DDD1');
    expect(styles.userAvatar.backgroundColor).toBe('#F3ECE3');
    expect(styles.hotCommentTitle.color).toBe('#201A17');
    expect(styles.sourceText.color).toBe('#6F6258');
    expect(styles.userName.color).toBe('#201A17');
    expect(styles.postImage.backgroundColor).toBe('#F3ECE3');
    expect(styles.actionText.color).toBe('#6F6258');
    expect(styles.likedText.color).toBe('#C96A34');
    expect(styles.loadingText.color).toBe('#6F6258');
    expect(styles.filterItem.backgroundColor).toBe('#FFFDFC');
    expect(styles.activeFilterItem.backgroundColor).toBe('#F3ECE3');
    expect(styles.filterText.color).toBe('#6F6258');
    expect(styles.activeFilterText.color).toBe('#C96A34');
    expect(styles.activeTabItem.backgroundColor).toBe('#F3ECE3');
    expect(styles.activeTabText.color).toBe('#C96A34');
    expect(styles.emptyButton.backgroundColor).toBe('#C96A34');
    expect(styles.emptyButtonText.color).toBe('#FFFDFC');
  });
});
