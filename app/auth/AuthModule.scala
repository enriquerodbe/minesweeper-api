package auth

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers.BasicAuthProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.AkkaGuiceSupport
import scala.concurrent.ExecutionContext.Implicits.global

class AuthModule extends AbstractModule with AkkaGuiceSupport with ScalaModule {

  override def configure(): Unit = {
    bind[Silhouette[AuthEnv]].to[SilhouetteProvider[AuthEnv]]

    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)

    bind[RequestProvider].to[BasicAuthProvider].asEagerSingleton()

    bind[AuthenticatorService[JWTAuthenticator]].to[JWTAuthenticatorService].asEagerSingleton()

    bind[DelegableAuthInfoDAO[PasswordInfo]].to[AuthService].asEagerSingleton()

    bindActor[PasswordDao]("passwordDao")
  }

  @Provides
  def provideEnvironment(
      userService: AuthService,
      authenticatorService: AuthenticatorService[JWTAuthenticator],
      requestProvider: RequestProvider,
      eventBus: EventBus): Environment[AuthEnv] = {
    Environment[AuthEnv](
      userService, authenticatorService, Seq(requestProvider), eventBus)
  }

  @Provides
  def provideAuthInfoRepository(
      passwordDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordDao)
  }

  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    PasswordHasherRegistry(passwordHasher)
  }

  @Provides
  def provideJwtAuthenticatorSettings(configuration: Configuration): JWTAuthenticatorSettings = {
    JWTAuthenticatorSettings(sharedSecret = configuration.get[String]("play.http.secret.key"))
  }

  @Provides
  def provideJwtAuthenticatorService(
      settings: JWTAuthenticatorSettings): JWTAuthenticatorService = {
    new JWTAuthenticatorService(
      settings,
      None,
      new Base64AuthenticatorEncoder,
      new SecureRandomIDGenerator(),
      Clock(),
    )
  }
}
