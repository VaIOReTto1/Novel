import { fp, sp, wp } from '../../utils/theme/dimensions';
import { NovelColors } from '../../utils/theme/colors';
import { resolveNovelDesignTheme } from '../tokens/resolveNovelDesignTheme';
import { novelDesignDarkTheme, novelDesignLightTheme } from '../tokens/novelDesignTokens';

export type NovelDesignTheme = typeof novelDesignLightTheme;
export type NovelDesignMode = 'light' | 'dark';
export type NovelDesignSpaceToken = keyof NovelDesignTheme['space'];
export type NovelDesignRadiusToken = keyof NovelDesignTheme['radius'];

const normalizeHex = (value: string) => {
  const cleaned = value.replace('#', '');

  if (cleaned.length === 3) {
    return cleaned
      .split('')
      .map((token) => `${token}${token}`)
      .join('');
  }

  return cleaned.padEnd(6, '0').slice(0, 6);
};

const withAlpha = (value: string, opacity: number) => {
  const normalized = normalizeHex(value);
  const clampedOpacity = Math.max(0, Math.min(1, opacity));
  const alpha = Math.round(clampedOpacity * 255)
    .toString(16)
    .padStart(2, '0');

  return `#${normalized}${alpha}`;
};

export class NovelDesignUI {
  constructor(
    public readonly tokens: NovelDesignTheme,
    public readonly mode: NovelDesignMode,
  ) {}

  get color() {
    return this.tokens.color;
  }

  get space() {
    return this.tokens.space;
  }

  get radius() {
    return this.tokens.radius;
  }

  get elevation() {
    return this.tokens.elevation;
  }

  get motion() {
    return this.tokens.motion;
  }

  get typography() {
    return this.tokens.typography;
  }

  spacePx(token: NovelDesignSpaceToken) {
    return wp(this.space[token]);
  }

  radiusPx(token: NovelDesignRadiusToken) {
    return sp(this.radius[token]);
  }

  fontPx(size: number) {
    return fp(size);
  }

  alpha(color: string, opacity: number) {
    return withAlpha(color, opacity);
  }

  get metrics() {
    return {
      pageGutter: this.spacePx('200'),
      sectionGap: this.spacePx('200'),
      blockGap: this.spacePx('150'),
      compactGap: this.spacePx('100'),
      microGap: this.spacePx('050'),
      cardPadding: this.spacePx('200'),
      compactCardPadding: this.spacePx('150'),
      topBarHeight: wp(56),
      iconSizeSm: sp(16),
      iconSizeMd: sp(20),
      iconSizeLg: sp(24),
      iconSizeXl: sp(28),
      buttonHeightMd: wp(36),
      buttonHeightLg: wp(44),
      avatarMd: sp(40),
      writerCover: {
        width: wp(100),
        height: wp(130),
      },
      floatingComposerOffset: wp(75),
      inputMinHeight: wp(30),
      inputMaxHeight: wp(100),
      cardRadius: this.radiusPx('lg'),
      panelRadius: this.radiusPx('xl'),
      chipRadius: this.radiusPx('full'),
    };
  }

  get presets() {
    return {
      canvas: {
        backgroundColor: this.color.bg.canvas,
      },
      paperCard: {
        backgroundColor: this.color.bg.surface,
        borderWidth: 1,
        borderColor: this.color.border.subtle,
        borderRadius: this.metrics.cardRadius,
      },
      elevatedCard: {
        backgroundColor: this.color.bg.elevated,
        borderWidth: 1,
        borderColor: this.color.border.subtle,
        borderRadius: this.metrics.cardRadius,
      },
      topBar: {
        backgroundColor: this.color.bg.surface,
        borderBottomWidth: 1,
        borderColor: this.color.border.subtle,
        minHeight: this.metrics.topBarHeight,
        paddingHorizontal: this.metrics.pageGutter,
        paddingVertical: this.spacePx('150'),
      },
      inputField: {
        backgroundColor: this.color.bg.elevated,
        borderWidth: 1,
        borderColor: this.color.border.subtle,
        borderRadius: this.radiusPx('md'),
      },
      primaryButton: {
        backgroundColor: this.color.brand.primary,
        borderRadius: this.metrics.chipRadius,
      },
      subtlePill: {
        backgroundColor: this.color.bg.surface,
        borderWidth: 1,
        borderColor: this.color.border.subtle,
        borderRadius: this.metrics.chipRadius,
      },
    };
  }
}

export const createNovelDesignUI = (colors: NovelColors) => {
  const tokens = resolveNovelDesignTheme(colors.novelBackground);
  const mode: NovelDesignMode =
    tokens === novelDesignDarkTheme ? 'dark' : 'light';

  return new NovelDesignUI(tokens, mode);
};
