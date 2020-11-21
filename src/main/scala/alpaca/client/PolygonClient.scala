package alpaca.client

import alpaca.dto.Parameter
import alpaca.dto.polygon.{HistoricalAggregates, Trade}
import alpaca.service.{ConfigService, HTTPService}
import cats.implicits._

import scala.concurrent.Future

class PolygonClient(configService: ConfigService,
                    httpService: HTTPService) {

  def getHistoricalTrades(symbol: String,
                          date: String,
                          offset: Option[Long] = None,
                          limit: Option[Int] = None): Future[Trade] = {

    httpService.executeGet[Trade, Unit](
      s"${configService.getConfig.basePolygonUrl}/v1/historic/trades/$symbol/$date",
      httpService.createTuples(Parameter("apiKey", configService.getConfig.accountKey.some),
        Parameter("offset", offset),
        Parameter("limit", limit)
      )
    )
  }

  def getHistoricalTradesAggregate(
      symbol: String,
      size: String,
      from: Option[String] = None,
      to: Option[String] = None,
      limit: Option[Int] = None,
      unadjusted: Option[Boolean] = None): Future[HistoricalAggregates] =

    httpService.executeGet[HistoricalAggregates, Unit](
      s"${configService.getConfig.basePolygonUrl}/v1/historic/agg/$size/$symbol",
      httpService.createTuples(Parameter("apiKey", configService.getConfig.accountKey.some),
        Parameter("from", from),
        Parameter("to", to),
        Parameter("limit", limit)
      )
    )
}
