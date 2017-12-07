package sonnen.clients

import java.security.KeyPair

import akka.stream.Materializer
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSClient
import play.shaded.ahc.io.netty.handler.codec.http.HttpResponseStatus
import sonnen.model.{NoResult, Registration}
import sonnen.utils.CryptoUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

class SimulationController(wsClient: StandaloneWSClient) {

  def registerUsers(numUsers: Int): Future[Seq[UserWithKey]] =
    Future.traverse(1 to numUsers) { i =>

      val username = Random.alphanumeric.take(10).mkString
      val keyPair = CryptoUtils.generateKeyPair()
      val base64PublicKey = CryptoUtils.toBase64(keyPair.getPublic)
      val url = s"http://localhost:9000/register"
      val registration = Registration(username, base64PublicKey)

      println(s"Registering user $i: $registration")

      wsClient.url(url)
        .post(Json.toJson(registration))
        .map { res =>
          if (res.status >= HttpResponseStatus.BAD_REQUEST.code) {
            throw new RuntimeException(s"Got response ${res.status}: ${res.body} while posting to $url")
          } else {
            println(s"Successfully registered user $i: $username, $base64PublicKey")
            UserWithKey(username, keyPair)
          }
        }
    }

  def generateReadings(readingInterval: FiniteDuration, numReadings: Int)(usersWithKeys: Seq[UserWithKey])(implicit mat: Materializer): Future[NoResult] =
    Future.traverse(usersWithKeys) { userWithKey =>
      new NetMeter(userWithKey, wsClient).startReporting(readingInterval, numReadings)
    }.map(_ => NoResult)

}

case class UserWithKey(username: String, keyPair: KeyPair)
