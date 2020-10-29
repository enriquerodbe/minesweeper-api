package game

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import game.BoardSerializers._
import game.model.{Board, BoardConfiguration, PlayerMove}
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.Json.toJson
import play.api.mvc.{BaseController, ControllerComponents}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class GameController @Inject()(
    val controllerComponents: ControllerComponents,
    @Named("gameService") gameService: ActorRef)(
    implicit ec: ExecutionContext)
  extends BaseController {

  implicit val timeout: Timeout = 2.seconds
  val FixedOwnerId = "test"

  def createBoard() = Action.async(parse.json[BoardConfiguration]) { request =>
    val futureResponse = gameService ? GameService.CreateBoard(FixedOwnerId, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardSummaryWrites)).map(Created(_))
  }

  def retrieveAllBoards() = Action.async {
    val futureResponse = gameService ? GameService.RetrieveAllBoards(FixedOwnerId)
    futureResponse.mapTo[Seq[Board]].map(toJson(_)(bardSummarySeqWrites)).map(Ok(_))
  }

  def retrieveBoard(boardUid: String) = Action.async {
    val futureResponse = gameService ? GameService.RetrieveBoard(FixedOwnerId, boardUid)
    futureResponse.mapTo[Board].map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def move(boardUid: String) = Action.async(parse.json[PlayerMove]) { request =>
    val futureResponse = gameService ? GameService.Move(FixedOwnerId, boardUid, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def setBoardIsActive(boardUid: String) = Action.async(parse.json[Boolean]) { request =>
    val futureResponse = gameService ? GameService.SetIsActive(FixedOwnerId, boardUid, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardSummaryWrites)).map(Ok(_))
  }
}
