// A tiny cross-platform storage helper with graceful fallbacks
// Priority: RN AsyncStorage -> web localStorage -> in-memory

type StorageLike = {
	getItem: (k: string) => Promise<string | null>;
	setItem: (k: string, v: string) => Promise<void>;
	removeItem: (k: string) => Promise<void>;
};

function createStorage(): StorageLike {
	try {
		// 优先使用社区维护的 AsyncStorage 包（RN 官方已弃用 RN.AsyncStorage）
		// 动态 require 以兼容 Web/Node 环境
		// eslint-disable-next-line @typescript-eslint/no-var-requires
		const AS: any = require('@react-native-async-storage/async-storage');
		const asyncStorage = AS?.default ?? AS;
		if (asyncStorage && typeof asyncStorage.getItem === 'function') {
			return {
				getItem: (k: string) => asyncStorage.getItem(k),
				setItem: (k: string, v: string) => asyncStorage.setItem(k, v),
				removeItem: (k: string) => asyncStorage.removeItem(k),
			};
		}
	} catch {}

	try {
		// eslint-disable-next-line @typescript-eslint/no-var-requires
		const RN: any = require('react-native');
		const asyncStorage = RN?.AsyncStorage;
		if (asyncStorage && typeof asyncStorage.getItem === 'function') {
			return {
				getItem: (k: string) => asyncStorage.getItem(k),
				setItem: (k: string, v: string) => asyncStorage.setItem(k, v),
				removeItem: (k: string) => asyncStorage.removeItem(k),
			};
		}
	} catch {}

	try {
		const ls = (globalThis as any).localStorage as
			| { getItem: (k: string) => string | null; setItem: (k: string, v: string) => void; removeItem: (k: string) => void }
			| undefined;
		if (ls) {
			return {
				getItem: async (k: string) => ls.getItem(k),
				setItem: async (k: string, v: string) => {
					ls.setItem(k, v);
				},
				removeItem: async (k: string) => {
					ls.removeItem(k);
				},
			};
		}
	} catch {}

	const memory = new Map<string, string>();
	return {
		getItem: async (k: string) => (memory.has(k) ? memory.get(k)! : null),
		setItem: async (k: string, v: string) => {
			memory.set(k, v);
		},
		removeItem: async (k: string) => {
			memory.delete(k);
		},
	};
}

const storage = createStorage();

export const SimpleStorage = {
	getItem: (k: string) => storage.getItem(k),
	setItem: (k: string, v: string) => storage.setItem(k, v),
	removeItem: (k: string) => storage.removeItem(k),
};


