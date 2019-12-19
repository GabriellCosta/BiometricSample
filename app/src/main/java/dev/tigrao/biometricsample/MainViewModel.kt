package dev.tigrao.biometricsample

import android.app.Application
import android.util.Log
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val executor = Executors.newSingleThreadExecutor()

    private val _viewState = MutableLiveData<MainVO>()
    val viewState: LiveData<MainVO> = _viewState

    private val _resultState = MutableLiveData<String>()
    val resultState: LiveData<String> = _resultState

    private val keyStoreWrapper = KeyStoreWrapper()
    private val cipherWrapper = CipherWrapper()
    var encript: String = ""

    private val viewVO = MainVO(false, View.OnClickListener {

        when (val context = it.context) {
            is FragmentActivity -> showPrompt(context)
            else -> throw IllegalArgumentException("ButtonContext is invalid")
        }

    })

    private val biometricManager = BiometricManager.from(application)

    fun fetchButton() {
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "App can authenticate using biometrics.")
                viewVO.buttonEnabled = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(TAG, "No biometric features available on this device.")
                viewVO.buttonEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "Biometric features are currently unavailable.")
                viewVO.buttonEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(
                    TAG, "The user hasn't associated any biometric credentials " +
                        "with their account."
                )
                viewVO.buttonEnabled = false
            }
        }

        _viewState.postValue(viewVO)
    }

    private fun showPrompt(context: FragmentActivity) {

        keyStoreWrapper.createAndroidKeyStoreAsymmetricKey("Biometric")
        val keyPair = keyStoreWrapper.getAndroidKeyStoreAsymmetricKeyPair("Biometric")

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(context, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    _resultState.postValue("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    /* val authenticatedCryptoObject: BiometricPrompt.CryptoObject =
                         result.getCryptoObject()*/
                    // User has verified the signature, cipher, or message
                    // authentication code (MAC) associated with the crypto object,
                    // so you can use it in your app's crypto-driven workflows.

                    result.cryptoObject?.cipher

                    val message = cipherWrapper.decrypt(encript)

                    _resultState.postValue("Authentication success ^$message")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    _resultState.postValue("Authentication failed")
                }
            })



        encript = cipherWrapper.encrypt("leonardo", keyPair.public)

        // Displays the "log in" prompt.
        biometricPrompt.authenticate(
            promptInfo,
            BiometricPrompt.CryptoObject(cipherWrapper.decryptCipher(keyPair.private))
        )
    }
}
