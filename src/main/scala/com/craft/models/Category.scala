package com.craft.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.craft.services.DatabaseService
import spray.json.DefaultJsonProtocol

// Our Category model
case class Category(id: Option[Long] = None, title: String)

// JSON format for our Category model, to convert to and from JSON
trait CategoryJson extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val categoryFormat = jsonFormat2(Category.apply)
}

// Slick table mapped to our Category model
trait CategoryTable {
  val databaseService: DatabaseService

  import databaseService.driver.api._

  class Categories(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")

    def * = (id, title) <> ((Category.apply _).tupled, Category.unapply)
  }

  protected val categories = TableQuery[Categories]
}