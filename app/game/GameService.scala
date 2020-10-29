package game

import akka.actor.{Actor, ActorRef, Props}
import game.GameService._
import game.model.BoardException._
import game.model.BoardStatus.BoardStatus
import game.model.{BoardConfiguration, PlayerMove}
import javax.inject.Inject
import play.api.Configuration
import scala.util.{Failure, Success, Try}

class GameService @Inject()(configuration: Configuration) extends Actor {

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

    case RetrieveAllBoards(ownerUid) =>
      getOrCreate(ownerUid).tell(Player.RetrieveAll, sender())

    case RetrieveBoard(ownerUid, boardUid) =>
      getOrCreate(ownerUid).tell(Player.Retrieve(boardUid), sender())

    case Move(ownerUid, boardUid, move) =>
      getOrCreate(ownerUid).tell(Player.Move(boardUid, move), sender())

    case ChangeBoardStatus(ownerUid, boardUid, newStatus) =>
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

object GameService {

  sealed trait Command
  case class CreateBoard(ownerUid: String, config: BoardConfiguration) extends Command
  case class RetrieveAllBoards(ownerUid: String) extends Command
  case class RetrieveBoard(ownerUid: String, boardUid: String) extends Command
  case class Move(ownerUid: String, boardUid: String, move: PlayerMove) extends Command
  case class ChangeBoardStatus(
      ownerUid: String, boardUid: String, newStatus: BoardStatus) extends Command
}
