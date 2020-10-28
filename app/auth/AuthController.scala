package auth

import auth.AuthSerializers._
import auth.model.Credentials
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}
import scala.concurrent.ExecutionContext

@Singleton
class AuthController @Inject()(
    val controllerComponents: ControllerComponents,
    authService: AuthService)(
    implicit ec: ExecutionContext)
  extends BaseController {

  def register() = Action.async(parse.json[Credentials]) { request =>
    authService.register(request.body).map(_ => NoContent)
  }

  def login() = Action.async(parse.json[Credentials]) { request =>
    authService.login(request.body).map(_ => NoContent)
  }
}
