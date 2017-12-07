package dao

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

@Singleton
class DbTables @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile._

  class Users(tag: Tag) extends Table[(Long, String, String)](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def username = column[String]("username")
    def publicKey = column[String]("public_key")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, username, publicKey)
  }

  val users = TableQuery[Users]

  class Readings(tag: Tag) extends Table[(Long, Long, Timestamp, String, Long)](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def netInWh = column[Long]("net_in_wh")
    def readTime = column[Timestamp]("read_time")
    def signature = column[String]("signature")
    def userId = column[Long]("user_id")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, netInWh, readTime, signature, userId)
  }

  val readings = TableQuery[Readings]
}
