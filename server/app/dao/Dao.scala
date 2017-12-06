package dao

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import sonnen.model.NoResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Dao @Inject()(dbTables: DbTables, protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbTables._

  def register(username: String, publicKey: String): Future[NoResult] =
    dbConfig.db.run(users += (0, username, publicKey))
      .map(_ => NoResult)

  def publicKeyExists(base64PublicKey: String): Future[Boolean] = ???

}
