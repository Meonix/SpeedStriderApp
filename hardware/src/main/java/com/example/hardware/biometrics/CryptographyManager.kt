package com.example.hardware.biometrics

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.gson.Gson
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Handles encryption and decryption
 */
interface CryptographyManager {

    fun getInitializedCipherForEncryption(keyName: String): Cipher

    fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher

    /**
     * The Cipher created with [getInitializedCipherForEncryption] is used here
     */
    fun encryptData(plaintext: String, cipher: Cipher): CiphertextWrapper

    /**
     * The Cipher created with [getInitializedCipherForDecryption] is used here
     */
    fun decryptData(ciphertext: ByteArray, cipher: Cipher): String

    fun persistCiphertextWrapperToSharedPrefs(
        ciphertextWrapper: CiphertextWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    )

    fun getCiphertextWrapperFromSharedPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): CiphertextWrapper?
}

fun CryptographyManager(): CryptographyManager = CryptographyManagerImpl()

/**
 * To get an instance of this private com.example.hardware.biometrics.CryptographyManagerImpl class, use the top-level function
 * fun com.example.hardware.biometrics.CryptographyManager(): com.example.hardware.biometrics.CryptographyManager = com.example.hardware.biometrics.CryptographyManagerImpl()
 */
private class CryptographyManagerImpl : CryptographyManager {

    private val KEY_SIZE = 256
    private val ANDROID_KEYSTORE = "AndroidKeyStore"

    private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

    override fun getInitializedCipherForEncryption(keyName: String): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }

    override fun getInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray
    ): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, initializationVector))
        return cipher
    }

    override fun encryptData(plaintext: String, cipher: Cipher): CiphertextWrapper {
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return CiphertextWrapper(ciphertext, cipher.iv)
    }

    override fun decryptData(ciphertext: ByteArray, cipher: Cipher): String {
        val data = try {
            val plaintext = cipher.doFinal(ciphertext)
            String(plaintext, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            ""
        }
        return data
    }

    private fun getCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        //--If Secretkey was previously created for that keyName, then grab and return it.
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        keyStore.getKey(keyName, null)?.let {
            return it as SecretKey
        }

        //--If you reach here, then a new SecretKey must be generated for that keyName
        val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val paramsBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyGenParameterSpec.Builder(keyName, keyProperties)
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        paramsBuilder.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setBlockModes(ENCRYPTION_BLOCK_MODE)
                setEncryptionPaddings(ENCRYPTION_PADDING)
                setKeySize(KEY_SIZE)
//                setUserAuthenticationParameters(600,KeyProperties.AUTH_DEVICE_CREDENTIAL)
                setUserAuthenticationRequired(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setInvalidatedByBiometricEnrollment(true)
                }
            }
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)

        return keyGenerator.generateKey()
    }

    override fun persistCiphertextWrapperToSharedPrefs(
        ciphertextWrapper: CiphertextWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ) {
        val json = Gson().toJson(ciphertextWrapper)
        context.getSharedPreferences(filename, mode).edit().putString(prefKey, json).apply()
    }

    override fun getCiphertextWrapperFromSharedPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): CiphertextWrapper? {
        val json = context.getSharedPreferences(filename, mode).getString(prefKey, null)
        return Gson().fromJson(json, CiphertextWrapper::class.java)
    }
}

data class CiphertextWrapper(val ciphertext: ByteArray, val initializationVector: ByteArray)
