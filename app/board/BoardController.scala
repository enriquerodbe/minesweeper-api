package board

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import board.BoardSerializers._
import board.model.BoardStatus.BoardStatus
import board.model.{Board, BoardConfiguration, PlayerMove}
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.Json.toJson
import play.api.mvc.{BaseController, ControllerComponents}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class BoardController @Inject()(
    val controllerComponents: ControllerComponents,
    @Named("boardService") boardService: ActorRef)(
    implicit ec: ExecutionContext)
  extends BaseController {

  implicit val timeout: Timeout = 2.seconds
  val FixedOwnerId = "test"

  def create() = Action.async(parse.json[BoardConfiguration]) { request =>
    val futureResponse = boardService ? BoardService.CreateBoard(FixedOwnerId, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardSummaryWrites)).map(Created(_))
  }

  def retrieveAll() = Action.async {
    val futureResponse = boardService ? BoardService.RetrieveAll(FixedOwnerId)
    futureResponse.mapTo[Seq[Board]].map(toJson(_)(bardSummarySeqWrites)).map(Ok(_))
  }

  def retrieve(boardUid: String) = Action.async {
    val futureResponse = boardService ? BoardService.Retrieve(FixedOwnerId, boardUid)
    futureResponse.mapTo[Board].map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def move(boardUid: String) = Action.async(parse.json[PlayerMove]) { request =>
    val futureResponse = boardService ? BoardService.Move(FixedOwnerId, boardUid, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def changeStatus(boardUid: String) = Action.async(parse.json[BoardStatus]) { request =>
    val futureResponse = boardService ? BoardService.ChangeStatus(FixedOwnerId, boardUid, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardSummaryWrites)).map(Ok(_))
  }
}
