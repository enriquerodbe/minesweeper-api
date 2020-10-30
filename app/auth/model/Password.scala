package auth.model

case class Password(email: String, hasher: String, hash: String, salt: Option[String])
