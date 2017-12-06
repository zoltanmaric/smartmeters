package sonnen.clients

import akka.stream.Materializer
import sonnen.clients.util.ResourcesUtil

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object Main {

  def main(args: Array[String]): Unit = {
    val simulation = ResourcesUtil.withMaterializer { implicit mat: Materializer =>
      ResourcesUtil.withWsClient { wsClient =>

        val controller = new SimulationController(wsClient)

        controller.registerUsers(10).flatMap(controller.generateReadings)

      }
    }

    Await.result(simulation, Duration.Inf)

    println("done")
  }

}
