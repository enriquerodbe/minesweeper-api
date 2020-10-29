package board

import akka.actor.{Actor, ActorRef, Props}
import board.BoardService._
import board.model.BoardException._
import board.model.BoardStatus.BoardStatus
import board.model.{BoardConfiguration, PlayerMove}
import javax.inject.Inject
import play.api.Configuration
import scala.util.{Failure, Success, Try}

class BoardService @Inject()(configuration: Configuration) extends Actor {

  val MinBoardSize = configuration.get[Int]("board.config.size.min")
  val MaxBoardSize = configuration.get[Int]("board.config.size.max")
  val MinBoardMines = configuration.get[Int]("board.config.mines.min")

  override def receive: Receive = {
    case CreateBoard(ownerUid, config) =>
      validateBoardConfig(config) match {
        case Success(config) =>
          getOrCreate(ownerUid).tell(Player.Initialize(config), sender())
        case Failure(ex) =>
          sender() ! akka.actor.Status.Failure(ex)
      }

    case RetrieveAll(ownerUid) =>
      getOrCreate(ownerUid).tell(Player.RetrieveAll, sender())

    case Retrieve(ownerUid, boardUid) =>
      getOrCreate(ownerUid).tell(Player.Retrieve(boardUid), sender())

    case Move(ownerUid, boardUid, move) =>
      getOrCreate(ownerUid).tell(Player.Move(boardUid, move), sender())

    case ChangeStatus(ownerUid, boardUid, newStatus) =>
      getOrCreate(ownerUid).tell(Player.ChangeStatus(boardUid, newStatus), sender())
  }

  private def getOrCreate(ownerUid: String): ActorRef = {
    val name = makeActorName(ownerUid)
    context.child(name).getOrElse(context.actorOf(Props(new Player), name))
  }

  private def makeActorName(ownerUid: String): String = s"player-$ownerUid"

  private def validateBoardConfig(config: BoardConfiguration): Try[BoardConfiguration] = {
    if (config.size < MinBoardSize) {
      Failure(BoardSizeTooSmall(config.size, MinBoardSize))
    } else if (config.size > MaxBoardSize) {
      Failure(BoardSizeTooBig(config.size, MaxBoardSize))
    } else if (config.numMines < MinBoardMines) {
      Failure(BoardTooFewMines(config.numMines, MinBoardMines))
    } else if (config.numMines >= config.size) {
      Failure(BoardTooManyMines(config.numMines, config.size))
    }
    else Success(config)
  }
}

object BoardService {

  sealed trait Command
  case class CreateBoard(ownerUid: String, config: BoardConfiguration) extends Command
  case class RetrieveAll(ownerUid: String) extends Command
  case class Retrieve(ownerUid: String, boardUid: String) extends Command
  case class Move(ownerUid: String, boardUid: String, move: PlayerMove) extends Command
  case class ChangeStatus(ownerUid: String, boardUid: String, newStatus: BoardStatus) extends Command
}
