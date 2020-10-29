package game

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

trait BaseControllerTest extends PlaySpec with GuiceOneAppPerSuite {
  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure("akka.persistence.journal.plugin" -> "akka.persistence.journal.inmem")
      .build()
  }
}
