import React, { useEffect } from 'react';
import { ScrollView, View } from 'react-native';

const RN: any = require('react-native');

import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import { useNovelColors } from '../../../utils/theme/colors';
import {
  bootstrapBookManagePage,
  createBookManagePageHandlers,
} from './domain/bookManagePageModel';
import { Banner } from './components/Banner';
import { ChapterSection } from './components/ChapterSection';
import { DraftBar } from './components/DraftBar';
import { EmptyChapter } from './components/EmptyChapter';
import { Footer } from './components/Footer';
import { Header } from './components/Header';
import { useBookManageStore } from './store/bookManageStore';
import { createBookManageStyles } from './styles/bookManageStyles';

const BookManagePage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);
  const { book, draft, chapters, load } = useBookManageStore();
  const handlers = React.useMemo(
    () =>
      createBookManagePageHandlers({
        navigateBack: () => NavigationBridge.navigateBack?.('BookManagePageComponent'),
        navigateToWritePage: () => NavigationBridge.navigateToWritePage(),
        navigateToBookManage: () => NavigationBridge.navigateToBookManage?.(),
      }),
    [],
  );

  useEffect(() => {
    const cleanupBackHandler = registerHardwareBackHandler(() => {
      handlers.handleBack();
      return true;
    });
    bootstrapBookManagePage({
      load,
    });
    return cleanupBackHandler;
  }, [handlers, load]);

  return (
    <View style={styles.container}>
      <Header />
      <ScrollView contentContainerStyle={styles.contentScroll}>
        <Banner book={book} />
        <DraftBar draft={draft} onContinue={handlers.handleContinueDraft} />
        <ChapterSection chapters={chapters} />
        <View style={styles.volume}>
          <RN.Text style={styles.volumeText}>第一卷：默认卷</RN.Text>
        </View>
        {chapters.length === 0 ? (
          <View style={styles.emptyWrap}>
            <EmptyChapter />
          </View>
        ) : null}
      </ScrollView>
      <Footer />
    </View>
  );
};

export default BookManagePage;
