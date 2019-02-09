package com.craft.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.craft.directives.VerifyToken
import com.craft.models.{UserJson, User}
import com.craft.repositories.UserRepository
import com.craft.services.TokenService

import scala.concurrent.ExecutionContext

// Add a `TokenService` and an implicit `ExecutionContext` to comply with our `VerifyToken` trait
class UserController(val userRepository: UserRepository, val tokenService: TokenService)(implicit val ec: ExecutionContext)
  extends UserJson
  // Add the `VerifyToken` trait
  with VerifyToken {

  val routes = pathPrefix("users") {
    pathEndOrSingleSlash {
      post {
          decodeRequest {
            entity(as[User]) { user =>
              onSuccess(userRepository.findByEmail(user.email)) {
                case Some(_) => complete(StatusCodes.BadRequest, "Email already exists.")
                case None => complete(StatusCodes.Created, userRepository.create(user))
              }
            }
          }
        }
    }  ~
      pathPrefix(IntNumber) { id =>
        pathEndOrSingleSlash {
          // Add our directive for security
          verifyTokenUser(id) { user =>
            get {
              // Up to this point we know the user provided by `verifyTokenUser` exists, so we just return it
              complete(user)
            }
          }
        }
      }
  }

}
