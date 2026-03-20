const loadCommentStore = () => {
  jest.resetModules();
  return require('../../src/page/comment/CommentPage/store/commentStore') as typeof import('../../src/page/comment/CommentPage/store/commentStore');
};

describe('comment store mock closure', () => {
  afterEach(() => {
    delete (global as typeof globalThis & { fetch?: typeof fetch }).fetch;
    jest.restoreAllMocks();
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('returns an empty comment list when the API responds with no comments', async () => {
    const { useCommentStore } = loadCommentStore();

    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      statusText: 'OK',
      json: async () => ({
        code: '200',
        message: 'ok',
        data: {
          commentTotal: 0,
          comments: [],
        },
        ok: true,
      }),
    }) as unknown as typeof fetch;

    await useCommentStore.getState().loadComments('book-1');

    expect(useCommentStore.getState().comments).toEqual([]);
    expect(useCommentStore.getState().hasMore).toBe(false);
    expect(useCommentStore.getState().error).toBeNull();
  });

  it('returns an empty comment list when fetching comments fails', async () => {
    const { useCommentStore } = loadCommentStore();

    jest.spyOn(Math, 'random').mockReturnValue(0.1);
    global.fetch = jest.fn().mockRejectedValue(new Error('network down')) as unknown as typeof fetch;

    await useCommentStore.getState().loadComments('book-1');

    expect(useCommentStore.getState().comments).toEqual([]);
    expect(useCommentStore.getState().hasMore).toBe(false);
    expect(useCommentStore.getState().error).toBeNull();
  });
});
