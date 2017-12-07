package sonnen.utils

import java.security.spec.X509EncodedKeySpec
import java.security._
import java.util.Base64


object CryptoUtils {

  private val SignatureImplementation = "SHA256withRSA"
  private val KeyPairImplementation = "RSA"
  private val KeySize = 1024

  def sign(bytes: Array[Byte], privateKey: PrivateKey): String = {
    val signer = Signature.getInstance(SignatureImplementation)
    signer.initSign(privateKey)
    signer.update(bytes)

    val signature = signer.sign()
    toBase64(signature)
  }

  def verifySignature(bytes: Array[Byte], base64PublicKey: String, base64Signature: String): Boolean = {

    val publicKey = toPublicKey(base64PublicKey)

    val sig = Signature.getInstance(SignatureImplementation)
    sig.initVerify(publicKey)
    sig.update(bytes)

    sig.verify(toByteArray(base64Signature))
  }

  def toBase64(publicKey: PublicKey): String =
    toBase64(publicKey.getEncoded)

  def generateKeyPair(): KeyPair = {
    import java.security.KeyPairGenerator
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
