package com.novel.utils.Store.NovelKeyChain

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Stable
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

enum class NovelKeyChainType(val key: String) {
    TOKEN("com.atcumt.kxq.token"),
    REFRESH_TOKEN("com.atcumt.kxq.refreshToken"),
}

interface NovelKeyChain {
    fun saveToken(accessToken: String?, refreshToken: String?)
    fun save(key: NovelKeyChainType, value: String)
    fun read(key: NovelKeyChainType): String?
    fun delete(key: NovelKeyChainType)
}

@Singleton
@Stable
class EncryptedNovelKeyChain @Inject constructor(
    @ApplicationContext private val context: Context,
) : NovelKeyChain {

    companion object {
        private const val TAG = "EncryptedNovelKeyChain"
        private const val PREFS_NAME = "Novel_keystore_prefs"
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun saveToken(accessToken: String?, refreshToken: String?) {
        accessToken?.let { save(NovelKeyChainType.TOKEN, it) }
        refreshToken?.let { save(NovelKeyChainType.REFRESH_TOKEN, it) }
    }

    override fun save(key: NovelKeyChainType, value: String) {
        runCatching {
            prefs.edit { putString(key.key, value) }
        }.onFailure { error ->
            Timber.e(error, "Failed to save key %s", key.key)
        }
    }

    override fun read(key: NovelKeyChainType): String? {
        return runCatching {
            prefs.getString(key.key, null)
        }.getOrElse { error ->
            Timber.e(error, "Failed to read key %s", key.key)
            null
        }
    }

    override fun delete(key: NovelKeyChainType) {
        runCatching {
            prefs.edit { remove(key.key) }
        }.onFailure { error ->
            Timber.e(error, "Failed to delete key %s", key.key)
        }
    }
}

@Stable
interface TokenProvider {
    fun getToken(): String?
}

@Singleton
class KeyChainTokenProvider @Inject constructor(
    private val novelKeyChain: NovelKeyChain,
) : TokenProvider {

    override fun getToken(): String? = novelKeyChain.read(NovelKeyChainType.TOKEN)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NovelKeyChainModule {

    @Binds
    abstract fun bindNovelKeyChain(
        impl: EncryptedNovelKeyChain,
    ): NovelKeyChain

    @Binds
    abstract fun bindTokenProvider(
        impl: KeyChainTokenProvider,
    ): TokenProvider
}
