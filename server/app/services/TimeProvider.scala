package services

import java.time.Instant
import javax.inject.Singleton

@Singleton
class TimeProvider {

  def getTime: Instant = Instant.now()
}
