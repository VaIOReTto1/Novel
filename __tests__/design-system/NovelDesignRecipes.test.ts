import {
  createNovelDesignUI,
  createNovelDesignLayoutRecipes,
  createNovelDesignComponentRecipes,
  createNovelDesignSurfaceRecipes,
  createNovelDesignSurfaceSpec,
  createNovelDesignComponentSpec,
} from '../../src/design-system/novelDesign';

const lightColors = {
  novelMain: '#FF995D',
  novelMainLight: '#F86827',
  novelBookBackground: '#E8E3CF',
  novelBackground: '#FFFFFF',
  novelDivider: '#F7F7F8',
  novelSecondaryBackground: '#F7F7F8',
  novelText: '#000000',
  novelTextGray: '#7F7F7F',
  novelLightGray: '#DDDDDD',
  novelChipBackground: '#EBEDF0',
  novelError: '#FF995D',
  outline: '#E0E0E0',
  novelThemeMode: 'light' as const,
};

describe('NovelDesign recipes', () => {
  it('builds shared layout, component and surface recipes from the global UI entry', () => {
    const ui = createNovelDesignUI(lightColors as any);
    const layout = createNovelDesignLayoutRecipes(ui);
    const components = createNovelDesignComponentRecipes(ui);
    const surfaces = createNovelDesignSurfaceRecipes(ui);

    expect(layout.screenShell.backgroundColor).toBe('#FAF6F0');
    expect(layout.topBar.backgroundColor).toBe('#FFFDFC');
    expect(layout.paperCard.borderColor).toBe('#E8DDD1');
    expect(components.quietIconButton.backgroundColor).toBe('#F3ECE3');
    expect(components.primaryActionPill.backgroundColor).toBe('#C96A34');
    expect(surfaces['rn-host-ai-write-assistant-component'].sectionOrder).toEqual(
      expect.arrayContaining(['header', 'conversation', 'suggestions', 'composer']),
    );
    expect(surfaces['rn-nested-bookshelf-community-page'].sectionOrder).toEqual(
      expect.arrayContaining(['topBar', 'circleTabs', 'feedHeading', 'postFeed', 'floatingCompose']),
    );
  });

  it('returns explicit surface and component specs for Stage 7 high-priority flows', () => {
    const ui = createNovelDesignUI(lightColors as any);
    const aiSurface = createNovelDesignSurfaceSpec('rn-host-ai-write-assistant-component', ui);
    const actionBar = createNovelDesignComponentSpec(
      'src/page/Writer/AIWriteAssistant/components/ActionBar.tsx',
      ui,
    );

    expect(aiSurface.layoutBlueprint.primaryRail).toBe('conversation-thread');
    expect(aiSurface.sectionRecipes.composer).toBe('anchored-composer');
    expect(actionBar.recipeBinding).toBe('assistant-response-actions');
    expect(actionBar.slotStructure).toEqual(
      expect.arrayContaining(['disclaimer', 'actionGroup', 'actionPill']),
    );
  });
});
