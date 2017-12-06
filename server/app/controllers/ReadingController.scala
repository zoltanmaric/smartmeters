package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{InvalidSignature, ReadingService, UnknownPublicKey, VerificationSuccess}
import sonnen.model.SignedReading
import sonnen.utils.Signer

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReadingController @Inject()(
                                   readingService: ReadingService,
                                   cc: ControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def reportReading(): Action[SignedReading] = Action.async(parse.json[SignedReading]) { request =>
    val reading = request.body

    Logger.info(s"Received signed reading: $reading")

    readingService.verifyReading(reading).map {
      case VerificationSuccess =>
        Ok
      case UnknownPublicKey =>
        Forbidden(s"Unknown public key in $reading")
      case InvalidSignature =>
        BadRequest(s"Invalid signature in $reading")
    }

    if (Signer.verifySignature(reading))
      Future.successful(Ok)
    else
      Future.successful(Forbidden(s"Invalid signature received in $reading"))
  }

}
