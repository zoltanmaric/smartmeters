package services.client

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

import scala.util.Random

class ReadingSimulator {

  private val DELTA_RANGE: Int = 5

  private val inWh: AtomicLong = new AtomicLong()
  private val outWh: AtomicLong = new AtomicLong()

  def getReading: Reading = {
    val deltaInWh = getRandomDelta
    val deltaOutWh = getRandomDelta

    Reading(inWh.addAndGet(deltaInWh), outWh.addAndGet(deltaOutWh), Instant.now())
  }

  private def getRandomDelta: Long = (Random.nextGaussian() * DELTA_RANGE).toLong
}

case class Reading(inWh: Long, outWh: Long, timestamp: Instant)
