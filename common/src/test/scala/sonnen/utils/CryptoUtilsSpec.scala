package sonnen.utils

import org.scalatest.Matchers
import org.scalatest.WordSpecLike

class CryptoUtilsSpec extends WordSpecLike with Matchers {

  "CryptoUtilSpec" should {
    "Sign and verify" in {
      val dataToSign = "Some message".getBytes("UTF-8")

      val keyPair = CryptoUtils.generateKeyPair()
      val signature = CryptoUtils.sign(dataToSign, keyPair.getPrivate)

      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      CryptoUtils.verifySignature(dataToSign, base64PublicKey, signature) should be(true)
    }

    "fail signature verification when data corrupt" in {
      val dataToSign = "Some message".getBytes("UTF-8")

      val keyPair = CryptoUtils.generateKeyPair()
      val signature = CryptoUtils.sign(dataToSign, keyPair.getPrivate)

      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      val corruptData = "S0me message".getBytes("UTF-8")

      CryptoUtils.verifySignature(corruptData, base64PublicKey, signature) should be(false)
    }

    "fail signature verification when signature corrupt" in {
      val dataToSign = "Some message".getBytes("UTF-8")

      val keyPair = CryptoUtils.generateKeyPair()
      val signature = CryptoUtils.sign(dataToSign, keyPair.getPrivate)

      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      val corruptSignature = signature.updated(3, 'X')

      CryptoUtils.verifySignature(dataToSign, base64PublicKey, corruptSignature) should be(false)
    }
  }

}
