package sonnen.utils

import java.security.spec.X509EncodedKeySpec
import java.security._
import java.util.Base64

/**
  * A utility class for performing the low-level cryptographic operations
  */
object CryptoUtils {

  private val SignatureImplementation = "SHA256withRSA"
  private val KeyPairImplementation = "RSA"
  private val KeySize = 1024

  /**
    * Signs data
    * @param bytes the array of bytes to sign
    * @param privateKey the RSA private key to use for signing
    * @return a base64-encoded signature
    */
  def sign(bytes: Array[Byte], privateKey: PrivateKey): String = {
    val signer = Signature.getInstance(SignatureImplementation)
    signer.initSign(privateKey)
    signer.update(bytes)

    val signature = signer.sign()
    toBase64(signature)
  }

  /**
    * Verifies the signature on the given data
    * @param bytes tha data on which the signature should be verified
    * @param base64PublicKey the base64-encoded public key of the signer
    * @param base64Signature the base64-encoded signature
    * @return true if the signature is valid, false otherwise
    */
  def verifySignature(bytes: Array[Byte], base64PublicKey: String, base64Signature: String): Boolean = {

    val publicKey = toPublicKey(base64PublicKey)

    val sig = Signature.getInstance(SignatureImplementation)
    sig.initVerify(publicKey)
    sig.update(bytes)

    sig.verify(toByteArray(base64Signature))
  }

  /**
    * Encodes a public key to base-64
    */
  def toBase64(publicKey: PublicKey): String =
    toBase64(publicKey.getEncoded)

  /**
    * @return A 1024-bit RSA key pair
    */
  def generateKeyPair(): KeyPair = {
    val kpg = KeyPairGenerator.getInstance(KeyPairImplementation)
    kpg.initialize(KeySize)
    kpg.genKeyPair()
  }

  private def toBase64(bytes: Array[Byte]): String =
    Base64.getEncoder.encodeToString(bytes)

  private def toPublicKey(base64PublicKey: String): PublicKey = {
    val publicKeyBytes = toByteArray(base64PublicKey)
    KeyFactory.getInstance(KeyPairImplementation).generatePublic(new X509EncodedKeySpec(publicKeyBytes))
  }

  private def toByteArray(base64String: String): Array[Byte] =
    Base64.getDecoder.decode(base64String)
}
