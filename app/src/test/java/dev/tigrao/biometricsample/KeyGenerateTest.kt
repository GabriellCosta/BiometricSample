package dev.tigrao.biometricsample

import org.junit.Test

class KeyGenerateTest {

    @Test
    fun shouldCreateKeyPair() {
        val keyGenerate = KeyGenerate()

        val result = keyGenerate.generateKeyPair()

        result
    }
}
