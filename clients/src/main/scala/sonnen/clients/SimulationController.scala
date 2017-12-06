package sonnen.clients

import java.security.KeyPair
import java.util.Base64

import akka.stream.Materializer
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSClient
import sonnen.model.{NoResult, Registration}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class SimulationController(wsClient: StandaloneWSClient) {

  def register100Users(): Future[Seq[UserWithKey]] =
    Future.traverse(1 to 100) { i =>

      val username = Random.alphanumeric.take(10).mkString
      val keyPair = generateKeyPair()
      val base64PublicKey = Base64.getEncoder.encodeToString(keyPair.getPublic.getEncoded)
      val url = s"http://localhost:9000/register"
      val registration = Registration(username, base64PublicKey)

      println(s"Registering user $i: $registration")

      wsClient.url(url)
        .post(Json.toJson(registration))
        .map { res =>
          if (res.status >= 400) {
            throw new RuntimeException(s"Got response ${res.status}: ${res.body} while posting to $url")
          } else {
            println(s"Successfully registered user $i: $username, $base64PublicKey")
            UserWithKey(username, keyPair)
          }
        }
    }

  def generateReadings(usersWithKeys: Seq[UserWithKey])(implicit mat: Materializer): Future[NoResult] =
    Future.traverse(usersWithKeys) { userWithKey =>
      new NetMeter(userWithKey, wsClient).startReporting()
    }.map(_ => NoResult)

  private def generateKeyPair(): KeyPair = {
    import java.security.KeyPairGenerator
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(1024)
    kpg.genKeyPair()
  }

}

case class UserWithKey(username: String, keyPair: KeyPair)
