package auth.model

abstract class AuthException(msg: String) extends Exception(msg)

object AuthException {
  case class UserAlreadyExists(user: String)
    extends AuthException(s"User $user already exists.")

  case class PasswordAlreadyExists(password: Password)
    extends AuthException(s"Password for ${password.email} already exists.")
}
