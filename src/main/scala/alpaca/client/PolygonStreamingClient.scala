package alpaca.client

import akka.http.scaladsl.model.ws.{TextMessage, Message => WSMessage}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import akka.{Done, NotUsed}
import alpaca.dto.streaming.Polygon._
import alpaca.dto.streaming.StreamMessage
import alpaca.service.{ConfigService, StreamingService}
import cats.implicits._
import com.typesafe.scalalogging.Logger
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class PolygonStreamingClient(configService: ConfigService, streamingService: StreamingService)
    extends BaseStreamingClient {

  private val logger: Logger = Logger(classOf[PolygonStreamingClient])

  private val messageList = scala.collection.mutable.ListBuffer.empty[PolygonClientStreamMessage]

  val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: TextMessage.Strict =>
        for {
          parsedJson <- parse(message.text)
          polygonStreamMessage <- parsedJson
            .as[List[PolygonStreamBasicMessage]]
          decodeBasicMessage <- streamingService.decodePolygonMessage(
            parsedJson,
            polygonStreamMessage.head)
          _ <- checkAuthentication(decodeBasicMessage)
          _ <- offerMessage(decodeBasicMessage)
        } yield decodeBasicMessage

      case x@_ => logger.warn(s"Not sure how to deal with WSMessage: $x")
    }

  val clientSource: SourceQueueWithComplete[WSMessage] = streamingService.createClientSource(wsUrl, incoming)

  authPromise.future.onComplete {
    case Failure(exception) =>
      logger.error(exception.toString)
    case Success(_) =>
      messageList.foreach(msg => {
        logger.debug(msg.toString)
        clientSource.offer(TextMessage(msg.asJson.noSpaces))
      })
  }

  private def checkAuthentication(message: List[PolygonStreamMessage])
  : Either[String, List[PolygonStreamMessage]] = {
    if (!authPromise.isCompleted) {
      message.head match {
        case polygonStreamAuthenticationMessage: PolygonStreamAuthenticationMessage =>
          if (polygonStreamAuthenticationMessage.status.equalsIgnoreCase(
            "auth_success")) {
            authPromise.completeWith(Future.successful(true))
          }
        case _ =>
      }
    }
    message.asRight
  }

  def subscribe(subject: PolygonClientStreamMessage):
    (SourceQueueWithComplete[StreamMessage], Source[StreamMessage, NotUsed]) = {
    if (authPromise.isCompleted) {
      clientSource.offer(TextMessage(subject.asJson.noSpaces))
    } else {
      clientSource.offer(TextMessage(PolygonAuthMessage(
        configService.getConfig.accountKey).asJson.noSpaces))
      messageList += subject
    }

    source
  }
}
