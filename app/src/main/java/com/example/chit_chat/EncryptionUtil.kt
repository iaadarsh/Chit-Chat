package com.example.chit_chat

import android.util.Base64
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object EncryptionUtil {

    private const val RSA_ALGORITHM = "RSA"
    private const val AES_TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding"

    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM)
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }

    fun encryptHybrid(message: String, publicKeyString: String): String {
        // 1. Generate AES key
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128)
        val aesKey = keyGen.generateKey()
        
        // 2. Encrypt message with AES
        val aesCipher = Cipher.getInstance(AES_TRANSFORMATION)
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encryptedMessageBytes = aesCipher.doFinal(message.toByteArray())
        val encryptedMessageBase64 = Base64.encodeToString(encryptedMessageBytes, Base64.NO_WRAP)
        
        // 3. Encrypt AES key with RSA
        val rsaPublicKey = getPublicKeyFromString(publicKeyString)
        val rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION)
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        val encryptedAESKeyBytes = rsaCipher.doFinal(aesKey.encoded)
        val encryptedAESKeyBase64 = Base64.encodeToString(encryptedAESKeyBytes, Base64.NO_WRAP)
        
        return "$encryptedAESKeyBase64:$encryptedMessageBase64"
    }

    fun decryptHybrid(combinedMessage: String, privateKeyString: String): String {
        try {
            val parts = combinedMessage.split(":")
            if (parts.size != 2) return combinedMessage
            
            val encryptedAESKeyBase64 = parts[0]
            val encryptedMessageBase64 = parts[1]
            
            // 1. Decrypt AES key with RSA
            val rsaPrivateKey = getPrivateKeyFromString(privateKeyString)
            val rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION)
            rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey)
            val aesKeyBytes = rsaCipher.doFinal(Base64.decode(encryptedAESKeyBase64, Base64.NO_WRAP))
            val aesKey = SecretKeySpec(aesKeyBytes, "AES")
            
            // 2. Decrypt message with AES
            val aesCipher = Cipher.getInstance(AES_TRANSFORMATION)
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey)
            val decryptedMessageBytes = aesCipher.doFinal(Base64.decode(encryptedMessageBase64, Base64.NO_WRAP))
            
            return String(decryptedMessageBytes)
        } catch (e: Exception) {
            return "Decryption Error"
        }
    }

    fun keyToString(key: Key): String {
        return Base64.encodeToString(key.encoded, Base64.NO_WRAP)
    }

    private fun getPublicKeyFromString(publicKeyString: String?): PublicKey? {
        if (publicKeyString == null) return null
        val keyBytes = Base64.decode(publicKeyString, Base64.NO_WRAP)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        return keyFactory.generatePublic(spec)
    }

    private fun getPrivateKeyFromString(privateKeyString: String): PrivateKey {
        val keyBytes = Base64.decode(privateKeyString, Base64.NO_WRAP)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        return keyFactory.generatePrivate(spec)
    }
}
