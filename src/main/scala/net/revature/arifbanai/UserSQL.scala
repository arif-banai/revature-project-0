package net.revature.arifbanai

import scala.util.Using

object UserSQL {

  def getKey(username: String): Int = {

    var key = -1

    Using.Manager { use =>
      val validateLoginStatement = use(DataSource.getConnection
        .prepareStatement("SELECT id FROM users WHERE username = ?;"))
      validateLoginStatement.setString(1, username)
      val resultSet = use(validateLoginStatement.executeQuery())

      // Should return exactly one password given some username, as usernames are unique
      if (resultSet.next()) {
        key = resultSet.getInt("id")
      }
    }

    key
  }

  def checkBalance(userKey: Int): Long = {

    var balance = -1L

    Using.Manager { use =>
      val validateLoginStatement = use(DataSource.getConnection
        .prepareStatement("SELECT balance FROM accounts WHERE userid = ?;"))
      validateLoginStatement.setInt(1, userKey)
      val resultSet = use(validateLoginStatement.executeQuery())

      // Should return exactly one password given some username, as usernames are unique
      if (resultSet.next()) {
        balance = resultSet.getLong("balance")
      }
    }

    balance
  }

}
