package user

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  def register() = TODO

  def login() = TODO
}
