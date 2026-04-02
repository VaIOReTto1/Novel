const fs = require('fs');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');

const read = (relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

describe('android novelDesign page adoption', () => {
  test('home and search page surfaces lean on NovelDesignTokens for shell colors', () => {
    const homePage = read('android/app/src/main/java/com/novel/page/home/HomePage.kt');
    const homeTopBar = read('android/app/src/main/java/com/novel/page/home/component/HomeTopBar.kt');
    const searchPage = read('android/app/src/main/java/com/novel/page/search/SearchPage.kt');
    const searchTopBar = read('android/app/src/main/java/com/novel/page/search/component/SearchTopBar.kt');
    const searchResultPage = read('android/app/src/main/java/com/novel/page/search/SearchResultPage.kt');

    expect(homePage).toContain('NovelDesignTokens');
    expect(homeTopBar).toContain('NovelDesignTokens');
    expect(searchPage).toContain('NovelDesignTokens');
    expect(searchTopBar).toContain('NovelDesignTokens');
    expect(searchResultPage).toContain('NovelDesignTokens');
  });

  test('search result chips, items and skeletons stop defaulting to NovelColors shell tokens', () => {
    const filterChip = read('android/app/src/main/java/com/novel/page/search/component/SearchFilterChip.kt');
    const resultItem = read('android/app/src/main/java/com/novel/page/search/component/SearchResultItem.kt');
    const resultSkeleton = read('android/app/src/main/java/com/novel/page/search/skeleton/SearchResultPageSkeleton.kt');

    expect(filterChip).toContain('NovelDesignTokens');
    expect(filterChip).not.toContain('NovelColors.NovelMain');
    expect(resultItem).toContain('NovelDesignTokens');
    expect(resultItem).not.toContain('NovelColors.NovelTextGray');
    expect(resultSkeleton).toContain('NovelDesignTokens');
    expect(resultSkeleton).not.toContain('NovelColors.NovelBackground');
  });
});
