package dev.tigrao.biometricsample

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher

private const val TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding"

internal class CipherWrapper {

    private val cipher: Cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC)

    fun encrypt(data: String, key: Key?): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val bytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    fun decryptCipher(key: Key?) =
        cipher.also {
            cipher.init(Cipher.DECRYPT_MODE, key)
        }
}
