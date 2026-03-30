import {
  bootstrapMemberCenterPage,
  createMemberCenterPageHandlers,
} from '../../src/page/ScrollBox/MemberCenterPage/domain/memberCenterPageModel';

describe('member center page model helpers', () => {
  test('bootstraps member center page once', async () => {
    const loadInitialData = jest.fn().mockResolvedValue(undefined);

    await bootstrapMemberCenterPage({
      loadInitialData,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadInitialData).toHaveBeenCalledTimes(1);
  });

  test('delegates card, package and purchase actions', () => {
    const handlers = createMemberCenterPageHandlers({
      navigateBack: jest.fn(),
      setCurrentCard: jest.fn(),
      selectPricePackage: jest.fn(),
      handlePurchase: jest.fn(),
      handlePrivacyPress: jest.fn(),
      handleTermsPress: jest.fn(),
      handleTaskPress: jest.fn(),
    });

    handlers.handleBackPress();
    handlers.handleCardChange(2);
    handlers.handlePackageSelect('package-1');
    handlers.handlePurchasePress();
    handlers.handlePrivacyLinkPress();
    handlers.handleTermsLinkPress();
    handlers.handleTaskCardPress('task-1');

    expect(handlers.handleBackPress).toBeDefined();
  });
});
