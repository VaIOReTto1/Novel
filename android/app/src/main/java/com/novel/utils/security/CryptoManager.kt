package com.novel.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.novel.utils.TimberLogger
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 加密管理器
 * 
 * 核心功能：
 * - 使用Android KeyStore进行安全的数据加密解密
 * - 自动生成和管理加密密钥
 * - 支持AES-GCM加密算法
 * - 提供Base64编码的加密结果
 * 
 * 安全特性：
 * - 密钥存储在硬件安全模块中
 * - 使用认证加密模式（GCM）
 * - 每次加密生成随机IV
 * - 256位密钥长度
 */
@Singleton
class CryptoManager @Inject constructor() {
    
    companion object {
        private const val TAG = "CryptoManager"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "NovelAppSecretKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val TAG_SIZE = 128
    }
    
    init {
        TimberLogger.d(TAG, "初始化CryptoManager")
        generateKey()
    }
    
    /**
     * 生成或获取加密密钥
     * 
     * 如果密钥已存在则跳过生成
     * 使用Android KeyStore确保密钥安全性
     */
    private fun generateKey() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                TimberLogger.d(TAG, "生成新的加密密钥")
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(false) // 不需要用户认证
                    .build()
                
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
                TimberLogger.d(TAG, "加密密钥生成成功")
            } else {
                TimberLogger.d(TAG, "使用现有加密密钥")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "密钥生成失败", e)
            throw e
        }
    }
    
    /**
     * 加密数据
     * 
     * @param plainText 明文字符串
     * @return Base64编码的密文（包含IV和认证标签）
     * @throws Exception 加密过程中的异常
     */
    fun encrypt(plainText: String): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            
            // 将IV和密文组合
            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
            
            val result = Base64.encodeToString(combined, Base64.NO_WRAP)
            TimberLogger.d(TAG, "数据加密成功，长度: ${plainText.length} -> ${result.length}")
            result
        } catch (e: Exception) {
            TimberLogger.e(TAG, "数据加密失败", e)
            throw e
        }
    }
    
    /**
     * 解密数据
     * 
     * @param encryptedText Base64编码的密文（包含IV和认证标签）
     * @return 解密后的明文字符串
     * @throws Exception 解密过程中的异常
     */
    fun decrypt(encryptedText: String): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val combined = Base64.decode(encryptedText, Base64.NO_WRAP)
            
            // 提取IV和密文
            val iv = combined.sliceArray(0 until IV_SIZE)
            val cipherText = combined.sliceArray(IV_SIZE until combined.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val plainText = cipher.doFinal(cipherText)
            val result = String(plainText, Charsets.UTF_8)
            TimberLogger.d(TAG, "数据解密成功，长度: ${encryptedText.length} -> ${result.length}")
            result
        } catch (e: Exception) {
            TimberLogger.e(TAG, "数据解密失败", e)
            throw e
        }
    }
} 
