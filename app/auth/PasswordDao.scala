package auth

import akka.persistence.PersistentActor
import auth.PasswordDao.{Find, PasswordSaved, Save}
import auth.model.AuthException.PasswordAlreadyExists
import auth.model.Password

class PasswordDao extends PersistentActor {

  var passwords = Map.empty[String, Password]

  override def persistenceId: String = "passwords"

  override def receiveCommand: Receive = {
    case Save(password) if passwords.contains(password.email) =>
      sender() ! akka.actor.Status.Failure(PasswordAlreadyExists(password))

    case Save(password) =>
      persist(PasswordSaved(password)) { event =>
        savePassword(password)
        sender() ! event
      }

    case Find(email) =>
      sender() ! passwords.get(email)
  }

  override def receiveRecover: Receive = {
    case PasswordSaved(password) => savePassword(password)
  }

  private def savePassword(password: Password): Unit = {
    passwords += password.email -> password
  }
}

sealed trait Event

object PasswordDao {

  sealed trait Command
  case class Save(password: Password) extends Command
  case class Find(email: String) extends Command

  case class PasswordSaved(password: Password) extends Event
}
