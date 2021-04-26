package net.revature.arifbanai

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.{Connection, SQLException}

object DataSource {
  private val config: HikariConfig = new HikariConfig()

  config.setJdbcUrl("jdbc:postgresql://localhost:5440/postgres?currentSchema=smeefybank")
  config.setUsername("postgres")
  config.setPassword("Julia24")
  config.addDataSourceProperty("cachePrepStmts", "true")
  config.addDataSourceProperty("prepStmtCacheSize", "250")
  config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
  private val dataSource = new HikariDataSource(config)

  @throws[SQLException]
  def getConnection: Connection = dataSource.getConnection
}

