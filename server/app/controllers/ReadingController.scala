package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import slick.jdbc.JdbcProfile
import sonnen.model.SignedReading

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReadingController @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                   cc: ControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  def reportReading(): Action[SignedReading] = Action.async(parse.json[SignedReading]) { request =>
    val reading = request.body

    Logger.info(s"Received signed reading: $reading")
    Future.successful(Ok)
  }

}
