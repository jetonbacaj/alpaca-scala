package alpaca.service

import akka.stream.StreamRefMessages.Payload
import alpaca.dto.Parameter

import scala.concurrent.Future
import sttp.client3._
import sttp.client3.akkahttp._
import sttp.client3.circe._
import io.circe.generic.auto._

class HTTPService (configService: ConfigService) extends BaseService {

  private val backend = AkkaHttpBackend()


  /*
  def buildURI(url: String, urlParams: Option[Array[(String, String)]] = None)
    : UriInterpolator.Output = {

    val withParams = if (urlParams.isDefined) {
      uri"${url}".params(urlParams.get: _*)
    } else {
      uri"$url"
    }

    withParams
  }

  def execute[A, B](method: Method,
                    url: String,
                    body: Option[B] = None,
                    queryParams: Option[Array[(String, String)]] = None)(
      implicit hammockEvidence: hammock.Decoder[A],
      hammockEvidenceEncoder: hammock.Encoder[B]): IO[A] = {
    val trueUrl = buildURI(url, queryParams)

    Hammock
      .request(
        method,
        trueUrl,
        Map(
          "APCA-API-KEY-ID" -> configService.getConfig.value.accountKey,
          "APCA-API-SECRET-KEY" -> configService.getConfig.value.accountSecret),
        body
      ) // In the `request` method, you describe your HTTP request
      .as[A]
      .exec[IO]
  }
   */

  def executeGet[A, B](url: String,
                       queryParams: Option[Array[(String, String)]] = None): Future[A] = {

    ???
  }

  def executePost[A, B](url: String,
                        body: Option[B],
                        queryParams: Option[Array[(String, String)]] = None): Future[A] = {

    ???
  }

  def executePatch[A, B](url: String,
                         body: Option[B],
                         queryParams: Option[Array[(String, String)]] = None): Future[A] = {

    ???
  }

  def executeDelete[A, B](url: String,
                          queryParams: Option[Array[(String, String)]] = None): Future[A] = {

    ???
  }

  private def execute[A, B](url: String,
                            body: Option[B] = None, queryParams: Option[Array[(String, String)]] = None): Future[A] = {

    val req = basicRequest

    queryParams.map(tuples => req.body(Map.from(tuples)))

    req
      .headers(
        Map("APCA-API-KEY-ID" -> configService.getConfig.accountKey,
          "APCA-API-SECRET-KEY" -> configService.getConfig.accountSecret)
      )
      .post(uri url)
      .response(asJson[A].getRight)
      .send(backend)

    ???

  }
}
