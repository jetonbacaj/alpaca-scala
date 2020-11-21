package alpaca.modules

import alpaca.client.{AlpacaClient, AlpacaStreamingClient, PolygonClient, PolygonStreamingClient, StreamingClient}
import alpaca.service.{ConfigService, HTTPService, StreamingService}

trait MainModule {
  import com.softwaremill.macwire._
  //Services
  lazy val configService: ConfigService = wire[ConfigService]
//  lazy val hammockService: HammockService = wire[HammockService]
  lazy val streamingService: StreamingService = wire[StreamingService]
  lazy val httpService: HTTPService = wire[HTTPService]

  //Clients
  lazy val polygonClient: PolygonClient = wire[PolygonClient]
  lazy val alpacaClient: AlpacaClient = wire[AlpacaClient]
  lazy val alpacaStreamingClient: AlpacaStreamingClient = wire[AlpacaStreamingClient]
  lazy val polygonStreamingClient: PolygonStreamingClient = wire[PolygonStreamingClient]
  lazy val streamingClient: StreamingClient = wire[StreamingClient]
}
