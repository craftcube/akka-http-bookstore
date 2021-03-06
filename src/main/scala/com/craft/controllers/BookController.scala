package com.craft.controllers

import java.sql.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.unmarshalling.{PredefinedFromStringUnmarshallers, Unmarshaller}
import com.craft.directives.VerifyToken
import com.craft.models.BookJson
import com.craft.models.BookSearch
import com.craft.models.Book
import com.craft.repositories.BookRepository
import com.craft.services.TokenService

import scala.concurrent.ExecutionContext


class BookController(val bookRepository: BookRepository, val tokenService: TokenService)(implicit val ec: ExecutionContext) extends BookJson
  with PredefinedFromStringUnmarshallers
  with VerifyToken {

  implicit val dateFromStringUnmarshaller: Unmarshaller[String, Date] =
    Unmarshaller.strict[String, Date] { string =>
      Date.valueOf(string)
    }

  val routes = pathPrefix("books") {
    pathEndOrSingleSlash {
      post {
        // From it's documentation: Decompresses the incoming request if it is `gzip` or `deflate` compressed. Uncompressed requests are passed through untouched.
        decodeRequest {
          // Parses the request as the given entity, in this case a `Book`
          entity(as[Book]) { book =>
            println(s"BOOK: $book")
            complete(StatusCodes.Created, bookRepository.create(book))
          }
        }
      } ~
        get {
          parameters(('title.?, 'releaseDate.as[Date].?, 'categoryId.as[Long].?, 'author.?))
            .as(BookSearch) { bookSearch: BookSearch =>
              complete {
                bookRepository.search(bookSearch)
              }
            }
        }
    } ~
      // After the `/books/` we expect the id of the book we want to delete
      pathPrefix(IntNumber) { id =>
        // We want to listen to paths like `/books/id` or `/books/id/`
        pathEndOrSingleSlash {
          get {
            verifyToken { user =>
              complete {
                bookRepository.findById(id)
              }
            }
          } ~
            delete {
              onSuccess(bookRepository.delete(id)) {
                // If we get a number higher than 0, it deleted the book
                case n if n > 0 => complete(StatusCodes.NoContent)
                // If 0 is returned, the book wasn't found
                case _ => complete(StatusCodes.NotFound)
              }
            }
        }
      }
  }

}
