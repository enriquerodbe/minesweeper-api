package auth

import auth.AuthSerializers._
import auth.model.AuthException.UserAlreadyExists
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo, Credentials => SilhouetteCredentials}
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

@Singleton
class AuthController @Inject()(
    val controllerComponents: ControllerComponents,
    silhouette: Silhouette[AuthEnv],
    authInfoDao: DelegableAuthInfoDAO[PasswordInfo],
    authenticatorService: AuthenticatorService[JWTAuthenticator],
    credentialsProvider: CredentialsProvider,
    hasher: PasswordHasher)(
    implicit ec: ExecutionContext)
  extends BaseController {

  def register() = silhouette.UnsecuredAction.async(parse.json[Credentials]) { implicit request =>
    credentialsProvider
      .authenticate(SilhouetteCredentials(request.body.email, request.body.password))
      .transformWith {
        case Failure(_: IdentityNotFoundException) => doRegister(request.body)
        case _ => Future.failed(UserAlreadyExists(request.body.email))
      }
  }

  private def doRegister(credentials: Credentials)(implicit req: RequestHeader): Future[Result] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, credentials.email)
    for {
      _ <- authInfoDao.save(loginInfo, hasher.hash(credentials.password))
      authenticator <- authenticatorService.create(loginInfo)
      token <- authenticatorService.init(authenticator)
      result <- authenticatorService.embed(token, Ok(Json.obj("token" -> token)))
    } yield result
  }

  def login() = silhouette.UnsecuredAction.async(parse.json[Credentials]) { implicit request =>
    val Credentials(email, password) = request.body
    val result = for {
      loginInfo <- credentialsProvider.authenticate(SilhouetteCredentials(email, password))
      authenticator <- authenticatorService.create(loginInfo)
      token <- authenticatorService.init(authenticator)
      result <- authenticatorService.embed(token, Ok(Json.obj("token" -> token)))
    } yield result

    result.recover {
      case _: ProviderException => Forbidden
    }
  }
}
