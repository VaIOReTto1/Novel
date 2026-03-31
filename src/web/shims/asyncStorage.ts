const memoryStore = new Map<string, string>();

const getStorage = (): Storage | null => {
  if (typeof window === 'undefined') {
    return null;
  }

  try {
    return window.localStorage;
  } catch (_error) {
    return null;
  }
};

const AsyncStorage = {
  async getItem(key: string): Promise<string | null> {
    const storage = getStorage();
    return storage ? storage.getItem(key) : memoryStore.get(key) ?? null;
  },

  async setItem(key: string, value: string): Promise<void> {
    const storage = getStorage();
    if (storage) {
      storage.setItem(key, value);
      return;
    }
    memoryStore.set(key, value);
  },

  async removeItem(key: string): Promise<void> {
    const storage = getStorage();
    if (storage) {
      storage.removeItem(key);
      return;
    }
    memoryStore.delete(key);
  },

  async mergeItem(key: string, value: string): Promise<void> {
    const currentValue = await AsyncStorage.getItem(key);
    if (!currentValue) {
      await AsyncStorage.setItem(key, value);
      return;
    }

    try {
      const merged = {
        ...JSON.parse(currentValue),
        ...JSON.parse(value),
      };
      await AsyncStorage.setItem(key, JSON.stringify(merged));
    } catch (_error) {
      await AsyncStorage.setItem(key, value);
    }
  },
};

export default AsyncStorage;
