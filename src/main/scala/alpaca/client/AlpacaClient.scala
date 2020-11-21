package alpaca.client

import alpaca.dto._
import alpaca.dto.algebra.Bars
import alpaca.dto.request.OrderRequest
import alpaca.service.{ConfigService, HTTPService}
import com.typesafe.scalalogging.Logger

import scala.concurrent.Future

private[alpaca] class AlpacaClient(configService: ConfigService,
                                   httpService: HTTPService) {

  private val logger: Logger = Logger(classOf[AlpacaClient])

  def getAccount: Future[Account] =
    httpService.executeGet[Account, Unit](configService.getConfig.account_url)

  def getAsset(symbol: String): Future[Assets] =
    httpService.executeGet[Assets, Unit](s"${configService.getConfig.assets_url}/$symbol")

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): Future[List[Assets]] = {
    httpService.executeGet[List[Assets], Unit](
      configService.getConfig.assets_url,
      httpService.createTuples(
        Parameter("status", status),
        Parameter("asset_class", asset_class))
    )
  }

  def getBars(timeframe: String,
              symbols: List[String],
              limit: Option[String] = None,
              start: Option[String] = None,
              end: Option[String] = None,
              after: Option[String] = None,
              until: Option[String] = None): Future[Bars] =
    httpService.executeGet[Bars, Unit](
      s"${configService.getConfig.bars_url}/$timeframe",
      httpService.createTuples(Parameter("symbols", Some(symbols.mkString(","))),
        Parameter("limit", limit),
        Parameter("start", start),
        Parameter("end", end),
        Parameter("after", after),
        Parameter("until", until)
      )
    )

  def getCalendar(start: Option[String] = None, end: Option[String] = None): Future[List[Calendar]] =
    httpService.executeGet[List[Calendar], Unit](
      configService.getConfig.calendar_url,
      httpService.createTuples(Parameter("start", start), Parameter("end", end)))

  def getClock: Future[Clock] =
    httpService.executeGet[Clock, Unit](
      configService.getConfig.clock_url)

  def getOrder(orderId: String): Future[Orders] =
    httpService.executeGet[Orders, Unit](
      s"${configService.getConfig.order_url}/$orderId")

  def cancelOrder(orderId: String): Future[String] =
    httpService.executeDelete[String, Unit](
      s"${configService.getConfig.order_url}/$orderId")

  def cancelAllOrders: Future[String] =
    httpService.executeDelete[String, Unit](
      s"${configService.getConfig.order_url}")

  def getOrders: Future[List[Orders]] =
    httpService.executeGet[List[Orders], Unit](
      configService.getConfig.order_url)

  def placeOrder(orderRequest: OrderRequest): Future[Orders] =
    httpService.executePost[Orders, OrderRequest](
      configService.getConfig.order_url,
      Some(orderRequest))

  def getPositions: Future[List[Position]] =
    httpService.executeGet[List[Position], Unit](
      configService.getConfig.positions_url)

  def getPosition(symbol: String): Future[Position] =
    httpService.executeGet[Position, Unit](
      s"${configService.getConfig.positions_url}/$symbol")

  def closePosition(symbol: String): Future[Orders] =
    httpService.executeDelete[Orders, Unit](
      s"${configService.getConfig.positions_url}/$symbol")

  def closeAllPositions(): Future[Unit] =
    httpService.executeDelete[Unit, Unit](
      s"${configService.getConfig.positions_url}")
}
