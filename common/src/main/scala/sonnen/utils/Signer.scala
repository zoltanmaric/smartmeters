package sonnen.utils

import java.nio.ByteBuffer
import java.security.KeyPair
import java.util.Base64

import sonnen.model.{Reading, SignedReading}

object Signer {
  private val inWhLength = 8
  private val outWhLength = 8
  private val timestampLength = 8

  private val totalLength = inWhLength + outWhLength + timestampLength

  def sign(reading: Reading, keyPair: KeyPair): SignedReading = {
    val dataToSign = toByteArray(reading)

    import java.security.Signature
    val sig: Signature = Signature.getInstance("SHA256withRSA")
    sig.initSign(keyPair.getPrivate)
    sig.update(dataToSign)
    val base64Signature = Base64.getEncoder.encodeToString(sig.sign())
    val base64PublicKey = Base64.getEncoder.encodeToString(keyPair.getPublic.getEncoded)

    SignedReading(base64PublicKey, base64Signature, reading)
  }

  private def toByteArray(reading: Reading): Array[Byte] = {
    val bb = ByteBuffer.allocate(totalLength)
    bb.putLong(reading.inWh)
    bb.putLong(reading.outWh)
    bb.putLong(reading.timestamp)
    bb.array()
  }
}
