import React, { useEffect } from 'react';
import { View, ScrollView } from 'react-native';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const RN: any = require('react-native');
const { BackHandler } = RN;
import { useNovelColors } from '../../../utils/theme/colors';
import { createBookManageStyles } from './styles/bookManageStyles';
import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { useBookManageStore } from './store/bookManageStore';
import { Header } from './components/Header';
import { Banner } from './components/Banner';
import { DraftBar } from './components/DraftBar';
import { EmptyChapter } from './components/EmptyChapter';
import { ChapterSection } from './components/ChapterSection';
import { Footer } from './components/Footer';

const BookManagePage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);
  const { book, draft, chapters, load } = useBookManageStore();

  useEffect(() => {
    const sub = BackHandler.addEventListener('hardwareBackPress', () => {
      NavigationBridge.navigateBack?.('BookManagePageComponent');
      return true;
    });
    load();
    return () => sub.remove();
  }, [load]);

  return (
    <View style={styles.container}>
      <Header />
      <Banner book={book} />
      <DraftBar draft={draft} onContinue={() => NavigationBridge.navigateToWritePage()} />
      <ChapterSection chapters={chapters} />
      <View style={styles.volume}><RN.Text style={styles.volumeText}>第一卷：默认</RN.Text></View>
      <ScrollView contentContainerStyle={styles.emptyWrap}>
        {chapters.length === 0 ? <EmptyChapter /> : null}
      </ScrollView>
      <Footer />
    </View>
  );
};

export default BookManagePage;


