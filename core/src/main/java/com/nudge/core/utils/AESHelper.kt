package com.nudge.core.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class AESHelper {
    fun generateSecretKey(): SecretKeySpec {
        /*** The customSecretKey can be 16, 24 or 32 depending on algorithm you are using* i.e AES-128, AES-192 or AES-256 ***/
        val secretKey = SecretKeySpec("".toByteArray(), "AES")
        return secretKey
    }

    fun encrypt(
        textToEncrypt: String,
    ): String {
        val plainText = textToEncrypt.toByteArray()

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey())

        val encrypt = cipher.doFinal(plainText)
        return Base64.encodeToString(encrypt, Base64.DEFAULT)
    }

    fun decrypt(
        encryptedText: String,

        ): String {
        val textToDecrypt = Base64.decode(encryptedText, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES")

        cipher.init(Cipher.DECRYPT_MODE, generateSecretKey())

        val decrypt = cipher.doFinal(textToDecrypt)
        return String(decrypt)
    }

}