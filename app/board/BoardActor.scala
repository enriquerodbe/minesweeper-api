package board

import akka.actor.Actor
import board.BoardActor._
import board.model.BoardStatus.BoardStatus
import board.model._

class BoardActor extends Actor {

  var boards = Map.empty[String, Board]

  override def receive: Receive = {
    case Initialize(config) =>
      val newBoard = config.generateRandomBoard()
      updateBoards(newBoard)
      sender() ! newBoard

    case Move(uid, PlayerMove(PlayerMoveType.Reveal, coordinates)) =>
      updateAndRespond(uid, _.revealed(coordinates))

    case Move(uid, PlayerMove(PlayerMoveType.RedFlag, coordinates)) =>
      updateAndRespond(uid, _.redFlagged(coordinates))

    case Move(uid, PlayerMove(PlayerMoveType.QuestionMark, coordinates)) =>
      updateAndRespond(uid, _.questionMarked(coordinates))

    case Move(uid, PlayerMove(PlayerMoveType.ClearFlag, coordinates)) =>
      updateAndRespond(uid, _.flagCleared(coordinates))

    case ChangeStatus(uid, newStatus) =>
      updateAndRespond(uid, _.copy(status = newStatus))

    case Retrieve(uid) =>
      updateAndRespond(uid, identity)

    case RetrieveAll =>
      sender() ! boards.values.toSeq
  }

  private def updateBoards(newBoard: Board): Unit = {
    boards += newBoard.uid -> newBoard
  }

  private def updateAndRespond(uid: String, updateFunction: Board => Board): Unit = {
    val maybeUpdatedBoard = boards.get(uid).map(updateFunction)
    maybeUpdatedBoard.foreach(updateBoards)
    sender() ! maybeUpdatedBoard
  }
}

object BoardActor {

  sealed trait Command
  case class Initialize(config: BoardConfiguration) extends Command
  case class Move(boardUid: String, movement: PlayerMove) extends Command
  case class ChangeStatus(boardUid: String, newStatus: BoardStatus) extends Command
  case class Retrieve(boardUid: String) extends Command
  case object RetrieveAll extends Command
}
