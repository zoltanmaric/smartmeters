package sonnen.clients

import java.security.KeyPair
import java.util.Base64

import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.DefaultBodyWritables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class SimulationController(wsClient: StandaloneWSClient) {

  def register100Users(): Future[Seq[UserWithKey]] =
    Future.traverse(1 to 100) { i =>

      val username = Random.alphanumeric.take(10).mkString
      val keyPair = generateKeyPair()
      val base64PublicKey = Base64.getEncoder.encodeToString(keyPair.getPublic.getEncoded)
      val url = s"http://localhost:9000/register?username=$username&publicKey=$base64PublicKey"

      println(s"Registering user $i: $username with key $base64PublicKey")

      wsClient.url(url)
        .post("")
        .map { res =>
          if (res.status >= 400) {
            throw new RuntimeException(s"Got response ${res.status}: ${res.body} while posting to $url")
          } else {
            println(s"Successfully registered user $i: $username, $base64PublicKey")
            UserWithKey(username, keyPair)
          }
        }
    }

  private def generateKeyPair(): KeyPair = {
    import java.security.KeyPairGenerator
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(2048)
    kpg.genKeyPair()
  }

}

case class UserWithKey(username: String, keyPair: KeyPair)
