package sonnen.clients

import java.time.Instant

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import play.api.libs.ws.ahc._

import scala.concurrent.{Await, Future}
import DefaultBodyReadables._
import sonnen.model.Reading

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object Main {

  def main(args: Array[String]): Unit = {
    // Create Akka system for thread and streaming management
    implicit val system: ActorSystem = ActorSystem()
    system.registerOnTermination {
      System.exit(0)
    }
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    Reading(1, 1, Instant.now())

    // Create the standalone WS client
    // no argument defaults to a AhcWSClientConfig created from
    // "AhcWSClientConfigFactory.forConfig(ConfigFactory.load, this.getClass.getClassLoader)"
    val wsClient = StandaloneAhcWSClient()

    Await.result(call(wsClient)
      .andThen { case _ => wsClient.close() }
      .andThen { case _ => system.terminate() }, Duration.Inf)

    println("done")
  }

  def call(wsClient: StandaloneWSClient): Future[Unit] = {
    wsClient.url("http://www.google.com").get().map { response ⇒
      val statusText: String = response.statusText
      val body = response.body[String]
      println(s"Got a response $statusText")
    }
  }

}
