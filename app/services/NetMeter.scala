package services

import java.time.Instant
import javax.inject.{Inject, Singleton}

import play.api.libs.ws.WSClient

import scala.concurrent.Future

@Singleton
class NetMeter @Inject()(wSClient: WSClient) {

  def reportReading(reading: Reading): Future[NoResult] = {

    // TODO: sign reading with the meter's key and send it to the server
    // if it succeeds, restart the counter, if it fails retry later with updated counter
    // actors? reply with reported reading?
    // Don't restart counter? Fire and forget reporting?


    NoResult.future
  }
}

trait NoResult

case object NoResult extends NoResult {
  val future: Future[NoResult] = Future.successful(NoResult)
}

case class Reading(netEnergyMilliWattHours: BigInt, timestamp: Instant)
