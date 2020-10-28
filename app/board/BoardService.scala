package board

import board.model.BoardStatus.BoardStatus
import board.model.{Board, BoardConfiguration, PlayerMove}
import scala.concurrent.Future

class BoardService {

  def create(boardConfiguration: BoardConfiguration): Future[Board] = ???

  def retrieveAll(): Future[Seq[Board]] = ???

  def retrieve(boardUid: String): Future[Board] = ???

  def move(boardUid: String, playerMove: PlayerMove): Future[Board] = ???

  def changeStatus(boardUid: String, status: BoardStatus): Future[Board] = ???
}
