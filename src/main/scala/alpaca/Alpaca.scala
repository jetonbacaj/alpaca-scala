package alpaca

import alpaca.client.StreamingClient
import alpaca.dto._
import alpaca.dto.algebra.Bars
import alpaca.dto.polygon.{HistoricalAggregates, Trade}
import alpaca.dto.request.OrderRequest
import alpaca.modules.MainModule

import scala.concurrent.Future

case class Alpaca(isPaper: Option[Boolean] = None,
                  accountKey: Option[String] = None,
                  accountSecret: Option[String] = None)
    extends MainModule {

  configService.loadConfig(isPaper, accountKey, accountSecret)

  def getAccount: Future[Account] = {
    alpacaClient.getAccount
  }

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): Future[List[Assets]] = {
    alpacaClient.getAssets(status, asset_class)
  }

  def getAsset(symbol: String): Future[Assets] = {
    alpacaClient.getAsset(symbol)
  }

  def getBars(timeframe: String,
              symbols: List[String],
              limit: Option[String] = None,
              start: Option[String] = None,
              end: Option[String] = None,
              after: Option[String] = None,
              until: Option[String] = None): Future[Bars] = {
    alpacaClient.getBars(timeframe, symbols, limit, start, end, after, until)
  }

  def getCalendar(start: Option[String] = None,
                  end: Option[String] = None): Future[List[Calendar]] = {
    alpacaClient.getCalendar(start, end)
  }

  def getClock: Future[Clock] = {
    alpacaClient.getClock
  }

  def cancelOrder(orderId: String): Unit = {
    alpacaClient.cancelOrder(orderId)
  }

  def cancelAllOrders(): Unit = {
    alpacaClient.cancelAllOrders
  }

  def getOrder(orderId: String): Future[Orders] = {
    alpacaClient.getOrder(orderId)
  }

  def getOrders: Future[List[Orders]] = {
    alpacaClient.getOrders
  }

  def placeOrder(orderRequest: OrderRequest): Future[Orders] = {
    alpacaClient.placeOrder(orderRequest)
  }

  def getPositions: Future[List[Position]] = {
    alpacaClient.getPositions
  }

  def closePosition(symbol: String): Future[Orders] = {
    alpacaClient.closePosition(symbol)
  }

  def closeAllPositions: Future[Unit] = {
    alpacaClient.closeAllPositions()
  }

  def getPosition(symbol: String): Future[Position] = {
    alpacaClient.getPosition(symbol)
  }

  def getHistoricalTrades(symbol: String,
                          date: String,
                          offset: Option[Long] = None,
                          limit: Option[Int] = None): Future[Trade] = {
    polygonClient.getHistoricalTrades(symbol, date, offset, limit)
  }

  def getHistoricalTradesAggregate(
      symbol: String,
      size: String,
      from: Option[String] = None,
      to: Option[String] = None,
      limit: Option[Int] = None,
      unadjusted: Option[Boolean] = None): Future[HistoricalAggregates] = {
    polygonClient.getHistoricalTradesAggregate(symbol,
                                               size,
                                               from,
                                               to,
                                               limit,
                                               unadjusted)
  }

  def getStream: StreamingClient = {
    streamingClient
  }
}
