package com.craft.services

import java.sql.Date

import com.craft.models.{Book, Category, User}
import slick.jdbc.JdbcProfile

trait DatabaseService {

  val driver: JdbcProfile

  import driver.api._

  val db: Database

}