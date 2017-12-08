package sonnen.clients

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

import sonnen.model.Reading

import scala.util.Random

/**
  * The simulation of the actual smart meter
  */
class ReadingSimulator() {

  private val DELTA_RANGE: Int = 5

  private val netInWh: AtomicLong = new AtomicLong()

  /**
    * Updates the internal net status (Wh input - Wh output) and returns it
    * @return the internal net reading status
    */
  def getReading: Reading = {
    val deltaInWh = generateRandomDelta()

    Reading(netInWh.addAndGet(deltaInWh), Instant.now().toEpochMilli)
  }

  private def generateRandomDelta(): Long = (Random.nextGaussian() * DELTA_RANGE).toLong
}
