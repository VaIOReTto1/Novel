const pageStateCache = new Map<string, unknown>();

export const savePageState = (pageId: string, state: unknown): void => {
  console.log(`[PageStateCache] Saving state for page: ${pageId}`);
  pageStateCache.set(pageId, state);
};

export const getPageState = <T = unknown>(pageId: string): T | undefined => {
  const state = pageStateCache.get(pageId) as T | undefined;
  console.log(`[PageStateCache] Restoring state for page: ${pageId}`, state);
  return state;
};

export const clearPageState = (pageId: string): void => {
  pageStateCache.delete(pageId);
  console.log(`[PageStateCache] Cleared state for page: ${pageId}`);
};

export const clearAllPageState = (): void => {
  pageStateCache.clear();
  console.log('[PageStateCache] Cleared all page state');
};
