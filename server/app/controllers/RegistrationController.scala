package controllers

import javax.inject.{Inject, Singleton}

import dao.Dao
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.util.Success

@Singleton
class RegistrationController @Inject()(
                                        dao: Dao,
                                        cc: ControllerComponents
                                      )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // TODO: restrict access
  def register(username: String, publicKey: String): Action[AnyContent] = Action.async {
    val res = dao.register(username, publicKey)
      .map(_ => Created)

    res.onComplete {
      case Success(_) =>
        Logger.info(s"Successfully registered $username with key $publicKey")
      case _ =>
        ()
    }

    res
  }
}
