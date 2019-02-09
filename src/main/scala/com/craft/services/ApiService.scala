package com.craft.services

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.craft.controllers.{AuthController, BookController, CategoryController, UserController,BookViewSearchController}
import com.craft.repositories.{AuthRepository, BookRepository, CategoryRepository, UserRepository}

import scala.concurrent.ExecutionContext


class ApiService(
                  categoryRepository: CategoryRepository,
                  bookRepository: BookRepository,
                  authRepository: AuthRepository,
                  userRepository: UserRepository,
                  tokenService: TokenService
                )(implicit executor: ExecutionContext, as: ActorSystem, mat: Materializer) {

  val categoryController = new CategoryController(categoryRepository)
  val bookController = new BookController(bookRepository, tokenService)
  val authController = new AuthController(authRepository, tokenService)
  val userController = new UserController(userRepository, tokenService)

  val bookViewSearchController = new BookViewSearchController(categoryRepository, bookRepository)

  def routes =
  // Add a new route
    pathPrefix("books") {
      bookViewSearchController.routes
    } ~
      pathPrefix("api") {
        categoryController.routes ~
          bookController.routes ~
          authController.routes ~
          userController.routes
      }

}
