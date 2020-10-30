package auth

import auth.AuthSerializers.Credentials
import auth.model.AuthException.UserAlreadyExists
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.BaseControllerTest

class AuthControllerTest extends BaseControllerTest {

  val controller = app.injector.instanceOf[AuthController]

  "AuthController" must {
    "register a user" in {
      val credentials = Credentials("test@minesweeper.com", "testPassword")

      val result = controller.register()(FakeRequest().withBody(credentials))

      status(result) mustEqual CREATED
      header("X-Auth-Token", result) mustBe defined
    }

    "reject duplicated user" in {
      val credentials = Credentials("dup@minesweeper.com", "dupTestPassword")

      val result = controller.register()(FakeRequest().withBody(credentials))
      status(result) mustEqual CREATED

      intercept[UserAlreadyExists] {
        status(controller.register()(FakeRequest().withBody(credentials)))
      }
    }

    "login an existing user" in {
      val result = controller.login()(FakeRequest().withBody(validCredentials))

      status(result) mustEqual NO_CONTENT
      header("X-Auth-Token", result) mustBe defined
    }

    "forbid an invalid user" in {
      val result = controller.login()(FakeRequest().withBody(Credentials("test", "test")))

      status(result) mustEqual FORBIDDEN
      header("X-Auth-Token", result) mustBe empty
    }
  }
}
