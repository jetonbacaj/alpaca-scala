package alpaca.client

import akka.http.scaladsl.model.ws.{BinaryMessage, TextMessage, Message => WSMessage}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import akka.{Done, NotUsed}
import alpaca.dto.streaming.Alpaca._
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

class AlpacaStreamingClient(configService: ConfigService,
                            streamingService: StreamingService) extends BaseStreamingClient {

  private val logger: Logger = Logger(classOf[AlpacaStreamingClient])

  private val messageList = scala.collection.mutable.ListBuffer.empty[AlpacaClientStreamMessage]

  override def wsUrl: String =
    configService.getConfig.base_url
      .replace("https", "wss")
      .replace("http", "wss") + "/stream"

  private val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: BinaryMessage.Strict =>
        val msg = message.data.utf8String
        logger.info(msg)
        for {
          parsed <- parse(message.data.utf8String)
          alpacaMessage <- parsed.as[AlpacaAckMessage]
          decodedMessage <- streamingService.decodeAlpacaMessage(parsed,
            alpacaMessage)
          _ <- checkAuthentication(List(decodedMessage))
          _ <- offerMessage(List(decodedMessage))
        } yield decodedMessage

      case message: TextMessage.Strict =>
        logger.info(message.toString())

      case x@_ => logger.warn(s"Not sure how to deal with WSMessage: $x")
    }

  private val clientSource: SourceQueueWithComplete[WSMessage] = streamingService.createClientSource(wsUrl, incoming)

  authPromise.future.onComplete {
    case Failure(exception) =>
      logger.error(exception.toString)
    case Success(_) =>
      messageList.foreach(msg => {
        import alpaca.dto.streaming.Alpaca._
        logger.debug(msg.toString)
        clientSource.offer(TextMessage(msg.asJson.noSpaces))
      })
  }

  private def checkAuthentication(message: List[AlpacaStreamMessage]): Either[String, List[AlpacaStreamMessage]] = {
    if (!authPromise.isCompleted) {
      message.head match {
        case polygonStreamAuthenticationMessage: AlpacaAuthorizationMessage =>
          if (polygonStreamAuthenticationMessage.data.status.equalsIgnoreCase(
            "authorized")) {
            authPromise.completeWith(Future.successful(true))
          }
        case _ =>
      }
    }
    message.asRight
  }

  def subscribe(subject: AlpacaClientStreamMessage): (SourceQueueWithComplete[StreamMessage], Source[StreamMessage, NotUsed]) = {

    if (authPromise.isCompleted) {
      clientSource.offer(TextMessage(subject.asJson.noSpaces))
    } else {
      val str = AlpacaAuthenticate(
        configService.getConfig.accountKey,
        configService.getConfig.accountSecret).asJson.noSpaces
      clientSource.offer(TextMessage(str))
      messageList += subject
    }

    source
  }
}
