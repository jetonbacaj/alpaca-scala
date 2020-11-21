package alpaca.service

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{
  WebSocketRequest,
  Message => WSMessage
}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import akka.stream.{
  ActorMaterializer,
  OverflowStrategy
}
import alpaca.dto.streaming.Alpaca.{
  AlpacaAccountUpdate,
  AlpacaAckMessage,
  AlpacaAuthorizationMessage,
  AlpacaListenMessage,
  AlpacaStreamMessage,
  AlpacaTradeUpdate,
  Stream
}
import alpaca.dto.streaming.Polygon._
import io.circe.generic.auto._
import io.circe.{DecodingFailure, Json}

import scala.concurrent.Future

class StreamingService {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def decodePolygonMessage(json: Json,
                           polygonStreamBasicMessage: PolygonStreamBasicMessage)
    : Either[DecodingFailure, List[PolygonStreamMessage]] = {
    polygonStreamBasicMessage.ev match {
      case Ev.T      => json.as[List[PolygonStreamTradeMessage]]
      case Ev.Q      => json.as[List[PolygonStreamQuoteMessage]]
      case Ev.A      => json.as[List[PolygonStreamAggregatePerSecond]]
      case Ev.AM     => json.as[List[PolygonStreamAggregatePerMinute]]
      case Ev.status => json.as[List[PolygonStreamAuthenticationMessage]]
    }

  }

  def decodeAlpacaMessage(json: Json, alpacaAckMessage: AlpacaAckMessage)
    : Either[DecodingFailure, AlpacaStreamMessage] = {
    alpacaAckMessage.stream match {
      case Stream.account_updates => json.as[AlpacaAccountUpdate]
      case Stream.trade_updates   => json.as[AlpacaTradeUpdate]
      case Stream.listening       => json.as[AlpacaListenMessage]
      case Stream.authorization   => json.as[AlpacaAuthorizationMessage]
    }
  }

  def createClientSource(wsUrl: String, flow: Sink[WSMessage, Future[Done]])
    : SourceQueueWithComplete[WSMessage] = {
    Source
      .queue[WSMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .via(Http().webSocketClientFlow(WebSocketRequest(wsUrl)))
      .to(flow)
      .run()
  }
}
