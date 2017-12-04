package controllers

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

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
    dbConfig.db.run(users += (0, username, publicKey))
      .map(_ => Created)
  }
}

case class KeyPair(secret: String, public: String)

object KeyPair {
  implicit val fmt: OFormat[KeyPair] = Json.format[KeyPair]
}
