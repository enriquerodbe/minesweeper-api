package board

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

@Singleton
class BoardController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  def create() = TODO

  def retrieveAll() = TODO

  def retrieve(boardId: String) = TODO

  def move(boardId: String) = TODO

  def changeStatus(boardId: String) = TODO
}
