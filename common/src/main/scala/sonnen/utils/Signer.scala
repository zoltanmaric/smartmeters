package sonnen.utils

import java.nio.ByteBuffer
import java.security.KeyPair

import sonnen.model.{Reading, SignedReading}

object Signer {
  private val netInWhLength = 8
  private val timestampLength = 8

  private val totalLength = netInWhLength + timestampLength

  def sign(reading: Reading, keyPair: KeyPair): SignedReading = {
    val dataToSign = toByteArray(reading)

    val base64Signature = CryptoUtils.sign(dataToSign, keyPair.getPrivate)
    val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

    SignedReading(base64PublicKey, base64Signature, reading)
  }

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
