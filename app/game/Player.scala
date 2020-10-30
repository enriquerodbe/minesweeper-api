package game

import akka.actor.Props
import akka.persistence.PersistentActor
import auth.model.Email
import game.Player._
import game.model.BoardException.BoardUidNotFound
import game.model._
import scala.util.{Failure, Success, Try}

class Player(player: Email) extends PersistentActor {

  var boards = Map.empty[BoardUid, Board]

  override val persistenceId: String = player.value

  override def receiveCommand: Receive = {
    case CreateBoard(config) =>
      updateState(BoardCreated(config.generateRandomBoard()))

    case Move(uid, playerMove) =>
      updateState(Moved(uid, playerMove))

    case SetIsActive(uid, isActive) =>
      updateState(IsActiveSet(uid, isActive))

    case RetrieveBoard(uid) =>
      getBoard(uid) match {
        case Success(board) => sender() ! board
        case Failure(ex) => sender() ! akka.actor.Status.Failure(ex)
      }

    case RetrieveAllBoards =>
      sender() ! boards.values.toSeq
  }

  private def updateState(event: Event): Unit = {
    updateStateRecover(event) match {
      case Success(board) => persist(event)(_ => sender() ! board)
      case Failure(ex) => sender() ! akka.actor.Status.Failure(ex)
    }
  }

  private def updateStateRecover(event: Event): Try[Board] = {
    val newBoard = event match {
      case BoardCreated(board) =>
        Success(board)
      case Moved(boardUid, movement) =>
        getBoard(boardUid).flatMap(_.tryMakeMove(movement))
      case IsActiveSet(boardUid, newStatus) =>
        getBoard(boardUid).map(_.activeSet(newStatus))
    }
    newBoard.foreach(b => boards += b.uid -> b)
    newBoard
  }

  private def getBoard(uid: BoardUid): Try[Board] = {
    boards.get(uid).fold[Try[Board]](Failure(BoardUidNotFound(uid)))(Success(_))
  }

  override def receiveRecover: Receive = {
    case event: Event => val _ = updateStateRecover(event)
  }
}

sealed trait Event

object Player {

  def props(player: Email): Props = Props(new Player(player))

  sealed trait Command
  case class CreateBoard(config: BoardConfiguration) extends Command
  case class Move(boardUid: BoardUid, movement: PlayerMove) extends Command
  case class SetIsActive(boardUid: BoardUid, isActive: Boolean) extends Command
  case class RetrieveBoard(boardUid: BoardUid) extends Command
  case object RetrieveAllBoards extends Command

  case class BoardCreated(board: Board) extends Event
  case class Moved(boardUid: BoardUid, movement: PlayerMove) extends Event
  case class IsActiveSet(boardUid: BoardUid, isActive: Boolean) extends Event
}
