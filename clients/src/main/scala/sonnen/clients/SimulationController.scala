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

  /**
    * Generates asymmetrical key pairs and usernames and registers them on the server.
    * @param numUsers the number of users to register
    * @return a list containing the usernames associated to their respective key pairs.
    */
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

  /**
    * Generates random smart meter readings and submits them to the server
    * @param readingInterval the interval at which the readings should be generated/submitted
    * @param numReadings the total number of readings to submit
    * @param usersWithKeys the list of users with their keypairs for which readings should be submitted
    * @param mat a streams materializer required for generating the akka stream of readings
    * @return a successful future if all readings from all meters are submitted successfully, a failed future if any of
    *         the readings fail to be generated or submitted
    */
  def generateReadings(readingInterval: FiniteDuration, numReadings: Int)(usersWithKeys: Seq[UserWithKey])(implicit mat: Materializer): Future[NoResult] =
    Future.traverse(usersWithKeys) { userWithKey =>
      new SmartMeter(userWithKey, wsClient).startReporting(readingInterval, numReadings)
    }.map(_ => NoResult)

}

case class UserWithKey(username: String, keyPair: KeyPair)
