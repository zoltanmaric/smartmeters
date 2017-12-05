package sonnen.utils

import java.nio.ByteBuffer
import java.security.{KeyPair, PublicKey, Signature}
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

import sonnen.model.{Reading, SignedReading}

object Signer {
  private val inWhLength = 8
  private val outWhLength = 8
  private val timestampLength = 8

  private val totalLength = inWhLength + outWhLength + timestampLength

  def sign(reading: Reading, keyPair: KeyPair): SignedReading = {
    val dataToSign = toByteArray(reading)

    val sig = Signature.getInstance("SHA256withRSA")
    sig.initSign(keyPair.getPrivate)
    sig.update(dataToSign)
    val base64Signature = Base64.getEncoder.encodeToString(sig.sign())
    val base64PublicKey = Base64.getEncoder.encodeToString(keyPair.getPublic.getEncoded)

    SignedReading(base64PublicKey, base64Signature, reading)
  }

  def verifySignature(signedReading: SignedReading): Boolean = {
    val dataToSign = toByteArray(signedReading.reading)

    val publicKeyBytes = Base64.getDecoder.decode(signedReading.base64PublicKey)
    val publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes))

    val sig = Signature.getInstance("SHA256withRSA")
    sig.initVerify(publicKey)
    sig.update(dataToSign)

    sig.verify(Base64.getDecoder.decode(signedReading.base64Signature))
  }

  private def toByteArray(reading: Reading): Array[Byte] = {
    val bb = ByteBuffer.allocate(totalLength)
    bb.putLong(reading.inWh)
    bb.putLong(reading.outWh)
    bb.putLong(reading.timestamp)
    bb.array()
  }
}
