package auth

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Reads.{email, minLength}
import play.api.libs.json.{Reads, __}

object AuthSerializers {

  case class Credentials(email: String, password: String)

  implicit val credentialsReads: Reads[Credentials] = (
    (__ \ "email").read[String](email) and
    (__ \ "password").read[String](minLength[String](4))
  )(Credentials.apply _)
}
