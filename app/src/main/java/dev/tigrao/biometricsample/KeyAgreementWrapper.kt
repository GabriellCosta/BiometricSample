package dev.tigrao.biometricsample

import android.security.keystore.KeyProperties
import java.security.Key
import javax.crypto.KeyAgreement

class KeyAgreementWrapper {

    fun doExchange(privateKey: Key, publicKey: Key): Key {

        val keyAgreement =
            KeyAgreement.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")

        keyAgreement.init(privateKey)

        keyAgreement.doPhase(publicKey, true)

        return keyAgreement.generateSecret(KeyProperties.KEY_ALGORITHM_RSA)
    }
}
