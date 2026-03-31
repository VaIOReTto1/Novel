export const shouldRenderStage7Showcase = (search: string): boolean => {
  return new URLSearchParams(search).get('stage7Showcase') === '1';
};
