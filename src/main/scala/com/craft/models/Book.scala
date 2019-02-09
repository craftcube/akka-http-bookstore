package com.craft.models

import java.sql.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.craft.services.DatabaseService

import spray.json.DefaultJsonProtocol

// Our Book model
case class Book(
                 id: Option[Long] = None,
                 title: String,
                 releaseDate: Date,
                 categoryId: Long,
                 quantity: Int,
                 price: Double,
                 author: String
               )

// JSON format for our Book model, to convert to and from JSON
trait BookJson extends SprayJsonSupport with DefaultJsonProtocol {
  import com.craft.services.FormatService._

  implicit val bookFormat = jsonFormat7(Book.apply)

}

// Slick table mapped to our Book model
trait BookTable {
  val databaseService: DatabaseService
  import databaseService.driver.api._

  class Books(tag: Tag) extends Table[Book](tag, "books") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def releaseDate = column[Date]("release_date")
    def categoryId = column[Long]("category_id")
    def quantity = column[Int]("quantity")
    def price = column[Double]("price_usd")
    def author = column[String]("author")

    def * = (id, title, releaseDate, categoryId, quantity, price, author) <> ((Book.apply _).tupled, Book.unapply)
  }


  protected val books = TableQuery[Books]

}