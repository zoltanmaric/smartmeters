package dao

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import sonnen.model.{NoResult, Reading, SignedReading}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Dao @Inject()(dbTables: DbTables, protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbTables._

  def register(username: String, publicKey: String): Future[NoResult] =
    dbConfig.db.run(users += (0, username, publicKey))
      .map(_ => NoResult)

  def publicKeyExists(base64PublicKey: String): Future[Boolean] =
    dbConfig.db.run(users.filter(_.publicKey === base64PublicKey).result)
      .map(_.nonEmpty)

  def storeReading(signedReading: SignedReading): Future[NoResult] = {
    val SignedReading(publicKey, signature, Reading(inWh, outWh, epochTimestamp)) = signedReading

    val timestamp = Timestamp.from(Instant.ofEpochMilli(epochTimestamp))

    val query = sql"""INSERT INTO readings (in_wh, out_wh, read_time, signature, user_id)
            SELECT ${inWh}, ${outWh}, ${timestamp}, ${signature}, id FROM users WHERE public_key = ${publicKey}"""

//    val select: Query[(Rep[Long], Rep[Long], Rep[Timestamp], Rep[String], Rep[Long]), (Long, Long, Timestamp, String, Long), Seq] = users.filter(_.publicKey === publicKey).map(user => (inWh, outWh, timestamp, signature, user.id))
//    val query = readings.map {
//      case (_, _inWh, _outWh, readTime, _signature, userId) =>
//        (_inWh, _outWh, readTime, _signature, userId)
//    }.forceInsertQuery(select)

    dbConfig.db.run(query.asUpdate).map(_ => NoResult)
  }
}
