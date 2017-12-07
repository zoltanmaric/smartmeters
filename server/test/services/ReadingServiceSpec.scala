package services

import java.time.Instant

import dao.Dao
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpecLike, Matchers}
import sonnen.model.Reading
import sonnen.utils.{CryptoUtils, Signer}

import scala.concurrent.Future

class ReadingServiceSpec extends AsyncWordSpecLike with MockitoSugar with Matchers {

  private class TestService(val dao: Dao = mock[Dao]) extends ReadingService(dao)

  "ReadingService" should {

    "accept reading with valid signature from known user" in {
      val service = new TestService()

      val keyPair = CryptoUtils.generateKeyPair()
      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      when(service.dao.publicKeyExists(base64PublicKey)) thenReturn Future.successful(true)

      val reading = Reading(1, 2, Instant.now.toEpochMilli)

      val signedReading = Signer.sign(reading, keyPair)

      service.verifyReading(signedReading).map(_ shouldEqual VerificationSuccess)
    }

  }
}
