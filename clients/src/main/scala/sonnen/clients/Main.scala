package sonnen.clients

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc._

import scala.concurrent.Await
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

    // Create the standalone WS client
    // no argument defaults to a AhcWSClientConfig created from
    // "AhcWSClientConfigFactory.forConfig(ConfigFactory.load, this.getClass.getClassLoader)"
    val wsClient = StandaloneAhcWSClient()

    val controller = new SimulationController(wsClient)

    Await.result(controller.register100Users()
      .andThen { case _ => wsClient.close() }
      .andThen { case _ => system.terminate() }, Duration.Inf)

    println("done")
  }

}
