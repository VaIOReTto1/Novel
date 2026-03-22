const fs = require('fs');
const path = require('path');

const themeChangedFixture = require('../fixtures/bridge/theme-changed-event.json');
const selectionMenuFixture = require('../fixtures/bridge/write-page-selection-menu-action.json');

const readProjectFile = (relativePath: string): string => {
  return fs.readFileSync(path.join(process.cwd(), relativePath), 'utf8');
};

describe('Native bridge source contracts', () => {
  it('exports stable bridge module names and core ReactMethods', () => {
    const navigationSource = readProjectFile(
      'android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt',
    );
    const userSource = readProjectFile(
      'android/app/src/main/java/com/novel/rn/bridge/UserBridgeModule.kt',
    );

    expect(navigationSource).toMatch(
      /override fun getName\(\): String = "NavigationBridge"/,
    );
    expect(navigationSource).toMatch(
      /fun navigateToBecomeWriterWithFlag\(isAuthor: Boolean\)/,
    );
    expect(navigationSource).toMatch(/fun getReadingHistory\(promise: Promise\)/);
    expect(navigationSource).toMatch(/fun getAuthorStatus\(promise: Promise\)/);
    expect(navigationSource).toMatch(
      /fun getAuthorBooks\(pageNum: Int, pageSize: Int, promise: Promise\)/,
    );

    expect(userSource).toMatch(/override fun getName\(\): String = "UserBridge"/);
    expect(userSource).toMatch(/fun getCurrentUserData\(promise: Promise\)/);
    expect(userSource).toMatch(/fun isUserLoggedIn\(promise: Promise\)/);
    expect(userSource).toMatch(/fun getUserBalance\(promise: Promise\)/);
  });

  it('keeps ThemeChanged event producer and RN consumers aligned', () => {
    const producerSource = readProjectFile(
      'android/core-ui/src/main/java/com/novel/ui/theme/ThemeManager.kt',
    );
    const themeStoreSource = readProjectFile('src/utils/theme/themeStore.ts');
    const timedSwitchSource = readProjectFile(
      'src/page/SettingsPage/TimeSwitchPage/TimedSwitchPage.tsx',
    );

    expect(producerSource).toMatch(
      new RegExp(`emit\\("${themeChangedFixture.eventName}", params\\)`),
    );
    expect(themeStoreSource).toMatch(
      new RegExp(`addListener\\('${themeChangedFixture.eventName}'`),
    );
    expect(timedSwitchSource).toMatch(
      new RegExp(`addListener\\('${themeChangedFixture.eventName}'`),
    );

    themeChangedFixture.requiredKeys.forEach((key: string) => {
      expect(producerSource).toContain(`"${key}"`);
      expect(themeStoreSource).toContain(key);
    });
    expect(timedSwitchSource).toContain('colorScheme');
  });

  it('keeps WritePageSelectionMenuAction payload keys aligned', () => {
    const producerSource = readProjectFile(
      'android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt',
    );
    const consumerSource = readProjectFile(
      'src/page/Writer/WritePage/WritePage.tsx',
    );

    expect(producerSource).toMatch(
      new RegExp(`emit\\("${selectionMenuFixture.eventName}", map\\)`),
    );
    expect(consumerSource).toMatch(
      new RegExp(`addListener\\?\\.\\('${selectionMenuFixture.eventName}'`),
    );

    selectionMenuFixture.requiredKeys.forEach((key: string) => {
      expect(producerSource).toContain(`"${key}"`);
      expect(consumerSource).toContain(`evt?.${key}`);
    });
    selectionMenuFixture.optionalKeys.forEach((key: string) => {
      expect(producerSource).toContain(`"${key}"`);
      expect(consumerSource).toContain(`evt?.${key}`);
    });
  });
});
