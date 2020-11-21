package alpaca.client

import akka.NotUsed
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import alpaca.dto.streaming.{Alpaca, ClientStreamMessage, Polygon, StreamMessage, StreamingMessage}

class StreamingClient(polygonStreamingClient: PolygonStreamingClient,
                      alpacaStreamingClient: AlpacaStreamingClient) {

  def subscribe(list: ClientStreamMessage*):
    Seq[(SourceQueueWithComplete[StreamMessage], Source[StreamMessage, NotUsed])] = {
    list.map {
      case message: Polygon.PolygonClientStreamMessage =>
        polygonStreamingClient.subscribe(message)
      case message: Alpaca.AlpacaClientStreamMessage =>
        alpacaStreamingClient.subscribe(message)
    }
  }

  def sub(list: List[String]): Map[String,
                                   (SourceQueueWithComplete[StreamingMessage],
                                    Source[StreamingMessage, NotUsed])] = {
    null

  }
}
