package sonnen.clients

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSClient
import sonnen.model.{NoResult, SignedReading}
import sonnen.utils.Signer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

/**
  * A representation of a user's smart meter
  * @param userWithKey the owner of the smart meter and their key pair
  * @param wSClient a client for web requests
  * @param mat a streams materializer required for generating the akka stream of readings
  */
class SmartMeter(userWithKey: UserWithKey, wSClient: StandaloneWSClient)(implicit mat: Materializer) {

  /**
    * Starts the generation and submission of readings from this meter
    * @param readingInterval the interval at which the readings should be generated/submitted
    * @param numReadings the total number of readings to submit
    * @return a successful future if all readings are submitted successfully, a failed future if any of the readings
    *         fail to be generated or submitted
    */
  def startReporting(readingInterval: FiniteDuration, numReadings: Int): Future[NoResult] = {
    val readingSimulator = new ReadingSimulator()

    // Start after a random number of milliseconds to even out the reporting from the different meters
    val startAfter = Random.nextInt(readingInterval.toMillis.toInt).millis

    Source.tick(startAfter, readingInterval, Unit)
      .take(numReadings)
      .map(_ => readingSimulator.getReading)
      .map(Signer.sign(_, userWithKey.keyPair))
      .mapAsync(1)(reportReading)
      .runWith(Sink.ignore)
      .map(_ => NoResult)
  }

  private def reportReading(signedReading: SignedReading): Future[NoResult] = {
    println(s"reporting reading for user ${userWithKey.username}: ${signedReading.reading}")
    wSClient.url("http://localhost:9000/reportReading")
      .post(Json.toJson(signedReading))
      .map { res =>
        if (res.status >= 400) {
          throw new RuntimeException(s"Got response ${res.status}: ${res.body}, while reporting reading $signedReading")
        } else {
          NoResult
        }
      }
  }
}

