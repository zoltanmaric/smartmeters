package sonnen.utils

import java.nio.ByteBuffer
import java.security.KeyPair

import sonnen.model.{Reading, SignedReading}

/**
  * A utility object for signing readings and verifying signatures
  */
object Signer {
  private val netInWhLength = 8
  private val timestampLength = 8

  private val totalLength = netInWhLength + timestampLength

  /**
    * Signs the provided reading
    * @param reading the reading to sign
    * @param keyPair the key pair to use for signing
    * @return an object containing the reading, the signature and the public key
    */
  def sign(reading: Reading, keyPair: KeyPair): SignedReading = {
    val dataToSign = toByteArray(reading)

    val base64Signature = CryptoUtils.sign(dataToSign, keyPair.getPrivate)
    val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

    SignedReading(base64PublicKey, base64Signature, reading)
  }

  /**
    * Verifies the signature in a signed reading
    * @param signedReading the signed reading to verify
    * @return true if the signature is valid, false otherwise
    */
  def verifySignature(signedReading: SignedReading): Boolean = {
    val dataToSign = toByteArray(signedReading.reading)

    CryptoUtils.verifySignature(dataToSign, signedReading.base64PublicKey, signedReading.base64Signature)
  }

  private def toByteArray(reading: Reading): Array[Byte] = {
    val bb = ByteBuffer.allocate(totalLength)
    bb.putLong(reading.netInWh)
    bb.putLong(reading.timestamp)
    bb.array()
  }
}
