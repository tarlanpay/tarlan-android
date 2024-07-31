package kz.tarlanpayments.storage.androidsdk.noui.data

import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


internal object RSAEncryption {
    fun loadPublicKeyAndEncryptData(data: String, pemPublicKey: String): String {
        val publicKey = loadPublicKey(pemPublicKey)
        return encryptData(data, publicKey)
    }

    private fun loadPublicKey(pemPublicKey: String): PublicKey {
        val publicKeyPEM = pemPublicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        Log.d("RSAEncryption", "Public key: $pemPublicKey")

        val decodedKey = Base64.decode(publicKeyPEM, Base64.NO_WRAP)
        val keySpec = X509EncodedKeySpec(decodedKey)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    private fun encryptData(data: String, publicKey: PublicKey): String {
        Log.d("RSAEncryption", "Data to encrypt: $data")
        val cipher = Cipher.getInstance(
            "${KeyProperties.KEY_ALGORITHM_RSA}/${KeyProperties.BLOCK_MODE_ECB}/${KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1}"
        )
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val result = Base64.encodeToString(encryptedData, Base64.NO_WRAP)
        Log.d("RSAEncryption", "Encrypted data: $result")
        return result
    }
}