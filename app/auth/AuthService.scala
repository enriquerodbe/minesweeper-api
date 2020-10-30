package auth

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import auth.model.{Email, Password}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@Singleton
class AuthService @Inject()(
    @Named("passwordDao") passwordDao: ActorRef)(
    implicit ec: ExecutionContext)
  extends IdentityService[Email] with DelegableAuthInfoDAO[PasswordInfo] {

  implicit val timeout: Timeout = 2.seconds

  override val classTag: ClassTag[PasswordInfo] = scala.reflect.classTag[PasswordInfo]

  override def retrieve(loginInfo: LoginInfo): Future[Option[Email]] = {
    (passwordDao ? PasswordDao.Find(loginInfo.providerKey))
      .mapTo[Option[Password]]
      .map(_.map(password => Email(password.email)))
  }

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    (passwordDao ? PasswordDao.Find(loginInfo.providerKey))
      .mapTo[Option[Password]]
      .map(_.map(password => PasswordInfo(password.hasher, password.hash, password.salt)))
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val password = Password(loginInfo.providerKey, authInfo.hasher, authInfo.password, authInfo.salt)
    (passwordDao ? PasswordDao.Save(password))
      .mapTo[PasswordDao.PasswordSaved]
      .map(p => PasswordInfo(p.password.hasher, p.password.hash, p.password.salt))
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    Future.failed(new UnsupportedOperationException)
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    Future.failed(new UnsupportedOperationException)
  }
}
