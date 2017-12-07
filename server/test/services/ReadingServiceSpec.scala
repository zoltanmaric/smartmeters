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

      val reading = Reading(1, Instant.now.toEpochMilli)

      val signedReading = Signer.sign(reading, keyPair)

      service.verifyReading(signedReading).map(_ shouldEqual VerificationSuccess)
    }

    "reject reading with valid signature from unknown user" in {
      val service = new TestService()

      val keyPair = CryptoUtils.generateKeyPair()
      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      // Mock DAO says this is not a known public key
      when(service.dao.publicKeyExists(base64PublicKey)) thenReturn Future.successful(false)

      val reading = Reading(1, Instant.now.toEpochMilli)

      val signedReading = Signer.sign(reading, keyPair)

      service.verifyReading(signedReading).map(_ shouldBe a[VerificationFailure])
    }

    "reject reading with invalid signature from known user" in {
      val service = new TestService()

      val keyPair = CryptoUtils.generateKeyPair()
      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      when(service.dao.publicKeyExists(base64PublicKey)) thenReturn Future.successful(true)

      val reading = Reading(1, Instant.now.toEpochMilli)

      val signedReading = Signer.sign(reading, keyPair)

      // Corrupt the character at index 2
      val corruptedBase64Signature = signedReading.base64Signature.updated(2, 'Y')

      // Replace signature with the corrupted one
      val corruptedSignedReading = signedReading.copy(base64Signature = corruptedBase64Signature)

      service.verifyReading(corruptedSignedReading).map(_ shouldBe a[VerificationFailure])
    }

    "reject reading with tampered reading from known user" in {
      val service = new TestService()

      val keyPair = CryptoUtils.generateKeyPair()
      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)

      when(service.dao.publicKeyExists(base64PublicKey)) thenReturn Future.successful(true)

      val reading = Reading(1, Instant.now.toEpochMilli)

      val signedReading = Signer.sign(reading, keyPair)

      // Replace reading in SignedReading
      val tamperedSignedReading = signedReading.copy(reading = Reading(1000, reading.timestamp))

      service.verifyReading(tamperedSignedReading).map(_ shouldBe a[VerificationFailure])
    }
  }
}
