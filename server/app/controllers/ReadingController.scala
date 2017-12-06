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

  def reportReading(): Action[SignedReading] = Action.async(parse.json[SignedReading]) { request =>
    val reading = request.body

    Logger.info(s"Received signed reading: $reading")

    readingService.verifyReading(reading).flatMap {
      case VerificationSuccess =>
        readingService.storeReading(reading).map(_ => Ok)
      case UnknownPublicKey =>
        Future.successful(Forbidden(s"Unknown public key ${reading.base64PublicKey} in $reading"))
      case InvalidSignature =>
        Future.successful(BadRequest(s"Invalid signature in $reading"))
    }
  }

}
