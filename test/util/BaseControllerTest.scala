package util

import auth.AuthController
import auth.AuthSerializers.Credentials
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.header
import play.api.test.{DefaultAwaitTimeout, FakeRequest}

trait BaseControllerTest
  extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterAll with DefaultAwaitTimeout {

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure("akka.persistence.journal.plugin" -> "akka.persistence.journal.inmem")
      .build()
  }

  var testToken = ""
  val validCredentials = Credentials("admin@minesweeper.com", "secretPassword")

  override protected def beforeAll(): Unit = {
    val request = FakeRequest().withBody(validCredentials)
    val response = app.injector.instanceOf[AuthController].register()(request)
    testToken = header("X-Auth-Token", response).get
  }

  def fakeRequest(): FakeRequest[AnyContentAsEmpty.type] = {
    FakeRequest().withHeaders("X-Auth-Token" -> testToken)
  }
}
