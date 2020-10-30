package game

import akka.actor.{Actor, ActorRef, Status}
import auth.model.Email
import game.GameService._
import game.model.BoardException._
import game.model.{BoardConfiguration, BoardUid, PlayerMove}
import javax.inject.Inject
import play.api.Configuration
import scala.util.{Failure, Success, Try}

class GameService @Inject()(configuration: Configuration) extends Actor {

  val MinBoardSize = configuration.get[Int]("board.config.size.min")
  val MaxBoardSize = configuration.get[Int]("board.config.size.max")
  val MinBoardMines = configuration.get[Int]("board.config.mines.min")

  override def receive: Receive = {
    case CreateBoard(player, config) =>
      validateBoardConfig(config) match {
        case Success(config) =>
          getOrCreate(player).tell(Player.CreateBoard(config), sender())
        case Failure(ex) =>
          sender() ! Status.Failure(ex)
      }

    case RetrieveAllBoards(player) =>
      getOrCreate(player).tell(Player.RetrieveAllBoards, sender())

    case RetrieveBoard(player, boardUid) =>
      getOrCreate(player).tell(Player.RetrieveBoard(boardUid), sender())

    case Move(player, boardUid, move) =>
      getOrCreate(player).tell(Player.Move(boardUid, move), sender())

    case SetIsActive(player, boardUid, newStatus) =>
      getOrCreate(player).tell(Player.SetIsActive(boardUid, newStatus), sender())
  }

  private def getOrCreate(player: Email): ActorRef = {
    val name = makeActorName(player)
    context.child(name).getOrElse(context.actorOf(Player.props(player), name))
  }

  private def makeActorName(player: Email): String = s"${player.value}"

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
  case class CreateBoard(player: Email, config: BoardConfiguration) extends Command
  case class RetrieveAllBoards(player: Email) extends Command
  case class RetrieveBoard(player: Email, boardUid: BoardUid) extends Command
  case class Move(player: Email, boardUid: BoardUid, move: PlayerMove) extends Command
  case class SetIsActive(player: Email, boardUid: BoardUid, isActive: Boolean) extends Command
}
