package services

import javax.inject.{Inject, Singleton}

import dao.Dao
import sonnen.model.SignedReading
import sonnen.utils.Signer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ReadingService @Inject()(dao: Dao) {

  def verifyReading(signedReading: SignedReading): Future[VerificationResult] =
    if (Signer.verifySignature(signedReading))
      dao.publicKeyExists(signedReading.base64PublicKey).map {
        case true =>
          VerificationSuccess
        case false =>
          UnknownPublicKey
      }
    else
      Future.successful(InvalidSignature)
}

sealed trait VerificationResult

case object VerificationSuccess extends VerificationResult

sealed trait VerificationFailure extends VerificationResult
case object InvalidSignature extends VerificationFailure
case object UnknownPublicKey extends VerificationFailure
