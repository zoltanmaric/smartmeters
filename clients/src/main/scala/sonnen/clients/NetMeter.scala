package sonnen.clients

import java.nio.ByteBuffer
import java.util.Base64

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSClient
import sonnen.model.{NoResult, Reading, SignedReading}
import play.api.libs.ws.JsonBodyWritables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random


class NetMeter(userWithKey: UserWithKey, wSClient: StandaloneWSClient)(implicit mat: Materializer) {

  def startReporting(): Future[NoResult] = {
    val readingSimulator = new ReadingSimulator()
    Source.tick(Random.nextInt(5000).millis, 5.seconds, Unit)
      .map(_ => readingSimulator.getReading)
      .map(Signer.sign(_, userWithKey))
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

object Signer {
  private val inWhLength = 8
  private val outWhLength = 8
  private val timestampLength = 8

  private val totalLength = inWhLength + outWhLength + timestampLength

  def sign(reading: Reading, userWithKey: UserWithKey): SignedReading = {
    val dataToSign = toByteArray(reading)

    import java.security.Signature
    val sig: Signature = Signature.getInstance("SHA256withRSA")
    sig.initSign(userWithKey.keyPair.getPrivate)
    sig.update(dataToSign)
    val base64Signature = Base64.getEncoder.encodeToString(sig.sign())
    val base64PublicKey = Base64.getEncoder.encodeToString(userWithKey.keyPair.getPublic.getEncoded)

    SignedReading(base64PublicKey, base64Signature, reading)
  }

  private def toByteArray(reading: Reading): Array[Byte] = {
    val bb = ByteBuffer.allocate(totalLength)
    bb.putLong(reading.inWh)
    bb.putLong(reading.outWh)
    bb.putLong(reading.timestamp)
    bb.array()
  }
}
