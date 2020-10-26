package user.dto

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

case class UserCredentials(email: String, password: String)

object UserCredentials {

  implicit val userCredentialsReads: Reads[UserCredentials] = (
    (__ \ "email").read[String](email) and
    (__ \ "password").read[String](minLength[String](4))
  )(UserCredentials.apply _)
}
