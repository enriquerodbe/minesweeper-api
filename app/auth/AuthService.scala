package auth

import auth.model.Credentials
import scala.concurrent.Future

class AuthService {

  def register(credentials: Credentials): Future[Unit] = ???

  def login(credentials: Credentials): Future[Unit] = ???
}
