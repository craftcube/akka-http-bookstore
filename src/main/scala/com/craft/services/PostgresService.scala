package com.craft.services

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.PostgresProfile

class PostgresService(jdbcUrl: String, dbUser: String, dbPassword: String) extends DatabaseService {

  // Setup our database driver, Postgres in this case
  val driver: PostgresProfile = PostgresProfile

  // Create a database connection
//  val db: Database = Database.forURL(jdbcUrl, dbUser, dbPassword)
//  db.createSession()

  // the second way of creating database
  private val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(jdbcUrl)
  hikariConfig.setUsername(dbUser)
  hikariConfig.setPassword(dbPassword)
//  val dataSource = new HikariDataSource(hikariConfig)
  val db = Database.forDataSource(new HikariDataSource(hikariConfig),maxConnections = Some(10))
}