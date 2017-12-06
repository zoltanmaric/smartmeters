package sonnen.clients.util

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResourcesUtil {

  def withMaterializer[T](block: Materializer => Future[T]): Future[T] = {
    // Create Akka system for thread and streaming management
    implicit val system: ActorSystem = ActorSystem()
    val mat: ActorMaterializer = ActorMaterializer()

    block(mat).andThen { case _ => system.terminate() }
  }

  def withWsClient[T](block: StandaloneWSClient => Future[T])(implicit mat: Materializer): Future[T] = {
    val wsClient = StandaloneAhcWSClient()

    block(wsClient).andThen { case _ => wsClient.close() }
  }

}
