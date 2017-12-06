package sonnen.model

import play.api.libs.json.{Json, OFormat}

case class Registration(username: String, base64PublicKey: String)

object Registration {
  implicit val fmt: OFormat[Registration] = Json.format[Registration]
}
