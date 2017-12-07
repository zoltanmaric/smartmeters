package sonnen.model

import play.api.libs.json.{Json, OFormat}

case class Reading(netInWh: Long, timestamp: Long)

object Reading {
  implicit val fmt: OFormat[Reading] = Json.format[Reading]
}

case class SignedReading(base64PublicKey: String, base64Signature: String, reading: Reading)

object SignedReading {
  implicit val fmt: OFormat[SignedReading] = Json.format[SignedReading]
}
