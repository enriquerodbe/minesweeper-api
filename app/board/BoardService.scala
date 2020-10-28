package board

import akka.actor.{Actor, ActorRef, Props}
import board.BoardService._
import board.model.BoardStatus.BoardStatus
import board.model.{BoardConfiguration, PlayerMove}
import javax.inject.Inject

class BoardService @Inject() extends Actor {

  override def receive: Receive = {
    case CreateBoard(ownerUid, config) =>
      getOrCreate(ownerUid).tell(BoardActor.Initialize(config), sender())

    case RetrieveAll(ownerUid) =>
      getOrCreate(ownerUid).tell(BoardActor.RetrieveAll, sender())

    case Retrieve(ownerUid, boardUid) =>
      getOrCreate(ownerUid).tell(BoardActor.Retrieve(boardUid), sender())

    case Move(ownerUid, boardUid, move) =>
      getOrCreate(ownerUid).tell(BoardActor.Move(boardUid, move), sender())

    case ChangeStatus(ownerUid, boardUid, newStatus) =>
      getOrCreate(ownerUid).tell(BoardActor.ChangeStatus(boardUid, newStatus), sender())
  }

  private def getOrCreate(ownerUid: String): ActorRef = {
    val name = makeActorName(ownerUid)
    context.child(name).getOrElse(context.actorOf(Props(new BoardActor), name))
  }

  private def makeActorName(ownerUid: String): String = s"player-$ownerUid"
}

object BoardService {

  sealed trait Command
  case class CreateBoard(ownerUid: String, config: BoardConfiguration) extends Command
  case class RetrieveAll(ownerUid: String) extends Command
  case class Retrieve(ownerUid: String, boardUid: String) extends Command
  case class Move(ownerUid: String, boardUid: String, move: PlayerMove) extends Command
  case class ChangeStatus(ownerUid: String, boardUid: String, newStatus: BoardStatus) extends Command
}
