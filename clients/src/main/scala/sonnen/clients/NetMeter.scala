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


class NetMeter(userWithKey: UserWithKey, wSClient: StandaloneWSClient)(implicit mat: Materializer) {

  def startReporting(): Future[NoResult] = {
    val readingSimulator = new ReadingSimulator()
    Source.tick(Random.nextInt(5000).millis, 5.seconds, Unit)
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

