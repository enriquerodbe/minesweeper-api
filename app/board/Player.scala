package board

import akka.actor.Actor
import board.Player._
import board.model.BoardException.BoardUidNotFound
import board.model.BoardStatus.BoardStatus
import board.model._
import scala.util.{Failure, Success, Try}

class Player extends Actor {

  var boards = Map.empty[String, Board]

  override def receive: Receive = {
    case Initialize(config) =>
      handleBoardResult(Success(config.generateRandomBoard()))

    case Move(uid, playerMove) =>
      val newBoard = getBoard(uid).flatMap(_.tryMakeMove(playerMove))
      handleBoardResult(newBoard)

    case ChangeStatus(uid, newStatus) =>
      val newBoard = getBoard(uid).map(_.copy(status = newStatus))
      handleBoardResult(newBoard)

    case Retrieve(uid) =>
      handleBoardResult(getBoard(uid))

    case RetrieveAll =>
      sender() ! boards.values.toSeq
  }

  private def getBoard(uid: String): Try[Board] = {
    boards.get(uid).fold[Try[Board]](Failure(BoardUidNotFound(uid)))(Success(_))
  }

  private def handleBoardResult(newBoard: Try[Board]): Unit = {
    newBoard match {
      case Success(board) =>
        boards += board.uid -> board
        sender() ! board
      case Failure(ex) =>
        sender() ! akka.actor.Status.Failure(ex)
    }
  }
}

object Player {

  sealed trait Command
  case class Initialize(config: BoardConfiguration) extends Command
  case class Move(boardUid: String, movement: PlayerMove) extends Command
  case class ChangeStatus(boardUid: String, newStatus: BoardStatus) extends Command
  case class Retrieve(boardUid: String) extends Command
  case object RetrieveAll extends Command
}
