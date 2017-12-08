package services

import javax.inject.{Inject, Singleton}

import dao.Dao
import sonnen.model.{NoResult, SignedReading}
import sonnen.utils.Signer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ReadingService @Inject()(dao: Dao) {

  /**
    * Verifies whether the signature is valid and whether the public key belongs to a registered user
    * @param signedReading the signed reading to verify
    * @return [[VerificationSuccess]] if the verification succeeds
    *         [[InvalidSignature]] if the signature is invalid
    *         [[UnknownPublicKey]] if the signature is valid, but the public key does not belong to a registered user
    */
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

  /**
    * Stores the provided reading into the database
    */
  def storeReading(signedReading: SignedReading): Future[NoResult] =
    dao.storeReading(signedReading)
}

sealed trait VerificationResult

case object VerificationSuccess extends VerificationResult

sealed trait VerificationFailure extends VerificationResult
case object InvalidSignature extends VerificationFailure
case object UnknownPublicKey extends VerificationFailure
