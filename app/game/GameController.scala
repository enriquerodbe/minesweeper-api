package game

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import auth.AuthEnv
import com.mohiva.play.silhouette.api.Silhouette
import game.BoardSerializers._
import game.model._
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.Json.toJson
import play.api.libs.json.Writes.seq
import play.api.mvc.{BaseController, ControllerComponents}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class GameController @Inject()(
    val controllerComponents: ControllerComponents,
    silhouette: Silhouette[AuthEnv],
    @Named("gameService") gameService: ActorRef)(
    implicit ec: ExecutionContext)
  extends BaseController {

  import silhouette.SecuredAction
  implicit val timeout: Timeout = 2.seconds

  def createBoard() = SecuredAction.async(parse.json[BoardConfiguration]) { request =>
    val futureResponse = gameService ? GameService.CreateBoard(request.identity, request.body)
    futureResponse.mapTo[Board].map(toJson(_)(boardSummaryWrites)).map(Created(_))
  }

  def retrieveAllBoards() = SecuredAction.async { request =>
    val futureResponse = gameService ? GameService.RetrieveAllBoards(request.identity)
    futureResponse.mapTo[Seq[Board]].map(toJson(_)(seq(boardSummaryWrites))).map(Ok(_))
  }

  def retrieveBoard(boardUid: String) = SecuredAction.async { request =>
    val message = GameService.RetrieveBoard(request.identity, BoardUid(boardUid))
    (gameService ? message).mapTo[Board].map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def move(boardUid: String) = SecuredAction.async(parse.json[PlayerMove]) { request =>
    val message = GameService.Move(request.identity, BoardUid(boardUid), request.body)
    (gameService ? message).mapTo[Board].map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def setBoardIsActive(boardUid: String) = SecuredAction.async(parse.json[Boolean]) { request =>
    val message = GameService.SetIsActive(request.identity, BoardUid(boardUid), request.body)
    (gameService ? message).mapTo[Board].map(toJson(_)(boardSummaryWrites)).map(Ok(_))
  }
}
