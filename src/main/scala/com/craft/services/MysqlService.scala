package com.craft.services

import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
class MysqlService (jdbcUrl: String, dbUser: String, dbPassword: String) extends DatabaseService {

  // Setup our database driver, Postgres in this case
  val driver: MySQLProfile = MySQLProfile

  // Create a database connection
  val db: Database = Database.forURL(jdbcUrl, dbUser, dbPassword)
  db.createSession()
}