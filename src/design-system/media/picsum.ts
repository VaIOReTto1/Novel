export interface PicsumUrlOptions {
  width: number;
  height: number;
  seed: string;
  grayscale?: boolean;
  blur?: number;
  attempt?: number;
}

export const buildPicsumUrl = ({
  width,
  height,
  seed,
  grayscale = false,
  blur = 0,
  attempt = 0,
}: PicsumUrlOptions): string => {
  const normalizedSeed = encodeURIComponent(seed);
  const params = new URLSearchParams();

  if (grayscale) {
    params.set('grayscale', '1');
  }
  if (blur > 0) {
    params.set('blur', String(blur));
  }
  params.set('retry', String(attempt));

  return `https://picsum.photos/seed/${normalizedSeed}/${width}/${height}?${params.toString()}`;
};
