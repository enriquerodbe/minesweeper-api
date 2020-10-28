package board

import board.BoardSerializers._
import board.model.BoardStatus.BoardStatus
import board.model.{BoardConfiguration, PlayerMove}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json.toJson
import play.api.mvc.{BaseController, ControllerComponents}
import scala.concurrent.ExecutionContext

@Singleton
class BoardController @Inject()(
    val controllerComponents: ControllerComponents,
    boardService: BoardService)(
    implicit ec: ExecutionContext)
  extends BaseController {

  def create() = Action.async(parse.json[BoardConfiguration]) { request =>
    boardService.create(request.body).map(toJson(_)(boardSummaryWrites)).map(Created(_))
  }

  def retrieveAll() = Action.async {
    boardService.retrieveAll().map(toJson(_)(bardSummarySeqWrites)).map(Ok(_))
  }

  def retrieve(boardUid: String) = Action.async {
    boardService.retrieve(boardUid).map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def move(boardUid: String) = Action.async(parse.json[PlayerMove]) { request =>
    boardService.move(boardUid, request.body).map(toJson(_)(boardDetailsWrites)).map(Ok(_))
  }

  def changeStatus(boardUid: String) = Action.async(parse.json[BoardStatus]) { request =>
    boardService.changeStatus(boardUid, request.body).map(toJson(_)(boardSummaryWrites)).map(Ok(_))
  }
}
