package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext
import scala.util.Success

@Singleton
class RegistrationController @Inject()(
                                        protected val dbConfigProvider: DatabaseConfigProvider,
                                        cc: ControllerComponents
                                      )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile._

  class Users(tag: Tag) extends Table[(Long, String, String)](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def username = column[String]("username")
    def publicKey = column[String]("public_key")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, username, publicKey)
  }
  val users = TableQuery[Users]


  // TODO: restrict access
  def register(username: String, publicKey: String): Action[AnyContent] = Action.async {
    val res = dbConfig.db.run(users += (0, username, publicKey))
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
