package auth

import auth.model.Email
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

trait AuthEnv extends Env {
  type I = Email
  type A = JWTAuthenticator
}
