package dev.tigrao.biometricsample

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey

private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val KEY_SIZE = 2048

@RequiresApi(Build.VERSION_CODES.M)
internal class KeyStoreWrapper {

    private val keyStore by lazy { createAndroidKeyStore() }

    fun createAndroidKeyStoreAsymmetricKey(alias: String): KeyPair {
        val generator =
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE)

        initGeneratorWithKeyGenParameterSpec(generator, alias)

        return generator.generateKeyPair()
    }

    fun getAndroidKeyStoreAsymmetricKeyPair(alias: String): KeyPair {
        val privateKey = keyStore.getKey(alias, null) as PrivateKey?
        val publicKey = keyStore.getCertificate(alias)?.publicKey

        return if (privateKey != null && publicKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            throw IllegalStateException("Keys not created before use")
        }
    }

    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE)
            .setUserAuthenticationRequired(true)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)

        generator.initialize(builder.build())
    }

    private fun createAndroidKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        return keyStore
    }
}
