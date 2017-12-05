package sonnen.model

import scala.concurrent.Future

trait NoResult

case object NoResult extends NoResult {
  val future: Future[NoResult] = Future.successful(NoResult)
}