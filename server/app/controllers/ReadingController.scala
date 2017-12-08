package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{InvalidSignature, ReadingService, UnknownPublicKey, VerificationSuccess}
import sonnen.model.SignedReading

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReadingController @Inject()(
                                   readingService: ReadingService,
                                   cc: ControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
    * Verifies the signature on the provided reading, checks whether the public key belongs to a registered user, and
    * stores the reading into the database, along with the signature and a link to the owner.
    * @return 201 CREATED on success
    *         403 FORBIDDEN if the provided public key does not belong to a registered user
    *         400 BAD REQUEST if the signature verification fails
    *
    */
  def reportReading(): Action[SignedReading] = Action.async(parse.json[SignedReading]) { request =>
    val reading = request.body

    Logger.info(s"Received signed reading: $reading")

    readingService.verifyReading(reading).flatMap {
      case VerificationSuccess =>
        readingService.storeReading(reading).map(_ => Created)
      case UnknownPublicKey =>
        Future.successful(Forbidden(s"Unknown public key ${reading.base64PublicKey} in $reading"))
      case InvalidSignature =>
        Future.successful(BadRequest(s"Invalid signature in $reading"))
    }
  }

}
