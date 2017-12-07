package sonnen.clients

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

import sonnen.model.Reading

import scala.util.Random

class ReadingSimulator() {

  private val DELTA_RANGE: Int = 5

  private val inWh: AtomicLong = new AtomicLong()
  private val outWh: AtomicLong = new AtomicLong()

  def getReading: Reading = {
    val deltaInWh = getRandomDelta
    val deltaOutWh = getRandomDelta

    Reading(inWh.addAndGet(deltaInWh), outWh.addAndGet(deltaOutWh), Instant.now().toEpochMilli)
  }

  private def getRandomDelta: Long = (((Random.nextGaussian() + 1) / 2) * DELTA_RANGE).toLong
}
