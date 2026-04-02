export const shouldRenderNovelDesignShowcase = (search: string): boolean => {
  return new URLSearchParams(search).get('novelDesignShowcase') === '1';
};
