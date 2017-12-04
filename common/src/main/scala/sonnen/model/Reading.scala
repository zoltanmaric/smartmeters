package sonnen.model

import java.time.Instant

import play.api.libs.json.{Json, OFormat}

case class Reading(inWh: Long, outWh: Long, timestamp: Instant)

object Reading {
  implicit val fmt: OFormat[Reading] = Json.format[Reading]
}
