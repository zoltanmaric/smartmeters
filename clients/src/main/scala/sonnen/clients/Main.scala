package sonnen.clients

import java.util.concurrent.TimeUnit

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import sonnen.clients.util.ResourcesUtil

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/** The main entry point of the clients simulator. */
object Main {

  /**
    * All arguments are ignored. Parameters can be configured in reference.conf instead.
    */
  def main(args: Array[String]): Unit = {
    val simulation = ResourcesUtil.withMaterializer { implicit mat: Materializer =>
      ResourcesUtil.withWsClient { wsClient =>

        val conf = loadConfiguration()
        val controller = new SimulationController(wsClient)

        controller.registerUsers(conf.numUsers).flatMap(controller.generateReadings(conf.readingInterval, conf.numReadings))

      }
    }

    Await.result(simulation, Duration.Inf)

    println("done")
  }

  private def loadConfiguration(): AppConfiguration = {
    val conf = ConfigFactory.load()

    val duration = conf.getDuration("readingInterval")
    val finiteDuration = FiniteDuration(duration.toMillis, TimeUnit.MILLISECONDS)

    AppConfiguration(
      conf.getInt("numUsers"),
      finiteDuration,
      conf.getInt("numReadings")
    )
  }

}

private case class AppConfiguration(numUsers: Int, readingInterval: FiniteDuration, numReadings: Int)
