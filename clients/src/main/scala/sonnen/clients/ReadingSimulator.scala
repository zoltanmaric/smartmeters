package sonnen.clients

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

import sonnen.model.Reading

import scala.util.Random

class ReadingSimulator() {

  private val DELTA_RANGE: Int = 5

  private val netInWh: AtomicLong = new AtomicLong()

  def getReading: Reading = {
    val deltaInWh = generateRandomDelta()

    Reading(netInWh.addAndGet(deltaInWh), Instant.now().toEpochMilli)
  }

  private def generateRandomDelta(): Long = (Random.nextGaussian() * DELTA_RANGE).toLong
}
