package error

import auth.model.AuthException.UserAlreadyExists
import game.model.BoardException
import game.model.BoardException.BoardUidNotFound
import javax.inject.Inject
import play.api.http.JsonHttpErrorHandler
import play.api.mvc.{RequestHeader, Result}
import play.api.{Environment, OptionalSourceMapper}
import play.mvc.Http.Status
import scala.concurrent.Future

class GlobalErrorHandler @Inject()(
    environment: Environment,
    optionalSourceMapper: OptionalSourceMapper)
  extends JsonHttpErrorHandler(environment, optionalSourceMapper) {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case ex: BoardUidNotFound => onClientError(request, Status.NOT_FOUND, ex.getMessage)
      case ex: BoardException => onClientError(request, Status.BAD_REQUEST, ex.getMessage)
      case ex: UserAlreadyExists => onClientError(request, Status.CONFLICT, ex.getMessage)
      case throwable => super.onServerError(request, throwable)
    }
  }
}
