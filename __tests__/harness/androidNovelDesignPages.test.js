const fs = require('fs');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');

const read = (relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

describe('android novelDesign page adoption', () => {
  test('home and search page surfaces lean on NovelDesignTokens for shell colors', () => {
    const homePage = read('android/app/src/main/java/com/novel/page/home/HomePage.kt');
    const homeTopBar = read('android/app/src/main/java/com/novel/page/home/component/HomeTopBar.kt');
    const homeFilterBar = read('android/app/src/main/java/com/novel/page/home/component/HomeFilterBar.kt');
    const homeRankPanel = read('android/app/src/main/java/com/novel/page/home/component/HomeRankPanel.kt');
    const homeSkeleton = read('android/app/src/main/java/com/novel/page/home/skeleton/HomePageSkeleton.kt');
    const searchPage = read('android/app/src/main/java/com/novel/page/search/SearchPage.kt');
    const searchTopBar = read('android/app/src/main/java/com/novel/page/search/component/SearchTopBar.kt');
    const searchResultPage = read('android/app/src/main/java/com/novel/page/search/SearchResultPage.kt');
    const fullRankingPage = read('android/app/src/main/java/com/novel/page/search/FullRankingPage.kt');
    const fullRankingSkeleton = read('android/app/src/main/java/com/novel/page/search/skeleton/FullRankingPageSkeleton.kt');

    expect(homePage).toContain('NovelDesignTokens');
    expect(homeTopBar).toContain('NovelDesignTokens');
    expect(homeFilterBar).toContain('NovelDesignTokens');
    expect(homeRankPanel).toContain('NovelDesignTokens');
    expect(homeSkeleton).toContain('NovelDesignTokens');
    expect(searchPage).toContain('NovelDesignTokens');
    expect(searchTopBar).toContain('NovelDesignTokens');
    expect(searchResultPage).toContain('NovelDesignTokens');
    expect(fullRankingPage).toContain('NovelDesignTokens');
    expect(fullRankingSkeleton).toContain('NovelDesignTokens');
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

  test('main shell host and RN host content start using NovelDesignTokens instead of raw NovelColors shell fills', () => {
    const mainPage = read('android/app/src/main/java/com/novel/page/MainPage.kt');
    const mainPageHost = read('android/app/src/main/java/com/novel/page/MainPageHostComponents.kt');
    const rnHostContent = read('android/feature-rn-host/src/main/java/com/novel/rn/ReactNativePageContent.kt');

    expect(mainPage).toContain('NovelDesignTokens');
    expect(mainPageHost).toContain('NovelDesignTokens');
    expect(mainPageHost).not.toContain('NovelColors.NovelBackground');
    expect(rnHostContent).toContain('NovelDesignTokens');
    expect(rnHostContent).not.toContain('NovelColors.NovelBackground');
  });
});
