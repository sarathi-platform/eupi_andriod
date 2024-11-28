package com.nudge.core.utils

import android.util.Base64
import com.nudge.core.AES_SALT
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object AESHelper {
    fun generateSecretKey(secretKeyPass: String): SecretKeySpec {
        /*** The customSecretKey can be 16, 24 or 32 depending on algorithm you are using* i.e AES-128, AES-192 or AES-256 ***/
        val secretKey = SecretKeySpec("$secretKeyPass$AES_SALT".toByteArray(), "AES")
        return secretKey
    }

    fun encrypt(
        textToEncrypt: String,
        secretKeyPass: String,
    ): String {
        val plainText = textToEncrypt.toByteArray()

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(secretKeyPass))

        val encrypt = cipher.doFinal(plainText)
        return Base64.encodeToString(encrypt, Base64.DEFAULT)
    }

    fun decrypt(
        encryptedText: String,
        secretKeyPass: String

        ): String {
        val textToDecrypt = Base64.decode(encryptedText, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES")

        cipher.init(Cipher.DECRYPT_MODE, generateSecretKey(secretKeyPass))

        val decrypt = cipher.doFinal(textToDecrypt)
        return String(decrypt)
    }

}